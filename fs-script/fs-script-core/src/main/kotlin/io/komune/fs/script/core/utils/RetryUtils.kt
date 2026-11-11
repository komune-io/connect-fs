package io.komune.fs.script.core.utils

import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.delay
import org.slf4j.Logger

private const val LOG_SEPARATOR_LENGTH = 50

suspend fun retryOnThrow(
    actionName: String,
    maxRetries: Int = 3,
    retryDelayMillis: Long = 5000,
    logger: Logger,
    action: suspend () -> Unit
): Boolean {
    var isSuccess = false
    var attempts = 0
    @Suppress("TooGenericExceptionCaught") // any failure of the action is retried, whatever its type
    while (attempts < maxRetries && !isSuccess) {
        attempts++
        try {
            logger.info("=".repeat(LOG_SEPARATOR_LENGTH))
            logger.info("$actionName (attempt $attempts of $maxRetries)")
            logger.info("=".repeat(LOG_SEPARATOR_LENGTH))
            action()
            isSuccess = true
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("$actionName failed (attempt $attempts of $maxRetries). Retrying...", ex)

            if (attempts >= maxRetries) {
                logger.error("$actionName failed after $maxRetries attempts. Exiting.")
                break
            }

            delay(retryDelayMillis)
        }
    }
    return isSuccess
}
