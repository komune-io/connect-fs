package io.komune.fs.spring.utils

import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.model.FilePath
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.Base64


fun FilePath.toUploadCommand(
    metadata: Map<String, String> = emptyMap(),
    vectorize: Boolean? = false
) = FileUploadCommand(
    path = this,
    metadata = mapOf(
        "uploadedAt" to System.currentTimeMillis().toString()
    ) + metadata,
    vectorize = vectorize
)

fun ByteArray.hash() = MessageDigest
    .getInstance("SHA-256")
    .digest(this)
    .encodeToB64()

fun ByteArray.encodeToB64() = Base64.getEncoder().encodeToString(this)
fun String.decodeB64() = Base64.getDecoder().decode(substringAfterLast(";base64,"))

suspend fun FilePart.contentByteArray(): ByteArray {
    return ByteArrayOutputStream().use { os ->
        DataBufferUtils.write(content(), os)
            .doOnNext { DataBufferUtils.release(it) }
            .then()
            .awaitFirstOrNull()
        os.toByteArray()
    }
}
