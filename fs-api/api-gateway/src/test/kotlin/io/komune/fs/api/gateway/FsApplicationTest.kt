package io.komune.fs.api.gateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication

class FsApplicationTest {

    @Test
    fun `component scanning covers the whole io komune fs tree`() {
        // Every fs module lives under io.komune.fs; narrowing this silently drops beans
        // from modules the gateway depends on.
        val annotation = FsApplication::class.java.getAnnotation(SpringBootApplication::class.java)

        assertThat(annotation).isNotNull()
        assertThat(annotation.scanBasePackages).containsExactly("io.komune.fs")
    }
}
