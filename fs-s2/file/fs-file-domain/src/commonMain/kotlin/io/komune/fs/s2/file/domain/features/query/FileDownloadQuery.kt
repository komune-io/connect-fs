package io.komune.fs.s2.file.domain.features.query

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.model.FilePathDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Download the content of a file from a given path.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 15
 */
typealias FileDownloadFunction = F2Function<FileDownloadQuery, FileDownloadResult>

/**
 * @d2 query
 * @parent [FileDownloadFunction]
 */
@Serializable
data class FileDownloadQuery(
    override val objectType: String,
    override val objectId: String,
    override val directory: String,
    override val name: String
): FilePathDTO

/**
 * @d2 result
 * @parent [FileDownloadFunction]
 */
@Serializable
@SerialName("FileDownloadResult")
data class FileDownloadResult(
    /**
     * Content as bytes[] of the file at the given path, or null if it doesn't exist.
     * @example []
     */
    val content: ByteArray?
)
