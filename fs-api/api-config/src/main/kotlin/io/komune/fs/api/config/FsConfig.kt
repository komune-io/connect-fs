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
    fun s3BucketProvider(fsProperties: FsProperties): S3BucketProvider {
        return S3BucketProvider(fsProperties)
    }

    @Bean
    fun minioClient(fsProperties: FsProperties): MinioClient = MinioClient.builder()
        .endpoint(fsProperties.s3.internalUrl)
        .region(fsProperties.s3.region)
        .credentials(fsProperties.s3.username, fsProperties.s3.password)
        .build()
        .apply {
            if (fsProperties.s3.dns) { enableVirtualStyleEndpoint() }
            else { disableVirtualStyleEndpoint() }
        }

    @Bean
    fun initApplication(
        fsProperties: FsProperties,
        minioClient: MinioClient
    ) = CommandLineRunner {
        fsProperties.init?.buckets?.let { buckets ->
            buckets.split(",").map{
                it.trim()
            }.forEach { bucket ->
                val exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
                )
                if(!exists) {
                    logger.info("Initializing bucket $bucket.")
                    minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build()
                    )
                }
            }
        }
    }

}

