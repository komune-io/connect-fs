package io.komune.fs.spring.utils

import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.model.FilePath
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.Base64
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart

fun FilePath.toUploadCommand(
    metadata: Map<String, String> = emptyMap(),
    isVectorizationRequested: Boolean = false
): FileUploadCommand = FileUploadCommand(
    path = this,
    metadata = mapOf(
        "uploadedAt" to System.currentTimeMillis().toString()
    ) + metadata,
    isVectorizationRequested = isVectorizationRequested
)

fun ByteArray.hash(): String = MessageDigest
    .getInstance("SHA-256")
    .digest(this)
    .encodeToB64()

fun ByteArray.encodeToB64(): String = Base64.getEncoder().encodeToString(this)
fun String.decodeB64(): ByteArray = Base64.getDecoder().decode(substringAfterLast(";base64,"))

suspend fun FilePart.contentByteArray(): ByteArray {
    return ByteArrayOutputStream().use { os ->
        DataBufferUtils.write(content(), os).awaitLast()
        os.toByteArray()
    }
}
