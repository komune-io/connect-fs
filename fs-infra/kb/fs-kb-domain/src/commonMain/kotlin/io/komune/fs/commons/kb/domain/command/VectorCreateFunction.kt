package io.komune.fs.commons.kb.domain.command

import io.komune.fs.s2.file.domain.model.FilePathDTO
import f2.dsl.cqrs.Event
import f2.dsl.fnc.F2Function
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Create a knowledge vector.
 * @d2 function
 * @parent [io.komune.fs.commons.kb.domain.D2VectorF2Page]
 * @order 10
 */
typealias VectorCreateFunction = F2Function<VectorCreateCommandDTOBase, Unit>

/**
 * @d2 command
 * @parent [VectorCreateFunction]
 */
@JsExport
@JsName("VectorCreateCommandDTO")
interface VectorCreateCommandDTO {
    val path: FilePathDTO
    val file: ByteArray
    val metadata: Map<String, String>
}

/**
 * @d2 inherit
 */
@Serializable
data class VectorCreateCommandDTOBase(
    override val path: FilePathDTO,
    override val file: ByteArray,
    override val metadata: Map<String, String>
): VectorCreateCommandDTO

/**
 * @d2 event
 * @parent [VectorCreateFunction]
 */
@JsExport
@JsName("VectorCreatedEventDTO")
interface VectorCreatedEventDTO: Event

/**
 * @d2 inherit
 */
@Serializable
class VectorCreatedEventDTOBase: VectorCreatedEventDTO
