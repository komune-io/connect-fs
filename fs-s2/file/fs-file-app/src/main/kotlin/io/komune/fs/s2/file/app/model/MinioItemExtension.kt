package io.komune.fs.s2.file.app.model

import io.komune.fs.s2.file.domain.model.File
import io.komune.fs.s2.file.domain.model.FilePath
import io.minio.Http
import io.minio.messages.Item

suspend fun Item.toFile(buildUrl: suspend (FilePath) -> String): File {
    val metadata = sanitizedMetadata()
    val path = FilePath.from(objectName())

    return File(
        id = metadata[File::id.name].orEmpty(),
        path = path,
        pathStr = path.toString(),
        url = buildUrl(path),
        metadata = metadata,
        isDirectory = isDir,
        size = size(),
        vectorized = metadata[File::vectorized.name].toBoolean(),
        lastModificationDate = if (isDir) 0 else lastModified().toInstant().toEpochMilli()
    )
}

fun Item.sanitizedMetadata() = userMetadata().orEmpty().sanitizedMetadata()

fun Http.Headers.sanitizedMetadata(): Map<String, String> = toMetadataMap().sanitizedMetadata()

fun Http.Headers.toMetadataMap(): Map<String, String?> = associate { it.key to it.value }

fun Map<String, String?>.sanitizedMetadata(): Map<String, String> = this
    .mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
    .filterValues { it != null } as Map<String, String>
