package io.komune.fs.script.core.model

import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.model.FilePath
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ImportContext(
    val rootDirectory: File,
    val bucketName: String,
    val settings: ImportSettings?
) {
    val files = ConcurrentHashMap<FilePath, FileId>()

}
