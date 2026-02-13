package io.komune.fs.s2.file.app

import f2.dsl.fnc.f2Function
import f2.dsl.fnc.invokeWith
import f2.spring.exception.NotFoundException
import io.komune.fs.api.config.Roles
import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.api.config.S3Properties
import io.komune.fs.commons.kb.KbClient
import io.komune.fs.commons.kb.domain.command.VectorAskQueryDTOBase
import io.komune.fs.commons.kb.domain.command.VectorCreateCommandDTOBase
import io.komune.fs.commons.utils.toJson
import io.komune.fs.s2.file.app.config.FsSsmConfig
import io.komune.fs.s2.file.app.model.Policy
import io.komune.fs.s2.file.app.model.S3Action
import io.komune.fs.s2.file.app.model.S3Effect
import io.komune.fs.s2.file.app.model.Statement
import io.komune.fs.s2.file.app.model.sanitizedMetadata
import io.komune.fs.s2.file.app.model.toFile
import io.komune.fs.s2.file.app.model.toFileUploadedEvent
import io.komune.fs.s2.file.app.service.S3Service
import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.features.command.FileDeleteByIdCommand
import io.komune.fs.s2.file.domain.features.command.FileDeleteFunction
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvent
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvents
import io.komune.fs.s2.file.domain.features.command.FileInitCommand
import io.komune.fs.s2.file.domain.features.command.FileInitPublicDirectoryFunction
import io.komune.fs.s2.file.domain.features.command.FileLogCommand
import io.komune.fs.s2.file.domain.features.command.FilePublicDirectoryInitializedEvent
import io.komune.fs.s2.file.domain.features.command.FilePublicDirectoryRevokedEvent
import io.komune.fs.s2.file.domain.features.command.FileRevokePublicDirectoryFunction
import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.features.command.FileUploadedEvent
import io.komune.fs.s2.file.domain.features.command.FileVectorizeFunction
import io.komune.fs.s2.file.domain.features.command.FileVectorizedEvent
import io.komune.fs.s2.file.domain.features.query.FileAskQuestionFunction
import io.komune.fs.s2.file.domain.features.query.FileAskQuestionResult
import io.komune.fs.s2.file.domain.features.query.FileDownloadQuery
import io.komune.fs.s2.file.domain.features.query.FileGetFunction
import io.komune.fs.s2.file.domain.features.query.FileGetResult
import io.komune.fs.s2.file.domain.features.query.FileListFunction
import io.komune.fs.s2.file.domain.features.query.FileListResult
import io.komune.fs.s2.file.domain.model.File
import io.komune.fs.s2.file.domain.model.FilePath
import io.komune.fs.spring.utils.contentByteArray
import io.komune.fs.spring.utils.hash
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import java.io.InputStream
import java.net.URLConnection
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 * @d2 service
 * @title File/Entrypoints
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 */
@RestController
@RequestMapping
@Configuration
@Suppress("TooManyFunctions")
class FileEndpoint(
    private val fileDeciderSourcingImpl: FileDeciderSourcingImpl,
    private val fsSsmConfig: FsSsmConfig,
    private val s3Properties: S3Properties,
    private val s3BucketProvider: S3BucketProvider,
    private val s3Service: S3Service,
) {
    private val logger = LoggerFactory.getLogger(FileEndpoint::class.java)

    @Autowired(required = false)
    private lateinit var kbClient: KbClient

    /**
     * Fetch a given file descriptor and content
     */
    @RolesAllowed(Roles.READ_FILE)
    @Bean
    fun fileGet(): FileGetFunction = f2Function { query ->
        val pathStr = query.toString()
        logger.info("fileGet: $pathStr")

        val objectStats = s3Service.statObject(pathStr)
        val metadata = objectStats
            ?.userMetadata()
            ?.sanitizedMetadata()
            ?: return@f2Function FileGetResult(null)

        FileGetResult(
            item = File(
                id = metadata["id"].orEmpty(),
                path = query,
                pathStr = pathStr,
                url = query.buildUrl(),
                metadata = metadata,
                isDirectory = false,
                size = objectStats.size(),
                vectorized = metadata[File::vectorized.name].toBoolean(),
                lastModificationDate = objectStats.lastModified().toInstant().toEpochMilli()
            ),
        )
    }

    @RolesAllowed(Roles.READ_FILE)
    @PostMapping("/fileDownload", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun fileDownload(@RequestBody query: FileDownloadQuery): ResponseEntity<InputStreamResource> {
        val path = FilePath(
            objectType = query.objectType,
            objectId = query.objectId,
            directory = query.directory,
            name = query.name,
        ).toString()
        logger.info("fileDownload: $path")

        val contentType = URLConnection.guessContentTypeFromName(query.name)
            ?.split("/")
            ?.takeIf { it.size == 2 }
            ?.let { (type, subtype) -> MediaType(type, subtype) }
            ?: MediaType.APPLICATION_OCTET_STREAM

        val fileStream = s3Service.getObject(path) ?: InputStream.nullInputStream()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType.toString())
            .body(InputStreamResource(fileStream))
    }

    /**
     * Fetch a list of file descriptors
     */
    @RolesAllowed(Roles.READ_FILE)
    @Bean
    fun fileList(): FileListFunction = f2Function { query ->
        logger.info("fileList: $query")
        val prefix = FilePath(
            objectType = query.objectType ?: "",
            objectId = query.objectId ?: "",
            directory = query.directory ?: "",
            name = ""
        ).toPartialPrefix()

        s3Service.listObjects(prefix, query.recursive)
            .map { obj -> obj.get().toFile { it.buildUrl() } }
            .let(::FileListResult)
    }

    /**
     * Upload multiple files
     * @d2 command
     */
    @RolesAllowed(Roles.WRITE_FILE)
    @PostMapping("/fileUploads")
    suspend fun fileUploads(
        @RequestPart("command") commands: HashMap<String, FileUploadCommand>,
        @RequestPart("file") files: Flux<FilePart>
    ): Flux<FileUploadedEvent> {
        return files.asFlow().map { file ->
            val cmd = commands[file.filename()]!! // TODO throw readable error if no command found for file
            fileUpload(cmd, file)
        }.asFlux()
    }

    /**
     * Upload a file
     */
    @RolesAllowed(Roles.WRITE_FILE)
    @PostMapping("/fileUpload")
    suspend fun fileUpload(
        @RequestPart("command") cmd: FileUploadCommand,
        @RequestPart("file") file: FilePart
    ): FileUploadedEvent {
        val pathStr = cmd.path.toString()
        logger.info("fileUpload: $cmd")

        val fileMetadata = s3Service.getObjectMetadata(pathStr)
        val fileExists = fileMetadata != null
        val fileId = fileMetadata?.get("id") ?: UUID.randomUUID().toString()

        val fileByteArray = file.contentByteArray()

        s3Service.putObject(
            path = pathStr,
            content = fileByteArray,
            metadata = cmd.metadata.plus("id" to fileId)
        )

        if (cmd.vectorize ?: false) {
            vectorize(cmd.path, cmd.metadata, fileByteArray)
        }

        return if (mustBeSavedToSsm(cmd.path.directory)) {
            if (fileExists) {
                fileByteArray.logFileInSsm(cmd, fileId, cmd.path.buildUrl())
            } else {
                fileByteArray.initFileInSsm(cmd, fileId, cmd.path.buildUrl())
            }
        } else {
            FileUploadedEvent(
                id = fileId,
                path = cmd.path,
                url = cmd.path.buildUrl(),
                hash = fileByteArray.hash(),
                metadata = cmd.metadata,
                time = System.currentTimeMillis()
            )
        }
    }

    private suspend fun vectorize(
        path: FilePath,
        metadata: Map<String, String>,
        fileByteArray: ByteArray
    ) {
        logger.debug("Vectorizing file $path")
        VectorCreateCommandDTOBase(
            path = path,
            file = fileByteArray,
            metadata = metadata
        ).invokeWith(kbClient.vectorCreateFunction())

        val newMetadata = s3Service.getObjectMetadata(path.toString())
            .orEmpty()
            .sanitizedMetadata()
            .plus("vectorized" to "true")

        println(newMetadata.toJson())

        s3Service.copyObject(path.toString(), newMetadata)

        logger.debug("File $path vectorized")
    }

    @PermitAll
    @Bean
    fun fileAskQuestion(): FileAskQuestionFunction = f2Function { query ->
        logger.info("fileAskQuestion: $query")
        VectorAskQueryDTOBase(
            question = query.question,
            targetedFiles = query.metadata.targetedFiles,
            history = query.history
        ).invokeWith(kbClient.knowledgeAsk())
            .let { FileAskQuestionResult(it.item) }

    }

    /**
     * Delete a file
     */
    @RolesAllowed(Roles.WRITE_FILE)
    @Bean
    fun fileDelete(): FileDeleteFunction = f2Function { command ->
        val commandPathStr = FilePath(
            objectType = command.objectType ?: "",
            objectId = command.objectId ?: "",
            directory = command.directory ?: "",
            name = command.name ?: ""
        ).toPartialPrefix(trailingSlash = command.name == null)
        logger.info("fileDelete: $commandPathStr")

        val events = s3Service.listObjects(commandPathStr, true).also { objects ->
            val count = objects.count()
            require(count > 0) {
                "File not found at path [$commandPathStr]"
            }
            logger.info("Found $count files to delete")
        }.mapAsync { obj ->
            val file = obj.get().toFile { it.buildUrl() }
            val metadata = s3Service.getObjectMetadata(file.pathStr)!!
            val id = metadata["id"]

            s3Service.removeObject(file.pathStr)
            if (id != null && mustBeSavedToSsm(file.path.directory)) {
                fileDeciderSourcingImpl.delete(FileDeleteByIdCommand(id = id))
            }

            FileDeletedEvent(
                id = id.orEmpty(),
                path = file.path
            )
        }

        FileDeletedEvents(events)
    }

    /**
     * Vectorize a file and save it into a vector-store
     */
    @RolesAllowed(Roles.WRITE_FILE)
    @Bean
    fun fileVectorize(): FileVectorizeFunction = f2Function { cmd ->
        logger.info("fileVectorize: ${cmd.path}")

        val fileContent = withContext(Dispatchers.IO) {
            s3Service.getObject(cmd.path.toString())?.use { it.readAllBytes() }
        } ?: throw NotFoundException("File", cmd.path.toString())

        vectorize(cmd.path, cmd.metadata, fileContent)

        FileVectorizedEvent(cmd.path)
    }

    /**
     * Grant public access to a given directory
     */
    @RolesAllowed(Roles.WRITE_POLICY)
    @Bean
    fun initPublicDirectory(): FileInitPublicDirectoryFunction = f2Function { cmd ->
        val path = FilePath(
            objectType = cmd.objectType,
            objectId = cmd.objectId,
            directory = cmd.directory,
            name = "*"
        ).toString()
        logger.info("initPublicDirectory: $path")

        val policy = s3Service.getBucketPolicy().orEmpty()
        policy.getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)
            .addResource(bucket = s3BucketProvider.getBucket(), path = path)
        s3Service.setBucketPolicy(policy)

        FilePublicDirectoryInitializedEvent(
            path = Statement.resourcePath(bucket = s3BucketProvider.getBucket(), path = path)
        )
    }

    /**
     * Revoke public access to a given directory
     */
    @RolesAllowed(Roles.WRITE_POLICY)
    @Bean
    fun revokePublicDirectory(): FileRevokePublicDirectoryFunction = f2Function { cmd ->
        val path = FilePath(
            objectType = cmd.objectType,
            objectId = cmd.objectId,
            directory = cmd.directory,
            name = "*"
        ).toString()
        logger.info("revokePublicDirectory: $path")

        val policy = s3Service.getBucketPolicy().orEmpty()
        policy.getStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)
            ?.removeResource(bucket = s3BucketProvider.getBucket(), path = path)
        s3Service.setBucketPolicy(policy)

        FilePublicDirectoryRevokedEvent(
            path = Statement.resourcePath(bucket = s3BucketProvider.getBucket(), path = path)
        )
    }

    private fun mustBeSavedToSsm(directory: String?) = directory in fsSsmConfig.directories.orEmpty()

    private suspend fun ByteArray.initFileInSsm(
        cmd: FileUploadCommand, fileId: FileId, path: String
    ): FileUploadedEvent {
        return FileInitCommand(
            id = fileId,
            path = cmd.path,
            url = path,
            hash = hash(),
            metadata = cmd.metadata,
        ).let { fileDeciderSourcingImpl.init(it).toFileUploadedEvent() }
    }

    private suspend fun ByteArray.logFileInSsm(
        cmd: FileUploadCommand, fileId: FileId, path: String
    ): FileUploadedEvent {
        return FileLogCommand(
            id = fileId,
            path = path,
            hash = hash(),
            metadata = cmd.metadata,
        ).let { fileDeciderSourcingImpl.log(it).toFileUploadedEvent() }
    }

    private suspend fun FilePath.buildUrl() = buildUrl(
        s3Properties.externalUrl, s3BucketProvider.getBucket(), s3Properties.dns
    )

    private fun Policy?.orEmpty() = this ?: Policy()

    private suspend inline fun <T, R> Iterable<T>.mapAsync(
        crossinline transform: suspend (T) -> R
    ): List<R> = coroutineScope {
        map {
            async { transform(it) }
        }.awaitAll()
    }
}
