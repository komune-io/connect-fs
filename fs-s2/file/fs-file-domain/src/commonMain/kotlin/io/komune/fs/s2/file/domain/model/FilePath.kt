package io.komune.fs.s2.file.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

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
 * Describe a path to a file within S3
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
    companion object {
        fun from(path: String): FilePath {
            val (objectType, objectId, directory, name) = path.split("/", limit = 4).padEnd(4)
            return FilePath(
                objectType = objectType,
                objectId = objectId,
                directory = directory,
                name = name
            )
        }

        private fun List<String>.padEnd(size: Int) = if (this.size < size) {
            this + List(size - this.size) { "" }
        } else this
    }

    override fun toString() = "$objectType/$objectId/$directory/$name"
        .substringBefore("//") // stop before first empty parameter
        .removeSuffix("/")

    fun toPartialPrefix(trailingSlash: Boolean = true) = toString().plus(if (trailingSlash) "/" else "")

    fun buildUrl(baseUrl: String, bucket: String, dnsStyle: Boolean) = if (dnsStyle) {
        val (scheme, host) = baseUrl.removeSuffix("/").split("://")
        "$scheme://$bucket.$host/$this"
    } else {
        "${baseUrl.removeSuffix("/")}/$bucket/$this"
    }
}
