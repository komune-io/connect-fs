package io.komune.fs.script.core.service

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.StatObjectArgs
import io.minio.StatObjectResponse
import io.minio.errors.ErrorResponseException
import io.minio.errors.MinioException
import io.minio.messages.Item
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FsScriptS3Service(
    private val minioClient: MinioClient
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun ensureBucket(bucketName: String) {
        try {
            val isExisting = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            )

            if (!isExisting) {
                logger.info("Creating bucket: $bucketName")
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
                logger.info("Successfully created bucket: $bucketName")
            } else {
                logger.debug("Bucket already exists: $bucketName")
            }
        } catch (e: MinioException) {
            logger.error("Failed to ensure bucket: $bucketName", e)
            throw e
        } catch (e: IOException) {
            logger.error("Failed to ensure bucket: $bucketName", e)
            throw e
        }
    }

    fun listObjects(bucketName: String, prefix: String = "", isRecursive: Boolean = false): List<Item> {
        ensureBucket(bucketName)

        return try {
            val results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(isRecursive)
                    .build()
            )

            results.map { it.get() }.toList()
        } catch (e: MinioException) {
            logger.error("Failed to list objects in bucket: $bucketName, prefix: $prefix", e)
            throw e
        } catch (e: IOException) {
            logger.error("Failed to list objects in bucket: $bucketName, prefix: $prefix", e)
            throw e
        }
    }

    fun statObject(bucketName: String, objectKey: String): StatObjectResponse? {
        return try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectKey)
                    .build()
            )
        } catch (e: ErrorResponseException) {
            logger.debug("Object not found: $bucketName/$objectKey", e)
            null
        }
    }

    fun putObject(
        bucketName: String,
        objectKey: String,
        content: ByteArray,
        contentType: String? = null,
        metadata: Map<String, String> = emptyMap()
    ) {
        ensureBucket(bucketName)

        try {
            val inputStream = ByteArrayInputStream(content)
            val builder = PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectKey)
                .stream(inputStream, content.size.toLong(), -1)
                .userMetadata(metadata)

            if (contentType != null) {
                builder.contentType(contentType)
            }

            minioClient.putObject(builder.build())
            logger.debug("Successfully uploaded object: $bucketName/$objectKey")
        } catch (e: MinioException) {
            logger.error("Failed to put object: $bucketName/$objectKey", e)
            throw e
        } catch (e: IOException) {
            logger.error("Failed to put object: $bucketName/$objectKey", e)
            throw e
        }
    }

    fun getObject(bucketName: String, objectKey: String): InputStream? {
        return try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectKey)
                    .build()
            )
        } catch (e: ErrorResponseException) {
            logger.debug("Object not found: $bucketName/$objectKey", e)
            null
        }
    }
}
