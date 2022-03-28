package city.smartb.fs.s2.file.app.entity

import city.smartb.fs.s2.file.domain.automate.FileId
import city.smartb.fs.s2.file.domain.automate.FileState
import s2.dsl.automate.model.WithS2Id
import s2.dsl.automate.model.WithS2State

data class FileEntity(
    val id: FileId,
    val name: String,
    val objectId: String,
    val category: String?,
    val metadata: Map<String, String>,
    val uploadDate: Long,
    val status: FileState
): WithS2State<FileState>, WithS2Id<FileId> {
	override fun s2State(): FileState = status
	override fun s2Id(): FileId = id
}
