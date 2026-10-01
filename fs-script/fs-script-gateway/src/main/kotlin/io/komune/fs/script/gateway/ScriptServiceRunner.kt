package io.komune.fs.script.gateway

import io.komune.fs.script.core.config.properties.FsMigrateProperties
import io.komune.fs.script.core.config.properties.FsRetryProperties
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.service.FsScriptS3Service
import io.komune.fs.script.core.utils.retryOnThrow
import io.komune.fs.script.imports.ImportScript
import io.komune.fs.script.migrate.MigrateScript
import io.komune.fs.script.migrate.MinioMigrationStore
import io.minio.MinioClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Service

class ImportScriptException(message: String) : RuntimeException(message)

class MigrateScriptException(message: String) : RuntimeException(message)

@Service
@EnableConfigurationProperties(FsScriptInitProperties::class, FsRetryProperties::class, FsMigrateProperties::class)
class ScriptServiceRunner(
    private val context: ConfigurableApplicationContext,
    private val fsScriptInitProperties: FsScriptInitProperties,
    private val retryProperties: FsRetryProperties,
    private val migrateProperties: FsMigrateProperties,
    private val fsScriptS3Service: FsScriptS3Service,
    private val minioClient: MinioClient,
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(ScriptServiceRunner::class.java)
    

    override fun run(vararg args: String) = runBlocking {
        try {
            logger.info("Starting FS Script Gateway...")
            if (migrateProperties.enabled) runMigrateScript() else runFileImportScript()
            logger.info("FS Script Gateway completed successfully")
        } catch (e: ImportScriptException) {
            logger.error("FS Script Gateway failed", e)
        } catch (e: MigrateScriptException) {
            logger.error("FS Script Gateway failed", e)
        } finally {
            context.close()
        }
    }

    private suspend fun runFileImportScript() {
        val importScript = ImportScript(fsScriptInitProperties, fsScriptS3Service)
        
        val success = retryOnThrow(
            actionName = "File Import Script",
            maxRetries = retryProperties.max,
            retryDelayMillis = retryProperties.delayMillis,
            logger = logger
        ) {
            importScript.run()
        }
        
        if (!success) {
            throw ImportScriptException("File Import Script failed after ${retryProperties.max} attempts")
        }
        
        logger.info("File Import Script completed successfully")
    }

    private suspend fun runMigrateScript() {
        val source = migrateProperties.source
            ?: throw MigrateScriptException("fs.script.migrate.source must be set to run the migration")
        // The target client is the Spring bean, closed with the context. The source one is ours to close,
        // otherwise its idle HTTP threads keep the JVM alive after the run.
        val success = MinioMigrationStore.create(
            source.internalUrl, source.username, source.password, source.region
        ).use { sourceStore ->
            val migrateScript = MigrateScript(
                properties = migrateProperties,
                source = sourceStore,
                target = MinioMigrationStore(minioClient),
            )
            retryOnThrow(
                actionName = "Migrate Script",
                maxRetries = retryProperties.max,
                retryDelayMillis = retryProperties.delayMillis,
                logger = logger
            ) {
                // Objects already copied are skipped, so a retry only redoes what failed.
                check(!migrateScript.run().hasFailures) { "Some objects could not be migrated" }
            }
        }

        if (!success) {
            throw MigrateScriptException("Migrate Script failed after ${retryProperties.max} attempts")
        }

        logger.info("Migrate Script completed successfully")
    }
}
