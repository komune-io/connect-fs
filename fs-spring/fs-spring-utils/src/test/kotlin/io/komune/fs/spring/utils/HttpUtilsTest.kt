package io.komune.fs.spring.utils

import java.io.ByteArrayInputStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class HttpUtilsTest {

    @Test
    fun `buildResponseForFile sets an attachment disposition carrying the filename`() {
        val response = buildResponseForFile("main.jpg", ByteArrayInputStream(ByteArray(0)))

        val disposition = response.headers.getFirst(HttpHeaders.CONTENT_DISPOSITION)
        assertThat(disposition).contains("attachment")
        assertThat(disposition).contains("main.jpg")
    }

    @Test
    fun `buildResponseForFile guesses the content type from the name`() {
        assertThat(buildResponseForFile("main.jpg", null).headers.getFirst(HttpHeaders.CONTENT_TYPE))
            .isEqualTo("image/jpeg")
    }

    @Test
    fun `a null stream still produces a 200 with an empty body rather than throwing`() {
        val response = buildResponseForFile("missing.txt", null)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.inputStream.readBytes()).isEmpty()
    }

    @Test
    fun `the body streams back exactly what was passed in`() {
        val content = "file content".toByteArray()

        val response = buildResponseForFile("a.txt", ByteArrayInputStream(content))

        assertThat(response.body!!.inputStream.readBytes()).isEqualTo(content)
    }
}
