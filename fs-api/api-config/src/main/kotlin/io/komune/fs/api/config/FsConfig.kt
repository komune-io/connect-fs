package io.komune.fs.api.config

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FsProperties::class)
class FsConfig {

    private val logger = LoggerFactory.getLogger(FsConfig::class.java)

    @Bean
    fun s3Properties(fsProperties: FsProperties): S3Properties = fsProperties.s3

    @Bean
    fun s3BucketProvider(fsProperties: FsProperties): S3BucketProvider = S3BucketProvider(fsProperties)

    @Bean
    fun minioClient(fsProperties: FsProperties): MinioClient = MinioClient.builder()
        .endpoint(fsProperties.s3.internalUrl)
        .region(fsProperties.s3.region)
        .credentials(fsProperties.s3.username, fsProperties.s3.password)
        .build()
        .apply {
            if (fsProperties.s3.isDns) { enableVirtualStyleEndpoint() }
            else { disableVirtualStyleEndpoint() }
        }

    @Bean
    fun initApplication(
        fsProperties: FsProperties,
        minioClient: MinioClient
    ) = CommandLineRunner {
        val buckets = fsProperties.init?.buckets
        if (buckets != null) {
            buckets.split(",").map(String::trim).forEach { bucket -> minioClient.initBucket(bucket) }
        }
    }

    private fun MinioClient.initBucket(bucket: String) {
        val isExisting = bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
        if (!isExisting) {
            logger.info("Initializing bucket $bucket.")
            makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
        }
    }

}

