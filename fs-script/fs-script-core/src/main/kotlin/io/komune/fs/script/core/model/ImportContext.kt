package io.komune.fs.script.core.model

import io.komune.fs.s2.file.domain.automate.FileId
import io.komune.fs.s2.file.domain.model.FilePath
import java.io.File
import java.util.concurrent.ConcurrentHashMap

// The files map is the state of one import: equality and copy over the three settings would ignore it.
@Suppress("UseDataClass")
class ImportContext(
    val rootDirectory: File,
    val bucketName: String,
    val settings: ImportSettings?
) {
    val files: ConcurrentHashMap<FilePath, FileId> = ConcurrentHashMap()
}
