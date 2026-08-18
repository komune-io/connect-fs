package io.komune.fs.script.core.utils

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class RetryUtilsTest {

    private val logger = LoggerFactory.getLogger(RetryUtilsTest::class.java)

    @Test
    fun `succeeds on the first attempt without retrying`() = runTest {
        var attempts = 0

        val success = retryOnThrow("action", logger = logger) { attempts++ }

        assertThat(success).isTrue()
        assertThat(attempts).isEqualTo(1)
    }

    @Test
    fun `retries until the action stops throwing`() = runTest {
        var attempts = 0

        val success = retryOnThrow("action", retryDelayMillis = 0, logger = logger) {
            attempts++
            if (attempts < 3) error("not yet")
        }

        assertThat(success).isTrue()
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `gives up after maxRetries and reports failure rather than throwing`() = runTest {
        var attempts = 0

        val success = retryOnThrow("action", maxRetries = 2, retryDelayMillis = 0, logger = logger) {
            attempts++
            error("always fails")
        }

        assertThat(success).isFalse()
        assertThat(attempts).isEqualTo(2)
    }

    @Test
    fun `maxRetries of one means a single attempt`() = runTest {
        var attempts = 0

        val success = retryOnThrow("action", maxRetries = 1, retryDelayMillis = 0, logger = logger) {
            attempts++
            error("fails")
        }

        assertThat(success).isFalse()
        assertThat(attempts).isEqualTo(1)
    }
}
