package io.komune.fs.script.core.service

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.Result
import io.minio.StatObjectArgs
import io.minio.messages.Item
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.InputStream

class FsScriptS3Service(
    private val minioClient: MinioClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun ensureBucket(bucketName: String) {
        try {
            val exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            )
            
            if (!exists) {
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
        } catch (e: Exception) {
            logger.error("Failed to ensure bucket: $bucketName", e)
            throw e
        }
    }

    suspend fun listObjects(bucketName: String, prefix: String = "", recursive: Boolean = false): List<Item> {
        ensureBucket(bucketName)
        
        return try {
            val results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(recursive)
                    .build()
            )
            
            results.map { it.get() }.toList()
        } catch (e: Exception) {
            logger.error("Failed to list objects in bucket: $bucketName, prefix: $prefix", e)
            throw e
        }
    }

    suspend fun statObject(bucketName: String, objectKey: String): io.minio.StatObjectResponse? {
        return try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectKey)
                    .build()
            )
        } catch (e: Exception) {
            logger.debug("Object not found: $bucketName/$objectKey", e)
            null
        }
    }

    suspend fun putObject(
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
                
            contentType?.let { builder.contentType(it) }
            
            minioClient.putObject(builder.build())
            logger.debug("Successfully uploaded object: $bucketName/$objectKey")
        } catch (e: Exception) {
            logger.error("Failed to put object: $bucketName/$objectKey", e)
            throw e
        }
    }

    suspend fun getObject(bucketName: String, objectKey: String): InputStream? {
        return try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectKey)
                    .build()
            )
        } catch (e: Exception) {
            logger.debug("Object not found: $bucketName/$objectKey", e)
            null
        }
    }
}
