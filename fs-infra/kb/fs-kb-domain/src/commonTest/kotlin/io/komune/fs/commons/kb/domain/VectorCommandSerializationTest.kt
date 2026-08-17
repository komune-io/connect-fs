package io.komune.fs.commons.kb.domain

import io.komune.fs.commons.kb.domain.command.VectorCreateCommandDTOBase
import io.komune.fs.s2.file.domain.model.FilePath
import io.komune.fs.s2.file.domain.model.FilePathDTO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * `VectorCreateCommandDTOBase.path` is declared as the [FilePathDTO] *interface*, so
 * kotlinx treats it as a polymorphic field. A caller therefore has to register the
 * concrete [FilePath] subclass before this DTO can be encoded at all — see
 * [plainJsonCannotEncodeThePolymorphicPathField], which pins that.
 *
 * Nothing in connect-fs relies on this today: `KbClient.vectorCreateFunction` sends the
 * command as multipart form data with `path.toString()`.
 */
class VectorCommandSerializationTest {

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(FilePathDTO::class) { subclass(FilePath.serializer()) }
        }
    }

    private val command = VectorCreateCommandDTOBase(
        path = FilePath.from("MyObject/id-1/image/main.jpg"),
        file = byteArrayOf(1, 2, 3),
        metadata = mapOf("owner" to "team-a")
    )

    @Test
    fun createCommandRoundTripsWhenTheSubclassIsRegistered() {
        val encoded = json.encodeToString(VectorCreateCommandDTOBase.serializer(), command)
        val decoded = json.decodeFromString(VectorCreateCommandDTOBase.serializer(), encoded)

        assertEquals(command.path, decoded.path)
        assertEquals(command.metadata, decoded.metadata)
        assertTrue(command.file.contentEquals(decoded.file))
    }

    @Test
    fun createCommandKeepsItsPublishedFieldNames() {
        val encoded = json.encodeToString(VectorCreateCommandDTOBase.serializer(), command)

        assertTrue(encoded.contains("\"path\""), encoded)
        assertTrue(encoded.contains("\"file\""), encoded)
        assertTrue(encoded.contains("\"metadata\""), encoded)
    }

    @Test
    fun theDiscriminatorForFilePathIsStable() {
        val encoded = json.encodeToString(VectorCreateCommandDTOBase.serializer(), command)

        // @SerialName("FilePath") — changing it breaks anyone already decoding this shape.
        assertTrue(encoded.contains("\"FilePath\""), encoded)
    }

    @Test
    fun emptyMetadataSurvivesTheRoundTrip() {
        val empty = command.copy(metadata = emptyMap())

        val decoded = json.decodeFromString(
            VectorCreateCommandDTOBase.serializer(),
            json.encodeToString(VectorCreateCommandDTOBase.serializer(), empty)
        )

        assertEquals(emptyMap(), decoded.metadata)
    }

    @Test
    fun plainJsonCannotEncodeThePolymorphicPathField() {
        assertFailsWith<SerializationException> {
            Json.encodeToString(VectorCreateCommandDTOBase.serializer(), command)
        }
    }
}
