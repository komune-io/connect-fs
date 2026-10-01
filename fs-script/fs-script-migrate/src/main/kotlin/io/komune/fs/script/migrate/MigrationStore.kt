package io.komune.fs.script.migrate

import java.io.InputStream

/**
 * The S3 operations the migration needs, on one side of it (source or target).
 */
interface MigrationStore {
    fun listBuckets(): List<String>
    fun bucketExists(bucket: String): Boolean
    fun createBucket(bucket: String)

    /** Keys of every object in [bucket], read page by page. */
    fun listKeys(bucket: String): Sequence<String>

    /** Returns null when the object does not exist. */
    fun stat(bucket: String, key: String): ObjectInfo?
    fun read(bucket: String, key: String): InputStream
    fun write(bucket: String, info: ObjectInfo, content: InputStream)
    fun remove(bucket: String, key: String)

    /** Returns null when the bucket has no policy. */
    fun getBucketPolicy(bucket: String): String?
    fun setBucketPolicy(bucket: String, policy: String)
}

/**
 * @property metadata user metadata, keys lowercased and without the `x-amz-meta-` prefix.
 */
data class ObjectInfo(
    val key: String,
    val size: Long,
    val contentType: String?,
    val metadata: Map<String, String>,
) {
    /** True when [other] already holds the same object, so copying it again is pointless. */
    fun isSameAs(other: ObjectInfo) = size == other.size
        && contentType == other.contentType
        && metadata == other.metadata
}

fun Map<String, String?>.normalizedMetadata(): Map<String, String> = this
    .filterValues { it != null }
    .mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
    .mapValues { (_, value) -> value!! }
