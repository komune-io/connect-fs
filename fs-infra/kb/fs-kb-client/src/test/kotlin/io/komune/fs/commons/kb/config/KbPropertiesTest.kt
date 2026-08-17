package io.komune.fs.commons.kb.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationProperties

class KbPropertiesTest {

    @Test
    fun `url is required, so the knowledge base cannot be half-configured`() {
        assertThat(KbProperties(url = "http://kb:8080").url).isEqualTo("http://kb:8080")
    }

    @Test
    fun `the configuration prefix is the one documented in application yml`() {
        // Renaming this silently stops the knowledge base from being configured at all.
        val prefix = KbProperties::class.java.getAnnotation(ConfigurationProperties::class.java)

        assertThat(prefix).isNotNull()
        assertThat(prefix.prefix).isEqualTo("fs.kb")
    }
}
