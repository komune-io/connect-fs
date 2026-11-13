package io.komune.fs.s2.file.app

import io.komune.fs.commons.utils.jsonMapper
import io.komune.fs.commons.utils.parseJsonTo
import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.features.query.FileListQuery
import io.komune.fs.s2.file.domain.model.File
import io.komune.fs.s2.file.domain.model.FilePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WireNamesJacksonTest {

    private val path = FilePath(objectType = "o", objectId = "i", directory = "d", name = "n")

    @Test
    fun `file keeps the vectorized key`() {
        val file = File(
            id = "file-1",
            path = path,
            pathStr = path.toString(),
            url = "http://s3/b/$path",
            metadata = emptyMap(),
            isDirectory = false,
            size = 1,
            vectorized = true,
            lastModificationDate = 0,
        )

        val encoded = jsonMapper.writeValueAsString(file)

        assertThat(encoded).contains(""""vectorized":true""")
        assertThat(encoded.parseJsonTo(File::class.java)).isEqualTo(file)
    }

    @Test
    fun `upload command keeps the nullable vectorize key`() {
        val json = """{"path":{"objectType":"o","objectId":"i","directory":"d","name":"n"},"vectorize":true}"""

        assertThat(json.parseJsonTo(FileUploadCommand::class.java).vectorize).isTrue()
        val withNull = json.replace(oldValue = "true", newValue = "null")
        assertThat(withNull.parseJsonTo(FileUploadCommand::class.java).vectorize).isNull()
        assertThat(jsonMapper.writeValueAsString(FileUploadCommand(path = path, vectorize = true)))
            .contains(""""vectorize":true""")
    }

    @Test
    fun `list query keeps the recursive key`() {
        val json = """{"objectType":"o","objectId":"i","directory":"d","recursive":false}"""

        val decoded = json.parseJsonTo(FileListQuery::class.java)

        assertThat(decoded.recursive).isFalse()
        assertThat(jsonMapper.writeValueAsString(decoded)).contains(""""recursive":false""")
    }
}
