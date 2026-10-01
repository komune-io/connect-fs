package io.komune.fs.script.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Copies every bucket of a source S3 server (e.g. MinIO) into the target one configured
 * under `fs.script.s3` (e.g. RustFS). Runs instead of the import script when [enabled].
 */
@ConfigurationProperties(prefix = "fs.script.migrate")
data class FsMigrateProperties(
    val enabled: Boolean = false,
    /** Log what would be copied or deleted without writing anything to the target. */
    val dryRun: Boolean = false,
    /** Buckets to migrate. Empty means every bucket of the source. */
    val buckets: List<String> = emptyList(),
    /** Number of objects copied in parallel. */
    val concurrency: Int = 8,
    /** Delete target objects that no longer exist on the source. Meant for the final pass. */
    val deleteExtraneous: Boolean = false,
    val source: FsMigrateSourceProperties? = null,
)

data class FsMigrateSourceProperties(
    val internalUrl: String,
    val username: String,
    val password: String,
    val region: String? = null,
)
