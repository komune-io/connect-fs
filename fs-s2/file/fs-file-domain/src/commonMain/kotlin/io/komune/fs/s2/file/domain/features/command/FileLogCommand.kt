package io.komune.fs.s2.file.domain.features.command

import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.model.FilePath
import f2.dsl.fnc.F2Function
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import s2.dsl.automate.S2Command

typealias FileLogFunction = F2Function<FileLogCommand, FileLoggedEvent>

@Serializable
data class FileLogCommand(
    override val id: FileId,
    val path: String,
    val hash: String,
    val metadata: Map<String, String>
): S2Command<FileId>

@Serializable
@SerialName("FileLoggedEvent")
data class FileLoggedEvent(
    val id: FileId,
    val path: FilePath,
    val url: String,
    val hash: String,
    val metadata: Map<String, String>,
    val time: Long
): FileEvent {
	override fun s2Id(): FileId = id
}
