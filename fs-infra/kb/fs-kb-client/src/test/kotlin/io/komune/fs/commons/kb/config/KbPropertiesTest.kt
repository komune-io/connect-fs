package io.komune.fs.commons.kb.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationProperties

class KbPropertiesTest {

    @Test
    fun `the configured url is carried through verbatim`() {
        // `url` is non-null in the constructor, so a missing `fs.kb.url` fails at binding
        // time rather than here; this only pins that nothing rewrites the value.
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
