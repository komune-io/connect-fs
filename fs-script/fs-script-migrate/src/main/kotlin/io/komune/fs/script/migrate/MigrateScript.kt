package io.komune.fs.script.migrate

import io.komune.fs.script.core.config.properties.FsMigrateProperties
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

/**
 * Copies buckets from [source] to [target]: objects with their content type and user metadata,
 * and the bucket policy (which holds FS public directories).
 *
 * Objects already present on the target with the same size, content type and metadata are
 * skipped, so the script can be run again to resume a failed run or to copy what changed
 * since the previous one.
 */
class MigrateScript(
    private val properties: FsMigrateProperties,
    private val source: MigrationStore,
    private val target: MigrationStore,
) {
    private val logger = LoggerFactory.getLogger(MigrateScript::class.java)

    init {
        require(properties.concurrency > 0) { "fs.script.migrate.concurrency must be positive" }
    }

    suspend fun run(): MigrationReport = withContext(Dispatchers.IO) {
        val buckets = properties.buckets.ifEmpty { source.listBuckets() }
        logger.info("Migrating buckets $buckets${if (properties.dryRun) " (dry run)" else ""}")

        MigrationReport(buckets.map { migrateBucket(it) })
            .also { report -> report.buckets.forEach { logger.info(it.toString()) } }
    }

    private suspend fun migrateBucket(bucket: String): BucketReport {
        require(source.bucketExists(bucket)) { "Bucket $bucket does not exist on the source" }

        val report = BucketReport(bucket)
        val targetExists = target.bucketExists(bucket)
        if (!targetExists && !properties.dryRun) {
            logger.info("Creating bucket $bucket on the target")
            target.createBucket(bucket)
        }

        report.policyCopied = copyPolicy(bucket)

        // In a dry run against a missing bucket there is nothing on the target to compare with.
        val targetReadable = targetExists || !properties.dryRun
        forEachConcurrently(source.listKeys(bucket)) { key ->
            copyObject(bucket, key, targetReadable, report)
        }

        if (properties.deleteExtraneous && targetExists) {
            forEachConcurrently(target.listKeys(bucket)) { key ->
                removeIfExtraneous(bucket, key, report)
            }
        }

        return report
    }

    private fun copyPolicy(bucket: String): Boolean {
        val policy = source.getBucketPolicy(bucket)
            ?.takeIf { it != target.getBucketPolicyOrNull(bucket) }
            ?: return false

        logger.info("Copying policy of bucket $bucket")
        if (!properties.dryRun) target.setBucketPolicy(bucket, policy)
        return true
    }

    @Suppress("TooGenericExceptionCaught")
    private fun copyObject(bucket: String, key: String, targetReadable: Boolean, report: BucketReport) {
        try {
            val info = source.stat(bucket, key)
            if (info == null) {
                logger.warn("$bucket/$key disappeared from the source while migrating, skipping it")
                report.skipped.incrementAndGet()
                return
            }

            val existing = if (targetReadable) target.stat(bucket, key) else null
            if (existing != null && info.isSameAs(existing)) {
                report.skipped.incrementAndGet()
                return
            }

            logger.debug("Copying $bucket/$key (${info.size} bytes)")
            if (!properties.dryRun) {
                source.read(bucket, key).use { content -> target.write(bucket, info, content) }
            }
            report.copied.incrementAndGet()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to copy $bucket/$key", e)
            report.failed.incrementAndGet()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun removeIfExtraneous(bucket: String, key: String, report: BucketReport) {
        try {
            if (source.stat(bucket, key) != null) return

            logger.info("Deleting $bucket/$key, which no longer exists on the source")
            if (!properties.dryRun) target.remove(bucket, key)
            report.deleted.incrementAndGet()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to delete $bucket/$key", e)
            report.failed.incrementAndGet()
        }
    }

    /**
     * Runs [action] on every key with [FsMigrateProperties.concurrency] workers. Keys are read
     * lazily through a bounded channel, so memory stays flat whatever the bucket size.
     */
    private suspend fun forEachConcurrently(keys: Sequence<String>, action: (String) -> Unit) = coroutineScope {
        val channel = Channel<String>(capacity = properties.concurrency * 2)
        launch {
            try {
                keys.forEach { channel.send(it) }
            } finally {
                channel.close()
            }
        }
        repeat(properties.concurrency) {
            launch {
                for (key in channel) action(key)
            }
        }
    }

    private fun MigrationStore.getBucketPolicyOrNull(bucket: String) =
        if (bucketExists(bucket)) getBucketPolicy(bucket) else null
}

data class MigrationReport(val buckets: List<BucketReport>) {
    val hasFailures get() = buckets.any { it.failed.get() > 0 }
}

class BucketReport(val bucket: String) {
    val copied = AtomicLong()
    val skipped = AtomicLong()
    val deleted = AtomicLong()
    val failed = AtomicLong()
    var policyCopied = false

    override fun toString() = "Bucket $bucket: ${copied.get()} copied, ${skipped.get()} skipped, " +
        "${deleted.get()} deleted, ${failed.get()} failed, policy ${if (policyCopied) "copied" else "unchanged"}"
}
