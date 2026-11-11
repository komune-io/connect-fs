package io.komune.fs.s2.file.domain

import io.komune.fs.s2.file.domain.model.FilePath
import io.komune.fs.s2.file.domain.model.buildUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class FilePathTest {

    @Test
    fun fromParsesAFullPath() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(expected = "MyObject", actual = path.objectType)
        assertEquals(expected = "id-1", actual = path.objectId)
        assertEquals(expected = "image", actual = path.directory)
        assertEquals(expected = "main.jpg", actual = path.name)
    }

    @Test
    fun fromPadsMissingSegmentsWithEmptyStrings() {
        val path = FilePath.from("MyObject/id-1")

        assertEquals(expected = "MyObject", actual = path.objectType)
        assertEquals(expected = "id-1", actual = path.objectId)
        assertEquals(expected = "", actual = path.directory)
        assertEquals(expected = "", actual = path.name)
    }

    @Test
    fun fromKeepsSlashesInsideTheFileName() {
        // split has limit = 4, so anything past the third slash belongs to the name
        val path = FilePath.from("MyObject/id-1/image/nested/main.jpg")

        assertEquals(expected = "nested/main.jpg", actual = path.name)
    }

    @Test
    fun toStringRebuildsTheOriginalPath() {
        val original = "MyObject/id-1/image/main.jpg"

        assertEquals(expected = original, actual = FilePath.from(original).toString())
    }

    @Test
    fun toStringTruncatesAtTheFirstEmptySegment() {
        val path = FilePath(objectType = "MyObject", objectId = "id-1", directory = "", name = "main.jpg")

        // "MyObject/id-1//main.jpg" -> everything from the doubled slash is dropped
        assertEquals(expected = "MyObject/id-1", actual = path.toString())
    }

    @Test
    fun toStringDropsASingleTrailingSlash() {
        val path = FilePath(objectType = "MyObject", objectId = "id-1", directory = "image", name = "")

        assertEquals(expected = "MyObject/id-1/image", actual = path.toString())
    }

    @Test
    fun toPartialPrefixAppendsATrailingSlashByDefault() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(expected = "MyObject/id-1/image/main.jpg/", actual = path.toPartialPrefix())
        assertEquals(expected = "MyObject/id-1/image/main.jpg", actual = path.toPartialPrefix(trailingSlash = false))
    }

    @Test
    fun buildUrlInPathStylePutsTheBucketInThePath() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(
            expected = "https://s3.example.com/my-bucket/MyObject/id-1/image/main.jpg",
            actual = path.buildUrl(baseUrl = "https://s3.example.com/", bucket = "my-bucket", dnsStyle = false)
        )
    }

    @Test
    fun buildUrlInDnsStylePutsTheBucketInTheHost() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(
            expected = "https://my-bucket.s3.example.com/MyObject/id-1/image/main.jpg",
            actual = path.buildUrl(baseUrl = "https://s3.example.com", bucket = "my-bucket", dnsStyle = true)
        )
    }

    @Test
    fun serializesToStableFieldNames() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        val json = Json.encodeToString(FilePath.serializer(), path)

        // These names are published API — consumers deserialize them.
        assertEquals(
            expected = """{"objectType":"MyObject","objectId":"id-1","directory":"image","name":"main.jpg"}""",
            actual = json
        )
        assertEquals(expected = path, actual = Json.decodeFromString(FilePath.serializer(), json))
    }
}
