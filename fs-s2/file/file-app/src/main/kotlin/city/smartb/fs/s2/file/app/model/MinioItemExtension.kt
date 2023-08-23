package city.smartb.fs.s2.file.app.model

import city.smartb.fs.s2.file.domain.model.File
import city.smartb.fs.s2.file.domain.model.FilePath
import io.minio.messages.Item

suspend fun Item.toFile(buildUrl: suspend (FilePath) -> String): File {
    val metadata = sanitizedMetadata()
    val path = FilePath.from(objectName())

    return File(
        id = metadata[File::id.name].orEmpty(),
        path = path,
        url = buildUrl(path),
        metadata = metadata,
        isDirectory = isDir,
        size = size(),
        vectorized = metadata[File::vectorized.name].toBoolean(),
        lastModificationDate = if (isDir) 0 else lastModified().toInstant().toEpochMilli()
    )
}

fun Item.sanitizedMetadata() = userMetadata().orEmpty().sanitizedMetadata()

fun Map<String, String>.sanitizedMetadata() = this.mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
