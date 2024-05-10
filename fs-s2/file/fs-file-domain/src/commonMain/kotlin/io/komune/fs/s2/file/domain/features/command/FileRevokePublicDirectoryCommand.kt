package io.komune.fs.s2.file.domain.features.command

import f2.dsl.fnc.F2Function
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Revoke public access from a given directory.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 40
 */
typealias FileRevokePublicDirectoryFunction
		= F2Function<FileRevokePublicDirectoryCommand, FilePublicDirectoryRevokedEvent>


/**
 * @d2 command
 * @parent [FileRevokePublicDirectoryFunction]
 */
@Serializable
data class FileRevokePublicDirectoryCommand(
	/**
	 * Type of object the directory is attached to.
	 * @example "*"
	 */
	val objectType: String,

	/**
	 * Identifier of the object the directory is attached to.
	 * @example "*"
	 */
	val objectId: String,

	/**
	 * Directory to grant public access to.
	 * @example [io.komune.fs.s2.file.domain.model.FilePath.directory]
	 */
	val directory: String
)

/**
 * Result of the file revoke public directory command.
 * @d2 event
 * @parent [FileRevokePublicDirectoryFunction]
 */
@Serializable
@SerialName("FilePublicDirectoryRevokedEvent")
data class FilePublicDirectoryRevokedEvent(
	val path: String
)
