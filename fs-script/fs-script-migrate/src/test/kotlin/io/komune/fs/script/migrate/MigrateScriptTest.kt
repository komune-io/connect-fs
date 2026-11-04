package io.komune.fs.script.migrate

import io.komune.fs.script.core.config.properties.FsMigrateProperties
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MigrateScriptTest {

    private val source = InMemoryMigrationStore()
    private val target = InMemoryMigrationStore()

    private fun script(properties: FsMigrateProperties = FsMigrateProperties(enabled = true)) =
        MigrateScript(properties, source, target)

    @Test
    fun `copies every object with its content type and user metadata`() = runTest {
        source.put("fs", "doc/1/contract.pdf", "pdf", "application/pdf",
            mapOf("id" to "file-1", "vectorized" to "true"))
        source.put("fs", "doc/1/notes.md", "md", "text/markdown", mapOf("id" to "file-2"))

        val report = script().run()

        assertThat(target.keys("fs")).containsExactlyInAnyOrder("doc/1/contract.pdf", "doc/1/notes.md")
        assertThat(target.content("fs", "doc/1/contract.pdf")).isEqualTo("pdf")
        assertThat(target.stat("fs", "doc/1/contract.pdf")?.copy(lastModified = null))
            .isEqualTo(source.stat("fs", "doc/1/contract.pdf")?.copy(lastModified = null))
        assertThat(report.buckets.single().copied.get()).isEqualTo(2)
        assertThat(report.hasFailures).isFalse()
    }

    @Test
    fun `migrates every source bucket when none is configured`() = runTest {
        source.put("a", "x", "1")
        source.put("b", "y", "2")

        script().run()

        assertThat(target.listBuckets()).containsExactly("a", "b")
    }

    @Test
    fun `migrates only the configured buckets`() = runTest {
        source.put("a", "x", "1")
        source.put("b", "y", "2")

        script(FsMigrateProperties(enabled = true, buckets = listOf("b"))).run()

        assertThat(target.listBuckets()).containsExactly("b")
    }

    @Test
    fun `fails on a configured bucket missing from the source`() = runTest {
        val error = runCatching { script(FsMigrateProperties(buckets = listOf("nope"))).run() }.exceptionOrNull()

        assertThat(error).hasMessageContaining("nope")
    }

    @Test
    fun `copies the bucket policy`() = runTest {
        source.put("fs", "x", "1")
        source.setBucketPolicy("fs", """{"Version":"2012-10-17","Statement":[]}""")

        val report = script().run()

        assertThat(target.getBucketPolicy("fs")).isEqualTo("""{"Version":"2012-10-17","Statement":[]}""")
        assertThat(report.buckets.single().policyCopied).isTrue()
    }

    @Test
    fun `a second run skips what is already copied and copies what changed`() = runTest {
        source.put("fs", "same", "1", metadata = mapOf("id" to "a"))
        source.put("fs", "changed", "2", metadata = mapOf("id" to "b"))
        script().run()
        val writesAfterFirstRun = target.writes

        source.put("fs", "changed", "2", metadata = mapOf("id" to "b", "vectorized" to "true"))
        source.put("fs", "new", "3")
        val report = script().run().buckets.single()

        assertThat(target.writes - writesAfterFirstRun).isEqualTo(2)
        assertThat(report.copied.get()).isEqualTo(2)
        assertThat(report.skipped.get()).isEqualTo(1)
        assertThat(target.stat("fs", "changed")?.metadata).containsEntry("vectorized", "true")
    }

    @Test
    fun `copies again an object overwritten with other bytes of the same size`() = runTest {
        source.put("fs", "doc", "aaa", metadata = mapOf("id" to "a"))
        script().run()

        source.put("fs", "doc", "bbb", metadata = mapOf("id" to "a"))
        val report = script().run().buckets.single()

        assertThat(report.copied.get()).isEqualTo(1)
        assertThat(target.content("fs", "doc")).isEqualTo("bbb")
    }

    @Test
    fun `overwrites a target object holding other bytes, even when written after the source`() = runTest {
        source.put("fs", "doc", "aaa", metadata = mapOf("id" to "a"))
        target.put("fs", "doc", "bbb", metadata = mapOf("id" to "a"))

        val report = script().run().buckets.single()

        assertThat(report.copied.get()).isEqualTo(1)
        assertThat(target.content("fs", "doc")).isEqualTo("aaa")
    }

    @Test
    fun `copies again an object whose ETag differs, as after a multipart upload with another part size`() =
        runTest {
            source.put("fs", "big", "content")
            script().run()
            target.setEtag("fs", "big", "d41d8cd98f00b204e9800998ecf8427e-3")

            val report = script().run().buckets.single()

            assertThat(report.copied.get()).isEqualTo(1)
        }

    @Test
    fun `removes the target policy when the source has none`() = runTest {
        source.put("fs", "x", "1")
        source.setBucketPolicy("fs", "{}")
        script().run()

        source.deleteBucketPolicy("fs")
        val report = script().run().buckets.single()

        assertThat(target.getBucketPolicy("fs")).isNull()
        assertThat(report.policyCopied).isTrue()
    }

    @Test
    fun `a dry run does not remove the target policy`() = runTest {
        source.put("fs", "x", "1")
        target.put("fs", "x", "1")
        target.setBucketPolicy("fs", "{}")

        script(FsMigrateProperties(enabled = true, dryRun = true)).run()

        assertThat(target.getBucketPolicy("fs")).isEqualTo("{}")
    }

    @Test
    fun `a dry run writes nothing`() = runTest {
        source.put("fs", "x", "1")
        source.setBucketPolicy("fs", "{}")

        val report = script(FsMigrateProperties(enabled = true, dryRun = true)).run().buckets.single()

        assertThat(target.listBuckets()).isEmpty()
        assertThat(report.copied.get()).isEqualTo(1)
        assertThat(report.policyCopied).isTrue()
    }

    @Test
    fun `deletes target objects gone from the source only when asked to`() = runTest {
        source.put("fs", "kept", "1")
        target.put("fs", "stale", "2")

        script().run()
        assertThat(target.keys("fs")).containsExactlyInAnyOrder("kept", "stale")

        val report = script(FsMigrateProperties(enabled = true, deleteExtraneous = true)).run().buckets.single()
        assertThat(target.keys("fs")).containsExactly("kept")
        assertThat(report.deleted.get()).isEqualTo(1)
    }

    @Test
    fun `counts a failing object and keeps copying the others`() = runTest {
        source.put("fs", "broken", "1")
        source.put("fs", "fine", "2")
        source.failingKeys += "broken"

        val report = script(FsMigrateProperties(enabled = true, concurrency = 1)).run()

        assertThat(report.hasFailures).isTrue()
        assertThat(report.buckets.single().failed.get()).isEqualTo(1)
        assertThat(target.keys("fs")).containsExactly("fine")
    }

    @Test
    fun `normalizes metadata keys from S3 headers`() {
        val raw = mapOf("X-Amz-Meta-Id" to "file-1", "x-amz-meta-vectorized" to "true", "missing" to null)

        assertThat(raw.normalizedMetadata()).isEqualTo(mapOf("id" to "file-1", "vectorized" to "true"))
    }
}
