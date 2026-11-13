package io.komune.fs.script.imports

import io.komune.fs.commons.utils.jsonMapper
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.config.properties.getSourceFiles
import io.komune.fs.script.core.model.ImportSettings
import io.komune.fs.script.core.service.FsScriptS3Service
import io.minio.errors.MinioException
import java.io.File
import java.io.IOException
import java.net.URLConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tools.jackson.module.kotlin.readValue

private const val BANNER = "/////////////////////////////////////////////////////////////////"

class ImportScript(
    private val properties: FsScriptInitProperties,
    private val s3Service: FsScriptS3Service
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("=== ImportScript Configuration ===")
        logger.info("Source Directory: ${properties.sources.orEmpty()}")
        logger.info("=== Environment Variables (S3 Config) ===")
        logger.info("FS_S3_INTERNAL_URL: ${System.getenv("FS_S3_INTERNAL_URL")}")
        logger.info("FS_S3_EXTERNAL_URL: ${System.getenv("FS_S3_EXTERNAL_URL")}")
        logger.info("FS_S3_BUCKET: ${System.getenv("FS_S3_BUCKET")}")
        logger.info("FS_S3_USERNAME: ${System.getenv("FS_S3_USERNAME")}")
        logger.info("FS_S3_PASSWORD: [REDACTED]")
        logger.info("================================")
    }

    fun run() {
        val rootDirectories = properties.getSourceFiles()
        rootDirectories.forEach { rootDirectory ->
            if (importFolder(rootDirectory)) return
        }
    }

    private fun importFolder(rootDirectory: File): Boolean {
        logger.info(BANNER)
        logger.info(BANNER)
        logger.info("Importing data from $rootDirectory")
        logger.info(BANNER)
        logger.info(BANNER)
        validateDirectory(rootDirectory)

        val bucketName = rootDirectory.name.lowercase()
        logger.info("Using bucket: $bucketName")

        val globalSettings = loadSettings(rootDirectory, "settings.json")
        initBucket(bucketName)

        val stats = processFiles(rootDirectory, bucketName, globalSettings)
        logImportSummary(rootDirectory, stats)

        return false
    }

    private fun validateDirectory(rootDirectory: File) {
        require(rootDirectory.exists() && rootDirectory.isDirectory) { "Root directory does not exist: $rootDirectory" }
    }

    private fun processFiles(
        rootDirectory: File,
        bucketName: String,
        globalSettings: ImportSettings?
    ): ImportStats {
        var uploaded = 0
        var skipped = 0
        var failed = 0

        rootDirectory.walkTopDown()
            .filter { it.isFile }
            .filter { !it.name.endsWith(".settings.json") }
            .filter { it.name != "settings.json" }
            .forEach { file ->
                val result = processFile(
                    file = file,
                    rootDirectory = rootDirectory,
                    bucketName = bucketName,
                    globalSettings = globalSettings,
                )
                when (result) {
                    FileProcessResult.UPLOADED -> uploaded++
                    FileProcessResult.SKIPPED -> skipped++
                    FileProcessResult.FAILED -> failed++
                }
            }

        return ImportStats(uploaded, skipped, failed)
    }

    private fun processFile(
        file: File,
        rootDirectory: File,
        bucketName: String,
        globalSettings: ImportSettings?
    ): FileProcessResult {
        val relPath = file.relativeTo(rootDirectory).invariantSeparatorsPath
        return try {
            val objectKey = relPath

            if (shouldSkipFile(file = file, bucketName = bucketName, objectKey = objectKey, relPath = relPath)) {
                return FileProcessResult.SKIPPED
            }

            uploadFile(
                file = file,
                bucketName = bucketName,
                objectKey = objectKey,
                globalSettings = globalSettings,
                relPath = relPath,
            )
            FileProcessResult.UPLOADED
        } catch (e: MinioException) {
            logger.error("Failed to upload $relPath", e)
            FileProcessResult.FAILED
        } catch (e: IOException) {
            logger.error("Failed to upload $relPath", e)
            FileProcessResult.FAILED
        }
    }

    private fun shouldSkipFile(
        file: File,
        bucketName: String,
        objectKey: String,
        relPath: String
    ): Boolean {
        val existing = s3Service.statObject(bucketName, objectKey)
        val localSize = file.length()

        if (existing != null && existing.size() == localSize) {
            logger.info("Skip (unchanged): $relPath")
            return true
        }
        return false
    }

    private fun uploadFile(
        file: File,
        bucketName: String,
        objectKey: String,
        globalSettings: ImportSettings?,
        relPath: String
    ) {
        val content = file.readBytes()
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        val metadata = buildFileMetadata(file, globalSettings, contentType)

        val existing = s3Service.statObject(bucketName, objectKey)
        s3Service.putObject(
            bucketName = bucketName,
            objectKey = objectKey,
            content = content,
            contentType = contentType,
            metadata = metadata,
        )

        val status = if (existing == null) "new" else "updated"
        logger.info("Uploaded ($status): $relPath")
    }

    private fun buildFileMetadata(
        file: File,
        globalSettings: ImportSettings?,
        contentType: String?
    ): Map<String, String> {
        val baseMetadata = buildMap {
            if (contentType != null) put(key = "content-type", value = contentType)
        }

        val fileSettings = loadSettings(
            file.parentFile,
            "${file.nameWithoutExtension}.settings.json"
        )

        return buildMap {
            putAll(baseMetadata)
            fileSettings?.metadata?.let { putAll(it) }
            globalSettings?.metadata?.let { putAll(it) }
        }
    }

    private fun logImportSummary(rootDirectory: File, stats: ImportStats) {
        logger.info(
            "Import summary for $rootDirectory: uploaded=${stats.uploaded}, " +
            "skipped=${stats.skipped}, failed=${stats.failed}"
        )
    }

    private data class ImportStats(
        val uploaded: Int,
        val skipped: Int,
        val failed: Int
    )

    private enum class FileProcessResult {
        UPLOADED, SKIPPED, FAILED
    }

    private fun initBucket(bucketName: String) {
        logger.info("Initializing S3 bucket: $bucketName (create if not exists)...")
        try {
            s3Service.ensureBucket(bucketName)
            val _ = s3Service.listObjects(bucketName, prefix = "", isRecursive = false)
            logger.info("S3 bucket '$bucketName' initialized successfully")
        } catch (e: MinioException) {
            logger.error("Failed to initialize S3 bucket: $bucketName", e)
            throw e
        } catch (e: IOException) {
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
            } catch (e: IOException) {
                logger.warn("Failed to parse settings file: ${settingsFile.absolutePath}", e)
                null
            }
        } else {
            null
        }
    }

}
