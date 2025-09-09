package io.komune.fs.script.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fs.script.retry")
data class FsRetryProperties(
    val max: Int = 3,
    val delayMillis: Long = 5000,
)
