package io.komune.fs.s2.file.client

import io.komune.fs.s2.file.domain.model.FilePath
import kotlin.reflect.full.declaredMemberFunctions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * `FileClient` is published API consumed by connect-im and Trace. These pin the surface
 * so a rename shows up here rather than in a downstream build.
 */
class FileClientTest {

    @Test
    fun `exposes the documented operations`() {
        val functions = FileClient::class.declaredMemberFunctions.map { it.name }

        assertThat(functions).contains(
            "fileGet",
            "fileDownload",
            "fileList",
            "fileUpload",
            "fileDelete",
            "initPublicDirectory",
            "revokePublicDirectory",
            "fileAskQuestion"
        )
    }

    @Test
    fun `can be constructed with only a url, since auth is optional`() {
        val client = FileClient(url = "http://fs:8080")

        assertThat(client).isNotNull()
    }

    @Test
    fun `the upload file name comes from the path, not the caller`() {
        // fileUpload names the multipart part after command.path.name; this pins that
        // FilePath still exposes the name segment the client reads.
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertThat(path.name).isEqualTo("main.jpg")
    }
}
