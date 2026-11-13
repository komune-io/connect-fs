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

        val isSuccess = retryOnThrow(actionName = "action", logger = logger) { attempts++ }

        assertThat(isSuccess).isTrue()
        assertThat(attempts).isEqualTo(1)
    }

    @Test
    fun `retries until the action stops throwing`() = runTest {
        var attempts = 0

        val isSuccess = retryOnThrow(actionName = "action", retryDelayMillis = 0, logger = logger) {
            attempts++
            if (attempts < 3) error("not yet")
        }

        assertThat(isSuccess).isTrue()
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `gives up after maxRetries and reports failure rather than throwing`() = runTest {
        var attempts = 0

        val isSuccess = retryOnThrow(actionName = "action", maxRetries = 2, retryDelayMillis = 0, logger = logger) {
            attempts++
            error("always fails")
        }

        assertThat(isSuccess).isFalse()
        assertThat(attempts).isEqualTo(2)
    }

    @Test
    fun `maxRetries of one means a single attempt`() = runTest {
        var attempts = 0

        val isSuccess = retryOnThrow(actionName = "action", maxRetries = 1, retryDelayMillis = 0, logger = logger) {
            attempts++
            error("fails")
        }

        assertThat(isSuccess).isFalse()
        assertThat(attempts).isEqualTo(1)
    }
}
