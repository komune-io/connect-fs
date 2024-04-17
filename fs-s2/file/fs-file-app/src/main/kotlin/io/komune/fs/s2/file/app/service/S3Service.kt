package io.komune.fs.s2.file.app.service

import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.s2.file.app.model.Policy
import io.komune.fs.s2.file.app.utils.parseJsonTo
import io.komune.fs.s2.file.app.utils.toJson
import io.minio.BucketExistsArgs
import io.minio.CopyObjectArgs
import io.minio.CopySource
import io.minio.Directive
import io.minio.GetBucketPolicyArgs
import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.Result
import io.minio.SetBucketPolicyArgs
import io.minio.StatObjectArgs
import io.minio.StatObjectResponse
import io.minio.errors.ErrorResponseException
import io.minio.messages.Item
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URLConnection

@Service
class S3Service(
    private val minioClient: MinioClient,
    private val s3BucketProvider: S3BucketProvider
) {
    private val logger = LoggerFactory.getLogger(S3Service::class.java)

    suspend fun putObject(path: String, content: ByteArray, metadata: Map<String, String>) = withBucket { bucket ->
        val contentType = metadata.entries.firstOrNull { (key) -> key.lowercase() == "content-type" }
            ?.value
            ?: URLConnection.guessContentTypeFromName(path)

        PutObjectArgs.builder()
            .bucket(bucket)
            .`object`(path)
            .stream(content.inputStream(), content.size.toLong(), -1)
            .userMetadata(metadata)
            .apply { contentType?.let(this::contentType) }
            .build()
            .let(minioClient::putObject)
    }

    suspend fun removeObject(path: String) = withBucket { bucket ->
        RemoveObjectArgs.builder()
            .bucket(bucket)
            .`object`(path)
            .build()
            .let(minioClient::removeObject)
    }

    suspend fun copyObject(path: String, metadata: Map<String, String>) = withBucket { bucket ->
        val source = CopySource.builder()
            .bucket(bucket)
            .`object`(path)
            .build()

        CopyObjectArgs.builder()
            .bucket(bucket)
            .source(source)
            .`object`(path)
            .metadataDirective(Directive.REPLACE)
            .userMetadata(metadata)
            .build()
            .let(minioClient::copyObject)
    }

    suspend fun listObjects(prefix: String, recursive: Boolean): Iterable<Result<Item>> = withBucket { bucket ->
        ListObjectsArgs.builder()
            .bucket(bucket)
            .prefix(prefix)
            .recursive(recursive)
            .includeUserMetadata(true)
            .build()
            .let(minioClient::listObjects)
    }

    suspend fun getObjectMetadata(path: String): Map<String, String>? = withBucket { bucket ->
        logger.debug("Getting metadata for $path")
        statObject(path)
            ?.userMetadata()
            ?.mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
            .also { logger.debug("Got metadata for $path: $it") }
    }

    suspend fun statObject(path: String): StatObjectResponse? = withBucket { bucket ->
        try {
            StatObjectArgs.builder()
                .bucket(bucket)
                .`object`(path)
                .build()
                .let(minioClient::statObject)
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code() == "NoSuchKey") {
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getObject(path: String): GetObjectResponse? = withBucket { bucket ->
        try {
            GetObjectArgs.builder()
                .bucket(bucket)
                .`object`(path)
                .build()
                .let(minioClient::getObject)
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code() == "NoSuchKey") {
                println("NoSuchKey")
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getBucketPolicy(): Policy? = withBucket { bucket ->
        GetBucketPolicyArgs.builder()
            .bucket(bucket)
            .build()
            .let(minioClient::getBucketPolicy)
            .ifBlank { null }
            ?.parseJsonTo(Policy::class.java)
    }

    suspend fun setBucketPolicy(policy: Policy) = withBucket { bucket ->
        SetBucketPolicyArgs.builder()
            .bucket(bucket)
            .config(policy.toJson())
            .build()
            .let(minioClient::setBucketPolicy)
    }

    private suspend fun <R> withBucket(execute: suspend (bucket: String) -> R): R {
        val bucket = s3BucketProvider.getBucket()
        val exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucket).build()
        )
        if (!exists) {
            logger.info("Initializing bucket $bucket")
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucket).build()
            )
        }
        return execute(bucket)
    }
}
