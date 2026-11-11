package io.komune.fs.commons.kb.domain.command

import f2.dsl.cqrs.Event
import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.model.FileAskMessage
import io.komune.fs.s2.file.domain.model.FileAskMessageDTO
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.Serializable

/**
 * Ask question  on a file.
 * @d2 function
 * @parent [io.komune.fs.commons.kb.domain.D2VectorF2Page]
 * @order 20
 */
typealias VectorAskFunction = F2Function<VectorAskQueryDTOBase, VectorAskedEventDTOBase>

/**
 * @d2 command
 * @parent [VectorAskFunction]
 */
@JsExport
@JsName("VectorAskQueryDTO")
interface VectorAskQueryDTO {
    val question: String
    val history: List<FileAskMessageDTO>
    val targetedFiles: List<String>
}

/**
 * @d2 inherit
 */
@Serializable
data class VectorAskQueryDTOBase(
    override val question: String,
    override val history: List<FileAskMessage>,
    override val targetedFiles: List<String>
): VectorAskQueryDTO

/**
 * @d2 event
 * @parent [VectorAskFunction]
 */
@JsExport
@JsName("VectorAskedEventDTO")
interface VectorAskedEventDTO: Event {
    val item: String
}

/**
 * @d2 inherit
 */
@Serializable
data class VectorAskedEventDTOBase(
    override val item: String
): VectorAskedEventDTO
