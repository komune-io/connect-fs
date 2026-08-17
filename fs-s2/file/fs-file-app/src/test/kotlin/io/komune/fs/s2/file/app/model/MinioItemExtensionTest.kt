package io.komune.fs.s2.file.app.model

import io.minio.Http
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MinioItemExtensionTest {

    @Test
    fun `sanitizedMetadata strips the x-amz-meta prefix and lowercases keys`() {
        val raw = mapOf<String, String?>(
            "X-Amz-Meta-Id" to "file-1",
            "x-amz-meta-vectorized" to "true"
        )

        assertThat(raw.sanitizedMetadata()).containsExactlyInAnyOrderEntriesOf(
            mapOf("id" to "file-1", "vectorized" to "true")
        )
    }

    @Test
    fun `sanitizedMetadata drops null values`() {
        val raw = mapOf("x-amz-meta-id" to "file-1", "x-amz-meta-missing" to null)

        assertThat(raw.sanitizedMetadata()).containsOnlyKeys("id")
    }

    @Test
    fun `sanitizedMetadata leaves keys without the prefix alone apart from case`() {
        val raw = mapOf<String, String?>("Content-Type" to "image/jpeg")

        assertThat(raw.sanitizedMetadata()).containsEntry("content-type", "image/jpeg")
    }

    @Test
    fun `sanitizedMetadata of an empty map is empty`() {
        assertThat(emptyMap<String, String?>().sanitizedMetadata()).isEmpty()
    }

    // minio 9 changed StatObjectResponse.userMetadata() from Map<String, String> to
    // Http.Headers. These two pin the conversion so the change cannot silently regress.

    @Test
    fun `Http Headers convert to a metadata map`() {
        val headers = Http.Headers(mapOf("X-Amz-Meta-Id" to "file-1"))

        assertThat(headers.toMetadataMap()).containsEntry("X-Amz-Meta-Id", "file-1")
    }

    @Test
    fun `Http Headers sanitize the same way a plain map does`() {
        val headers = Http.Headers(mapOf("X-Amz-Meta-Id" to "file-1", "x-amz-meta-vectorized" to "false"))

        assertThat(headers.sanitizedMetadata()).containsExactlyInAnyOrderEntriesOf(
            mapOf("id" to "file-1", "vectorized" to "false")
        )
    }
}
