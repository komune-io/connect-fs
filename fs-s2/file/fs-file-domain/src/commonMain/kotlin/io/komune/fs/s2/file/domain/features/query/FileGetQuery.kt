package io.komune.fs.s2.file.domain.features.query

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.model.File
import io.komune.fs.s2.file.domain.model.FilePath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Get a file descriptor and content from a given path.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 10
 */
typealias FileGetFunction = F2Function<FileGetQuery, FileGetResult>

/**
 * @d2 query
 * @parent [FileGetFunction]
 */
typealias FileGetQuery = FilePath

/**
 * @d2 result
 * @parent [FileGetFunction]
 */
@Serializable
@SerialName("FileGetResult")
data class FileGetResult(
    /**
     * Descriptor of the file at the given path, or null if it doesn't exist.
     */
    val item: File?,
)
