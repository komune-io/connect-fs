package io.komune.fs.commons.kb.domain.command

import io.komune.fs.s2.file.domain.model.FileAskMessage
import f2.dsl.cqrs.Event
import f2.dsl.fnc.F2Function
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

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
    val history: List<FileAskMessage>
    val targetedFiles: List<String>
}

interface VectorAskMessageDTO {
    val content: String
    val type: String
    @Suppress("VariableNaming")
    val additional_kwargs: Map<String, String>
}

@Serializable
class VectorAskMessageDTOBase(
    override val content: String,
    override val type: String,
    override val additional_kwargs: Map<String, String>
): VectorAskMessageDTO

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
class VectorAskedEventDTOBase(
    override val item: String
): VectorAskedEventDTO
