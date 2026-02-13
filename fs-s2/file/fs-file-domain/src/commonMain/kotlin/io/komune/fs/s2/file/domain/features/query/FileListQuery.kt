package io.komune.fs.s2.file.domain.features.query

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.model.File
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Get a list of the file descriptors at the given path.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 20
 */
typealias FileListFunction = F2Function<FileListQuery, FileListResult>

/**
 * @d2 query
 * @parent [FileListFunction]
 */
@Serializable
data class FileListQuery(
    /**
     * Type of object the file are attached to.
     * @example [io.komune.fs.s2.file.domain.model.FilePath.objectType]
     */
    val objectType: String?,

    /**
     * Identifier of the object the file are attached to.
     * @example [io.komune.fs.s2.file.domain.model.FilePath.objectId]
     */
    val objectId: String?,

    /**
     * Directory containing the files.
     * @example [io.komune.fs.s2.file.domain.model.FilePath.directory]
     */
    val directory: String?,

    /**
     * If true, fetch all files of subdirectories instead of said directories
     * @example false
     * @default true
     */
    val recursive: Boolean = true
)

/**
 * @d2 result
 * @parent [FileListFunction]
 */
@Serializable
@SerialName("FileListResult")
data class FileListResult(
    /**
     * List of descriptors of the files within the given path
     */
    val items: List<File>
)
