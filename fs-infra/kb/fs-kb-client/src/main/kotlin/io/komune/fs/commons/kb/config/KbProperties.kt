package io.komune.fs.commons.kb.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs.kb")
data class KbProperties (
    val url: String,
)
