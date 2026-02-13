package io.komune.fs.s2.file.domain.features.command

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.model.FilePath
import kotlinx.serialization.Serializable
import s2.dsl.automate.S2Command

/**
 * Delete all files matching a given path.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 20
 */
typealias FileDeleteFunction = F2Function<FileDeleteCommand, FileDeletedEvents>

/**
 * @d2 command
 * @parent [FileDeleteFunction]
 */
@Serializable
data class FileDeleteCommand(
	/**
	 * @ref [io.komune.fs.s2.file.domain.model.FilePath.objectType]
	 */
	val objectType: String?,

	/**
	 * @ref [io.komune.fs.s2.file.domain.model.FilePath.objectId]
	 */
	val objectId: String?,

	/**
	 * @ref [io.komune.fs.s2.file.domain.model.FilePath.directory]
	 */
	val directory: String?,

	/**
	 * @ref [io.komune.fs.s2.file.domain.model.FilePath.name]
	 */
	val name: String?
)

@Serializable
data class FileDeleteByIdCommand(
	override val id: FileId
): S2Command<FileId>

/**
 * @d2 event
 * @parent [FileDeleteFunction]
 */
@Serializable
data class FileDeletedEvents(
	val items: List<FileDeletedEvent>
)

@Serializable
data class FileDeletedEvent(
    /**
	 * Identifier of the deleted file.
	 */
	val id: FileId,

    /**
	 * Path of the deleted file.
	 */
	val path: FilePath
): FileEvent {
	override fun s2Id() = id
}
