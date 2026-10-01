package io.komune.fs.script.migrate

import io.komune.fs.script.core.config.properties.FsMigrateProperties
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Runs the migration between two real S3 servers. Both are RustFS: the MinIO server image is no
 * longer published, and the script only relies on standard S3 calls on the source side.
 */
@Testcontainers(disabledWithoutDocker = true)
class MigrateScriptRustfsTest {

    companion object {
        private const val IMAGE = "rustfs/rustfs:1.0.0"
        private const val ACCESS_KEY = "fsadmin"
        private const val SECRET_KEY = "fsadmin-secret"
        private const val BUCKET = "fs-sample"

        @Container
        @JvmStatic
        val sourceServer = rustfs()

        @Container
        @JvmStatic
        val targetServer = rustfs()

        private fun rustfs() = GenericContainer(IMAGE)
            .withEnv("RUSTFS_ACCESS_KEY", ACCESS_KEY)
            .withEnv("RUSTFS_SECRET_KEY", SECRET_KEY)
            .withExposedPorts(9000)
            .waitingFor(Wait.forHttp("/health").forPort(9000))

        private fun GenericContainer<*>.store() = MinioMigrationStore.create(
            url = "http://$host:${getMappedPort(9000)}",
            username = ACCESS_KEY,
            password = SECRET_KEY,
        )
    }

    private val source = sourceServer.store()
    private val target = targetServer.store()

    private val publicPolicy = """
        {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},
        "Action":["s3:GetObject"],"Resource":["arn:aws:s3:::$BUCKET/public/*"]}]}
    """.trimIndent().replace("\n", "")

    @Test
    fun `migrates objects, metadata and policy, then resumes without copying again`() : Unit = runBlocking {
        source.createBucket(BUCKET)
        source.write(BUCKET, "doc/1/contract.pdf", "pdf content", "application/pdf",
            mapOf("id" to "file-1", "vectorized" to "true"))
        source.write(BUCKET, "public/logo.svg", "<svg/>", "image/svg+xml", mapOf("id" to "file-2"))
        val big = ByteArray(12 * 1024 * 1024) { (it % 251).toByte() }
        source.write(BUCKET, ObjectInfo("big.bin", big.size.toLong(), "application/octet-stream", emptyMap()),
            big.inputStream())
        source.setBucketPolicy(BUCKET, publicPolicy)

        val properties = FsMigrateProperties(enabled = true, buckets = listOf(BUCKET))
        val first = MigrateScript(properties, source, target).run().buckets.single()

        assertThat(first.failed.get()).isZero()
        assertThat(first.copied.get()).isEqualTo(3)
        assertThat(first.policyCopied).isTrue()
        listOf("doc/1/contract.pdf", "public/logo.svg", "big.bin").forEach { key ->
            assertThat(target.stat(BUCKET, key)).isEqualTo(source.stat(BUCKET, key))
        }
        assertThat(target.stat(BUCKET, "doc/1/contract.pdf")?.metadata)
            .isEqualTo(mapOf("id" to "file-1", "vectorized" to "true"))
        assertThat(target.read(BUCKET, "big.bin").use { it.readBytes() }).isEqualTo(big)
        assertThat(target.getBucketPolicy(BUCKET)).contains("arn:aws:s3:::$BUCKET/public/*")

        source.write(BUCKET, "doc/1/added.txt", "new", "text/plain", mapOf("id" to "file-3"))
        source.remove(BUCKET, "public/logo.svg")
        val second = MigrateScript(properties.copy(deleteExtraneous = true), source, target).run().buckets.single()

        assertThat(second.failed.get()).isZero()
        assertThat(second.copied.get()).isEqualTo(1)
        assertThat(second.skipped.get()).isEqualTo(2)
        assertThat(second.deleted.get()).isEqualTo(1)
        assertThat(target.stat(BUCKET, "public/logo.svg")).isNull()
        assertThat(target.stat(BUCKET, "doc/1/added.txt")).isNotNull()
    }

    private fun MigrationStore.write(
        bucket: String, key: String, content: String, contentType: String, metadata: Map<String, String>,
    ) {
        val bytes = content.toByteArray()
        write(bucket, ObjectInfo(key, bytes.size.toLong(), contentType, metadata), bytes.inputStream())
    }
}
