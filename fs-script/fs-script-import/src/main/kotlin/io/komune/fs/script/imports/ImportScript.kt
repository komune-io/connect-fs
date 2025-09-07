package io.komune.fs.script.imports

import com.fasterxml.jackson.module.kotlin.readValue
import io.komune.fs.script.core.model.ImportSettings
import io.komune.fs.script.core.utils.jsonMapper
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.service.FsScriptS3Service
import java.io.File
import org.slf4j.LoggerFactory

class ImportScript(
    private val properties: FsScriptInitProperties,
    private val s3Service: FsScriptS3Service
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("=== ImportScript Configuration ===")
        logger.info("Source Directory: ${properties.source}")
        logger.info("=== Environment Variables (S3 Config) ===")
        logger.info("FS_S3_INTERNAL_URL: ${System.getenv("FS_S3_INTERNAL_URL")}")
        logger.info("FS_S3_EXTERNAL_URL: ${System.getenv("FS_S3_EXTERNAL_URL")}")
        logger.info("FS_S3_BUCKET: ${System.getenv("FS_S3_BUCKET")}")
        logger.info("FS_S3_USERNAME: ${System.getenv("FS_S3_USERNAME")}")
        logger.info("FS_S3_PASSWORD: [REDACTED]")
        logger.info("================================")
    }

    suspend fun run() {
        val rootDirectories = properties.getSourceFiles()
        rootDirectories.forEach { rootDirectory ->
            if (importFolder(rootDirectory)) return
        }
    }

    @Suppress("LongMethod")
    private suspend fun ImportScript.importFolder(rootDirectory: File): Boolean {
        logger.info("/////////////////////////////////////////////////////////////////")
        logger.info("/////////////////////////////////////////////////////////////////")
        logger.info("Importing data from $rootDirectory")
        logger.info("/////////////////////////////////////////////////////////////////")
        logger.info("/////////////////////////////////////////////////////////////////")
        if (!rootDirectory.exists() || !rootDirectory.isDirectory) {
            throw IllegalArgumentException("Root directory does not exist: $rootDirectory")
        }

        val bucketName = rootDirectory.name.lowercase()
        logger.info("Using bucket: $bucketName")

        val globalSettings = loadSettings(rootDirectory, "settings.json")
        
        initBucket(bucketName)

        var uploaded = 0
        var skipped = 0
        var failed = 0
        rootDirectory.walkTopDown()
            .filter { it.isFile }
            .filter { !it.name.endsWith(".settings.json") }
            .filter { it.name != "settings.json" }
            .forEach { file ->
                val relPath = file.relativeTo(rootDirectory).invariantSeparatorsPath
                try {
                    val objectKey = relPath

                    val existing = s3Service.statObject(bucketName, objectKey)
                    
                    val localSize = file.length()
                    if (existing != null && existing.size() == localSize) {
                        logger.info("Skip (unchanged): $relPath")
                        skipped++
                        return@forEach
                    }

                    val content = file.readBytes()
                    val contentType = java.net.URLConnection.guessContentTypeFromName(file.name)
                    val metadata = buildMap {
                        if (contentType != null) put("content-type", contentType)
                    }

                    val fileSettings = loadSettings(file.parentFile, "${file.nameWithoutExtension}.settings.json")
                    
                    val finalMetadata = buildMap {
                        putAll(metadata)
                        fileSettings?.metadata?.let { putAll(it) }
                        globalSettings?.metadata?.let { putAll(it) }
                    }

                    s3Service.putObject(bucketName, objectKey, content, contentType, finalMetadata)
                    
                    if (existing == null) {
                        logger.info("Uploaded (new): $relPath")
                    } else {
                        logger.info("Uploaded (updated): $relPath")
                    }
                    uploaded++
                } catch (e: Exception) {
                    logger.error("Failed to upload $relPath", e)
                    failed++
                }
            }
        logger.info(
            "Import summary for ${'$'}rootDirectory: uploaded=${'$'}uploaded, " +
            "skipped=${'$'}skipped, failed=${'$'}failed"
        )
        return false
    }

    private suspend fun initBucket(bucketName: String) {
        logger.info("Initializing S3 bucket: $bucketName (create if not exists)...")
        try {
            s3Service.ensureBucket(bucketName)
            s3Service.listObjects(bucketName, prefix = "", recursive = false)
            logger.info("S3 bucket '$bucketName' initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize S3 bucket: $bucketName", e)
            throw e
        }
    }
    
    private fun loadSettings(directory: File, filename: String): ImportSettings? {
        val settingsFile = File(directory, filename)
        return if (settingsFile.exists() && settingsFile.isFile) {
            try {
                jsonMapper.readValue<ImportSettings>(settingsFile).also {
                    logger.debug("Loaded settings from: ${settingsFile.absolutePath}")
                }
            } catch (e: Exception) {
                logger.warn("Failed to parse settings file: ${settingsFile.absolutePath}", e)
                null
            }
        } else {
            null
        }
    }

}
