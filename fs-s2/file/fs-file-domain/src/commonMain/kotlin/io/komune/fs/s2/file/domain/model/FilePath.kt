package io.komune.fs.s2.file.domain.model

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsExport
@JsName("FilePathDTO")
interface FilePathDTO {
    /**
     * Type of object the file is attached to.
     *
     * (must not contain any '/')
     * @example "MyAwesomeObject"
     */
    val objectType: String

    /**
     * Identifier of the object the file is attached to.
     *
     * (must not contain any '/')
     * @example "91541047-5da8-4161-af79-3fd367fc014e"
     */
    val objectId: String

    /**
     * Directory name.
     *
     * (must not contain any '/')
     * @example "image"
     */
    val directory: String

    /**
     * File name.
     * @example "main.jpg"
     */
    val name: String
}

/**
 * Describe a path to a file within S3.
 * @d2 model
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 20
 */
@Serializable
@SerialName("FilePath")
data class FilePath(
    override val objectType: String,
    override val objectId: String,
    override val directory: String,
    override val name: String
): FilePathDTO {
    override fun toString() = "$objectType/$objectId/$directory/$name"
        .substringBefore("//")
        .removeSuffix("/")

    fun toPartialPrefix(trailingSlash: Boolean = true) = toString().plus(if (trailingSlash) "/" else "")

    companion object {
        fun from(path: String): FilePath {
            val parts = path.split("/", limit = 4).padEnd(size = 4)
            return FilePath(
                objectType = parts[0],
                objectId = parts[1],
                directory = parts[2],
                name = parts[3]
            )
        }

        private fun List<String>.padEnd(size: Int) = if (this.size < size) {
            this + List(size - this.size) { "" }
        } else {
            this
        }
    }
}

fun FilePath.buildUrl(baseUrl: String, bucket: String, dnsStyle: Boolean): String = if (dnsStyle) {
    val (scheme, host) = baseUrl.removeSuffix("/").split("://")
    "$scheme://$bucket.$host/$this"
} else {
    "${baseUrl.removeSuffix("/")}/$bucket/$this"
}
