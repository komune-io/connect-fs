package io.komune.fs.s2.file.app.service

import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.commons.utils.parseJsonTo
import io.komune.fs.commons.utils.toJson
import io.komune.fs.s2.file.app.model.Policy
import io.komune.fs.s2.file.app.model.sanitizedMetadata
import io.minio.BucketExistsArgs
import io.minio.CopyObjectArgs
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
import io.minio.SourceObject
import io.minio.StatObjectArgs
import io.minio.StatObjectResponse
import io.minio.errors.ErrorResponseException
import io.minio.messages.Item
import java.net.URLConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private const val NO_SUCH_KEY = "NoSuchKey"

@Service
class S3Service(
    private val minioClient: MinioClient,
    private val s3BucketProvider: S3BucketProvider
) {
    private val logger: Logger = LoggerFactory.getLogger(S3Service::class.java)

    suspend fun putObject(
        path: String,
        content: ByteArray,
        metadata: Map<String, String>,
    ): Unit = withBucket { bucket ->
        val contentType = metadata.entries.firstOrNull { (key) -> key.lowercase() == "content-type" }
            ?.value
            ?: URLConnection.guessContentTypeFromName(path)

        val args = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`(path)
            .stream(content.inputStream(), content.size.toLong(), -1)
            .userMetadata(metadata)
        if (contentType != null) {
            args.contentType(contentType)
        }
        minioClient.putObject(args.build())
    }

    suspend fun removeObject(path: String): Unit = withBucket { bucket ->
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .`object`(path)
                .build()
        )
    }

    suspend fun copyObject(path: String, metadata: Map<String, String>): Unit = withBucket { bucket ->
        val source = SourceObject.builder()
            .bucket(bucket)
            .`object`(path)
            .build()

        minioClient.copyObject(
            CopyObjectArgs.builder()
                .bucket(bucket)
                .source(source)
                .`object`(path)
                .metadataDirective(Directive.REPLACE)
                .userMetadata(metadata)
                .build()
        )
    }

    suspend fun listObjects(prefix: String, isRecursive: Boolean): Iterable<Result<Item>> = withBucket { bucket ->
        minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .recursive(isRecursive)
                .includeUserMetadata(true)
                .build()
        )
    }

    suspend fun getObjectMetadata(path: String): Map<String, String>? {
        logger.debug("Getting metadata for {}", path)
        return statObject(path)
            ?.userMetadata()
            ?.sanitizedMetadata()
            .also { logger.debug("Got metadata for {}: {}", path, it) }
    }

    suspend fun statObject(path: String): StatObjectResponse? = withBucket { bucket ->
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .build()
            )
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code() == NO_SUCH_KEY) {
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getObject(path: String): GetObjectResponse? = withBucket { bucket ->
        try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .build()
            )
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code() == NO_SUCH_KEY) {
                logger.debug("No object at {}", path)
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getBucketPolicy(): Policy? = withBucket { bucket ->
        minioClient.getBucketPolicy(
            GetBucketPolicyArgs.builder()
                .bucket(bucket)
                .build()
        )
            .ifBlank { null }
            ?.parseJsonTo(Policy::class.java)
    }

    suspend fun setBucketPolicy(policy: Policy): Unit = withBucket { bucket ->
        minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(bucket)
                .config(policy.toJson())
                .build()
        )
    }

    private suspend fun <R> withBucket(execute: suspend (bucket: String) -> R): R {
        val bucket = s3BucketProvider.getBucket()
        val isExisting = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucket).build()
        )
        if (!isExisting) {
            logger.info("Initializing bucket {}", bucket)
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucket).build()
            )
        }
        return execute(bucket)
    }
}
