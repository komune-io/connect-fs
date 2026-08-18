package io.komune.fs.spring.utils

import io.komune.fs.s2.file.domain.model.FilePath
import java.security.MessageDigest
import java.util.Base64
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileUtilsTest {

    private val path = FilePath.from("MyObject/id-1/image/main.jpg")

    @Test
    fun `toUploadCommand carries the path through`() {
        val command = path.toUploadCommand()

        assertThat(command.path).isEqualTo(path)
    }

    @Test
    fun `toUploadCommand always stamps uploadedAt`() {
        val before = System.currentTimeMillis()

        val command = path.toUploadCommand()

        val uploadedAt = command.metadata["uploadedAt"]
        assertThat(uploadedAt).isNotNull()
        assertThat(uploadedAt!!.toLong()).isBetween(before, System.currentTimeMillis())
    }

    @Test
    fun `caller metadata wins over the stamped uploadedAt`() {
        val command = path.toUploadCommand(metadata = mapOf("uploadedAt" to "0", "custom" to "x"))

        assertThat(command.metadata["uploadedAt"]).isEqualTo("0")
        assertThat(command.metadata["custom"]).isEqualTo("x")
    }

    @Test
    fun `vectorize defaults to false and is passed through when set`() {
        assertThat(path.toUploadCommand().vectorize).isFalse()
        assertThat(path.toUploadCommand(vectorize = true).vectorize).isTrue()
        assertThat(path.toUploadCommand(vectorize = null).vectorize).isNull()
    }

    @Test
    fun `hash is base64-encoded SHA-256`() {
        val content = "hello".toByteArray()

        val expected = Base64.getEncoder()
            .encodeToString(MessageDigest.getInstance("SHA-256").digest(content))

        assertThat(content.hash()).isEqualTo(expected)
    }

    @Test
    fun `hash is stable and differs on different content`() {
        assertThat("a".toByteArray().hash()).isEqualTo("a".toByteArray().hash())
        assertThat("a".toByteArray().hash()).isNotEqualTo("b".toByteArray().hash())
    }

    @Test
    fun `encodeToB64 then decodeB64 round-trips`() {
        val content = "some bytes".toByteArray()

        assertThat(content.encodeToB64().decodeB64()).isEqualTo(content)
    }

    @Test
    fun `decodeB64 strips a data-url prefix`() {
        val encoded = "png-bytes".toByteArray().encodeToB64()

        val decoded = "data:image/png;base64,$encoded".decodeB64()

        assertThat(decoded).isEqualTo("png-bytes".toByteArray())
    }
}
