package io.komune.fs.s2.file.domain

import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.features.query.FileListQuery
import io.komune.fs.s2.file.domain.model.File
import io.komune.fs.s2.file.domain.model.FilePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class WireNamesTest {

    private val path = FilePath(objectType = "MyObject", objectId = "id-1", directory = "image", name = "main.jpg")

    @Test
    fun fileKeepsTheVectorizedKey() {
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

        val encoded = Json.encodeToString(File.serializer(), file)

        assertTrue(encoded.contains(""""vectorized":true"""), encoded)
        assertEquals(expected = file, actual = Json.decodeFromString(File.serializer(), encoded))
    }

    @Test
    fun uploadCommandKeepsTheNullableVectorizeKey() {
        val json = """{"path":{"objectType":"o","objectId":"i","directory":"d","name":"n"},"vectorize":true}"""

        val decoded = Json.decodeFromString(FileUploadCommand.serializer(), json)

        assertEquals(expected = true, actual = decoded.vectorize)
        val nullJson = json.replace(oldValue = "true", newValue = "null")
        val withNull = Json.decodeFromString(FileUploadCommand.serializer(), nullJson)
        assertEquals(expected = null, actual = withNull.vectorize)
    }

    @Test
    fun listQueryKeepsTheRecursiveKey() {
        val json = """{"objectType":"o","objectId":"i","directory":"d","recursive":false}"""

        val decoded = Json.decodeFromString(FileListQuery.serializer(), json)

        assertEquals(expected = false, actual = decoded.recursive)
        assertTrue(Json.encodeToString(FileListQuery.serializer(), decoded).contains(""""recursive":false"""))
    }
}
