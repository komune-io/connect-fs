package io.komune.fs.s2.file.domain

import io.komune.fs.s2.file.domain.model.FilePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class FilePathTest {

    @Test
    fun fromParsesAFullPath() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals("MyObject", path.objectType)
        assertEquals("id-1", path.objectId)
        assertEquals("image", path.directory)
        assertEquals("main.jpg", path.name)
    }

    @Test
    fun fromPadsMissingSegmentsWithEmptyStrings() {
        val path = FilePath.from("MyObject/id-1")

        assertEquals("MyObject", path.objectType)
        assertEquals("id-1", path.objectId)
        assertEquals("", path.directory)
        assertEquals("", path.name)
    }

    @Test
    fun fromKeepsSlashesInsideTheFileName() {
        // split has limit = 4, so anything past the third slash belongs to the name
        val path = FilePath.from("MyObject/id-1/image/nested/main.jpg")

        assertEquals("nested/main.jpg", path.name)
    }

    @Test
    fun toStringRebuildsTheOriginalPath() {
        val original = "MyObject/id-1/image/main.jpg"

        assertEquals(original, FilePath.from(original).toString())
    }

    @Test
    fun toStringTruncatesAtTheFirstEmptySegment() {
        val path = FilePath(objectType = "MyObject", objectId = "id-1", directory = "", name = "main.jpg")

        // "MyObject/id-1//main.jpg" -> everything from the doubled slash is dropped
        assertEquals("MyObject/id-1", path.toString())
    }

    @Test
    fun toStringDropsASingleTrailingSlash() {
        val path = FilePath(objectType = "MyObject", objectId = "id-1", directory = "image", name = "")

        assertEquals("MyObject/id-1/image", path.toString())
    }

    @Test
    fun toPartialPrefixAppendsATrailingSlashByDefault() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals("MyObject/id-1/image/main.jpg/", path.toPartialPrefix())
        assertEquals("MyObject/id-1/image/main.jpg", path.toPartialPrefix(trailingSlash = false))
    }

    @Test
    fun buildUrlInPathStylePutsTheBucketInThePath() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(
            "https://s3.example.com/my-bucket/MyObject/id-1/image/main.jpg",
            path.buildUrl(baseUrl = "https://s3.example.com/", bucket = "my-bucket", dnsStyle = false)
        )
    }

    @Test
    fun buildUrlInDnsStylePutsTheBucketInTheHost() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        assertEquals(
            "https://my-bucket.s3.example.com/MyObject/id-1/image/main.jpg",
            path.buildUrl(baseUrl = "https://s3.example.com", bucket = "my-bucket", dnsStyle = true)
        )
    }

    @Test
    fun serializesToStableFieldNames() {
        val path = FilePath.from("MyObject/id-1/image/main.jpg")

        val json = Json.encodeToString(FilePath.serializer(), path)

        // These names are published API — consumers deserialize them.
        assertEquals(
            """{"objectType":"MyObject","objectId":"id-1","directory":"image","name":"main.jpg"}""",
            json
        )
        assertEquals(path, Json.decodeFromString(FilePath.serializer(), json))
    }
}
