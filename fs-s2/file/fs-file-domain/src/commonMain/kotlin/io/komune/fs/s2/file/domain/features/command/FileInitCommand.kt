package io.komune.fs.s2.file.domain.features.command

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.model.FilePath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import s2.dsl.automate.S2InitCommand

typealias FileInitFunction = F2Function<FileInitCommand, FileInitiatedEvent>

@Serializable
data class FileInitCommand(
    val id: FileId,
    val path: FilePath,
    val url: String,
    val hash: String,
    val metadata: Map<String, String>
): S2InitCommand

@Serializable
@SerialName("FileInitiatedEvent")
data class FileInitiatedEvent(
    val id: FileId,
    val path: FilePath,
    val url: String,
    val hash: String,
    val metadata: Map<String, String>,
    val time: Long
): FileEvent {
	override fun s2Id(): FileId = id
}
