package io.komune.fs.script.gateway

import io.komune.fs.script.core.config.properties.FsRetryProperties
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.utils.retryOnThrow
import io.komune.fs.script.imports.ImportScript
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(FsScriptInitProperties::class, FsRetryProperties::class)
class ScriptServiceRunner(
    private val context: ConfigurableApplicationContext,
    private val fsScriptInitProperties: FsScriptInitProperties,
    private val retryProperties: FsRetryProperties
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(ScriptServiceRunner::class.java)
    

    override fun run(vararg args: String?) = runBlocking {
        try {
            logger.info("Starting FS Script Gateway...")
            runFileImportScript()
            logger.info("FS Script Gateway completed successfully")
        } catch (e: Exception) {
            logger.error("FS Script Gateway failed", e)
        } finally {
            context.close()
        }
    }

    private suspend fun runFileImportScript() {
        val importScript = ImportScript(fsScriptInitProperties)
        
        val success = retryOnThrow(
            actionName = "File Import Script",
            maxRetries = retryProperties.max,
            retryDelayMillis = retryProperties.delayMillis,
            logger = logger
        ) {
            importScript.run()
        }
        
        check(success) { "File Import Script failed after ${retryProperties.max} attempts" }
        
        logger.info("File Import Script completed successfully")
    }
}
