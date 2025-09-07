package io.komune.fs.script.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs.script.s3")
data class FsS3Properties(
    val internalUrl: String,
    val externalUrl: String,
    val username: String,
    val password: String
)
