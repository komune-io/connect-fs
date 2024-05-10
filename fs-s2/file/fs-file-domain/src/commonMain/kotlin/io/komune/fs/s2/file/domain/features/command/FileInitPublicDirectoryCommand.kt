package io.komune.fs.s2.file.domain.features.command

import f2.dsl.fnc.F2Function
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Grant public access to a given directory.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 30
 */
typealias FileInitPublicDirectoryFunction
        = F2Function<FileInitPublicDirectoryCommand, FilePublicDirectoryInitializedEvent>

/**
 * @d2 command
 * @parent [FileInitPublicDirectoryFunction]
 */
@Serializable
data class FileInitPublicDirectoryCommand(
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
     * Directory to revoke public access from.
     * @example [io.komune.fs.s2.file.domain.model.FilePath.directory]
     */
    val directory: String
)

/**
 * @d2 event
 * @parent [FileInitPublicDirectoryFunction]
 */
@Serializable
@SerialName("FilePublicDirectoryInitializedEvent")
data class FilePublicDirectoryInitializedEvent(
	val path: String
)
