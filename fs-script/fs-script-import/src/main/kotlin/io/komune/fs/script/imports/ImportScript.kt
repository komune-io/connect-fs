package io.komune.fs.script.imports

import com.fasterxml.jackson.module.kotlin.readValue
import io.komune.fs.script.core.model.ImportSettings
import io.komune.fs.script.core.utils.jsonMapper
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.model.ImportContext
import java.io.File
import org.slf4j.LoggerFactory

import io.komune.fs.s2.file.client.FileClient
import io.komune.fs.s2.file.domain.features.query.FileListQuery

class ImportScript(
    private val properties: FsScriptInitProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val fileClient: FileClient by lazy {
        val authRealm = properties.asAuthRealm()
        FileClient(
            url = properties.fs.url,
            authProvider = { authRealm }
        )
    }

    suspend fun run() {
        val rootDirectories = properties.getSourceFiles()
        rootDirectories.map { rootDirectory ->
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

        val settingsFile = File(rootDirectory, "settings.json")
        if (!settingsFile.exists() || !settingsFile.isFile) {
            throw IllegalArgumentException("File settings.json not found in root directory")
        }

        val importSettings = jsonMapper.readValue<ImportSettings>(settingsFile)
            .buckets
            ?: return true

        // Initialize bucket by triggering a lightweight list operation
        initBucket()

        // Traverse files and upload using FilePath convention: objectType/objectId/directory/name
        val importContext = ImportContext(rootDirectory, importSettings)
        var uploaded = 0
        var skipped = 0
        var failed = 0
        rootDirectory.walkTopDown()
            .filter { it.isFile }
            .filter { it.name != "settings.json" }
            .forEach { file ->
                val relPath = file.relativeTo(rootDirectory).invariantSeparatorsPath
                try {
                    // If relPath doesn't contain enough segments, skip with warning
                    val segments = relPath.split("/")
                    if (segments.size < 1) {
                        logger.warn("Skipping file with empty relative path: $relPath")
                        skipped++
                        return@forEach
                    }

                    // Build FilePath from relPath (FilePath.from handles missing parts)
                    val filePath = io.komune.fs.s2.file.domain.model.FilePath.from(relPath)

                    // Idempotency: check if file exists and size matches; if so, skip
                    val existing = fileClient.fileGet(listOf(filePath)).firstOrNull()?.item
                    val localSize = file.length()
                    if (existing != null && existing.size == localSize) {
                        logger.info("Skip (unchanged): $relPath")
                        skipped++
                        return@forEach
                    }

                    val content = file.readBytes()
                    val contentType = java.net.URLConnection.guessContentTypeFromName(file.name)
                    val metadata = buildMap {
                        if (contentType != null) put("content-type", contentType)
                    }

                    val event = fileClient.fileUpload(
                        command = io.komune.fs.s2.file.domain.features.command.FileUploadCommand(
                            path = filePath,
                            metadata = metadata
                        ),
                        file = content
                    )
                    importContext.files[filePath] = event.id
                    if (existing == null) {
                        logger.info("Uploaded (new): $relPath -> ${'$'}{event.id}")
                    } else {
                        logger.info("Uploaded (updated): $relPath -> ${'$'}{event.id}")
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

    private suspend fun initBucket() {
        logger.info("Initializing S3 bucket (create if not exists)...")
        try {
            // Any call to the file API ensures bucket existence server-side
            // The FileClient/S3Service automatically creates the bucket if it doesn't exist
            fileClient.fileList(
                command = listOf(
                    FileListQuery(
                        objectType = null,
                        objectId = null,
                        directory = null,
                        recursive = false
                    )
                )
            )
            logger.info("S3 bucket initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize S3 bucket", e)
            throw e
        }
    }

}
