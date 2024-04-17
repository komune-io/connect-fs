package io.komune.fs.s2.file.domain.model

import io.komune.fs.s2.file.domain.automate.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Descriptor of a file
 * @d2 model
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 10
 */
@Serializable
@SerialName("File")
data class File(
    /**
     * Identifier of the file
     */
    val id: FileId,

    /**
     * Path of the file within the S3 bucket
     */
    val path: FilePath,

    /**
     * Stringified path of the file within the S3 bucket
     * @example "MyAwesomeObject/91541047-5da8-4161-af79-3fd367fc014e/image/main.jpg"
     */
    val pathStr: String,

    /**
     * External URL to access the file
     * @example "https://s3.myproject.komune.io/myBucket/MyAwesomeObject/91541047-5da8-4161-af79-3fd367fc014e/image/main.jpg"
     */
    val url: String,

    /**
     * Additional metadata of the file
     * @example {
     *  "Pragma" : "no-cache",
     *  "Content-Type" : "image/jpeg",
     *  "secret": "blblbl"
     *  }
     */
    val metadata: Map<String, String>,

    /**
     * True if this is a directory
     * @example false
     */
    val isDirectory: Boolean,

    /**
     * Size of the file in bytes
     * @example 69950
     */
    val size: Long,

    /**
     * Whether the file has been vectorized and sent to a knowledge-base
     * @example false
     */
    val vectorized: Boolean,

    /**
     * Date of the last modification of the file, formatted as UNIX timestamp in ms
     * @example 1692627519000
     */
    val lastModificationDate: Long
)
