package io.komune.fs.script.migrate

import io.minio.BucketExistsArgs
import io.minio.DeleteBucketPolicyArgs
import io.minio.GetBucketPolicyArgs
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.SetBucketPolicyArgs
import io.minio.StatObjectArgs
import io.minio.errors.ErrorResponseException
import java.io.InputStream

/**
 * [MigrationStore] backed by the MinIO client, which works against any S3-compatible server
 * (MinIO and RustFS alike). Closing it closes the client.
 */
@Suppress("TooManyFunctions") // one per MigrationStore operation
class MinioMigrationStore(
    private val client: MinioClient,
) : MigrationStore, AutoCloseable {

    override fun listBuckets(): List<String> = client.listBuckets().map { it.name() }

    override fun bucketExists(bucket: String): Boolean =
        client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())

    override fun createBucket(bucket: String) {
        client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
    }

    override fun listKeys(bucket: String): Sequence<String> = ListObjectsArgs.builder()
        .bucket(bucket)
        .recursive(true)
        .build()
        .let(client::listObjects)
        .asSequence()
        .map { it.get() }
        .filterNot { it.isDir }
        .map { it.objectName() }

    override fun stat(bucket: String, key: String): ObjectInfo? = try {
        val stat = StatObjectArgs.builder()
            .bucket(bucket)
            .`object`(key)
            .build()
            .let(client::statObject)
        ObjectInfo(
            key = key,
            size = stat.size(),
            contentType = stat.contentType(),
            metadata = stat.userMetadata().associate { it.key to it.value }.normalizedMetadata(),
            etag = stat.etag(),
            lastModified = stat.lastModified()?.toInstant(),
        )
    } catch (e: ErrorResponseException) {
        if (e.errorResponse().code() in NOT_FOUND_CODES) null else throw e
    }

    override fun read(bucket: String, key: String): InputStream = GetObjectArgs.builder()
        .bucket(bucket)
        .`object`(key)
        .build()
        .let(client::getObject)

    override fun write(bucket: String, info: ObjectInfo, content: InputStream) {
        PutObjectArgs.builder()
            .bucket(bucket)
            .`object`(info.key)
            .stream(content, info.size, -1)
            .userMetadata(info.metadata)
            .apply { info.contentType?.let(::contentType) }
            .build()
            .let(client::putObject)
    }

    override fun remove(bucket: String, key: String) {
        RemoveObjectArgs.builder()
            .bucket(bucket)
            .`object`(key)
            .build()
            .let(client::removeObject)
    }

    override fun getBucketPolicy(bucket: String): String? = try {
        GetBucketPolicyArgs.builder()
            .bucket(bucket)
            .build()
            .let(client::getBucketPolicy)
            .ifBlank { null }
    } catch (e: ErrorResponseException) {
        if (e.errorResponse().code() == NO_SUCH_BUCKET_POLICY) null else throw e
    }

    override fun setBucketPolicy(bucket: String, policy: String) {
        SetBucketPolicyArgs.builder()
            .bucket(bucket)
            .config(policy)
            .build()
            .let(client::setBucketPolicy)
    }

    override fun deleteBucketPolicy(bucket: String) {
        DeleteBucketPolicyArgs.builder()
            .bucket(bucket)
            .build()
            .let(client::deleteBucketPolicy)
    }

    override fun close() = client.close()

    companion object {
        private val NOT_FOUND_CODES = setOf("NoSuchKey", "NoSuchObject")
        private const val NO_SUCH_BUCKET_POLICY = "NoSuchBucketPolicy"

        fun create(url: String, username: String, password: String, region: String? = null) = MinioMigrationStore(
            MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .apply { region?.let(::region) }
                .build()
        )
    }
}
