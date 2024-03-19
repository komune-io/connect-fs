package io.komune.fs.commons.kb.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.komune.fs.commons.kb.kbClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@Configuration
@ConditionalOnProperty("fs.kb.url")
@EnableConfigurationProperties(KbProperties::class)
class KbConfiguration {

    val logger = LoggerFactory.getLogger(KbConfiguration::class.java)
    @Bean
    fun kbClient(props: KbProperties) = runBlocking {
        logger.debug("Creating kb client with url ${props.url}")
        kbClient(props.url).invoke()
    }
}
