package io.komune.fs.s2.file.client

import io.komune.fs.s2.file.domain.features.command.FileDeleteCommand
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvents
import io.komune.fs.s2.file.domain.features.command.FileInitPublicDirectoryCommand
import io.komune.fs.s2.file.domain.features.command.FilePublicDirectoryInitializedEvent
import io.komune.fs.s2.file.domain.features.command.FilePublicDirectoryRevokedEvent
import io.komune.fs.s2.file.domain.features.command.FileRevokePublicDirectoryCommand
import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.features.command.FileUploadedEvent
import io.komune.fs.s2.file.domain.features.query.FileAskQuestionQuery
import io.komune.fs.s2.file.domain.features.query.FileAskQuestionResult
import io.komune.fs.s2.file.domain.features.query.FileDownloadQuery
import io.komune.fs.s2.file.domain.features.query.FileGetQuery
import io.komune.fs.s2.file.domain.features.query.FileGetResult
import io.komune.fs.s2.file.domain.features.query.FileListQuery
import io.komune.fs.s2.file.domain.features.query.FileListResult
import io.ktor.client.HttpClientConfig
import io.ktor.utils.io.ByteReadChannel

class FileClient(
    url: String,
    block: HttpClientConfig<*>.() -> Unit = {}
): Client(url, block) {
    suspend fun fileGet(command: List<FileGetQuery>): List<FileGetResult> = post("fileGet", command)

    suspend fun fileDownload(command: FileDownloadQuery): ByteReadChannel = post("fileDownload", command)

    suspend fun fileList(command: List<FileListQuery>): List<FileListResult> = post("fileList", command)

    suspend fun fileUpload(
        command: FileUploadCommand, file: ByteArray
    ): FileUploadedEvent = postFormData("fileUpload") {
        param("command", command)
        file("file", file, command.path.name)
    }

    suspend fun fileDelete(
        command: List<FileDeleteCommand>
    ): List<FileDeletedEvents> = post("fileDelete", command)

    suspend fun initPublicDirectory(
        command: List<FileInitPublicDirectoryCommand>
    ): List<FilePublicDirectoryInitializedEvent> = post("initPublicDirectory", command)

    suspend fun revokePublicDirectory(
        command: List<FileRevokePublicDirectoryCommand>
    ): List<FilePublicDirectoryRevokedEvent> = post("revokePublicDirectory", command)

    suspend fun fileAskQuestion(
        query: List<FileAskQuestionQuery>
    ): List<FileAskQuestionResult> = post("fileAskQuestion", query)

}
