package io.komune.fs.s2.file.domain.model

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("ChatMessageDTO")
interface FileAskMessageDTO {
    val content: String
    val type: String
}

@Serializable
data class FileAskMessage(
    override val content: String,
    override val type: String
): FileAskMessageDTO
