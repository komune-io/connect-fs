package io.komune.fs.s2.file.app.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty("fs.ssm.channel")
@EnableConfigurationProperties(FsSsmConfig::class)
@Configuration
class FsSsmConfiguration

@ConfigurationProperties(prefix = "fs.ssm")
class FsSsmConfig(
    val channel: String,
    val chaincode: String,
    val signerName: String,
    val signerFile: String,
    val directories: List<String>?
)
