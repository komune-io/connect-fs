package io.komune.fs.script.core.config

import io.komune.fs.script.core.config.properties.FsS3Properties
import io.komune.fs.script.core.service.FsScriptS3Service
import io.minio.MinioClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FsS3Properties::class)
class FsScriptS3Config {

    @Bean
    fun minioClient(s3Properties: FsS3Properties): MinioClient {
        return MinioClient.builder()
            .endpoint(s3Properties.internalUrl)
            .credentials(s3Properties.username, s3Properties.password)
            .build()
    }

    @Bean
    fun fsScriptS3Service(minioClient: MinioClient): FsScriptS3Service {
        return FsScriptS3Service(minioClient)
    }
}
