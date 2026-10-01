package io.komune.fs.script.migrate

import java.io.InputStream
import java.time.Instant

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
    fun deleteBucketPolicy(bucket: String)
}

/**
 * @property metadata user metadata, keys lowercased and without the `x-amz-meta-` prefix.
 * @property etag as returned by the server, used to tell whether two copies hold the same bytes.
 * @property lastModified as returned by the server, for information. Ignored when writing.
 */
data class ObjectInfo(
    val key: String,
    val size: Long,
    val contentType: String?,
    val metadata: Map<String, String>,
    val etag: String? = null,
    val lastModified: Instant? = null,
) {
    /** True when this copy of the object already matches [source], so copying it again is pointless. */
    fun isUpToDateWith(source: ObjectInfo) = hasSameAttributesAs(source) && hasSameContentAs(source)

    private fun hasSameAttributesAs(other: ObjectInfo) = size == other.size
        && contentType == other.contentType
        && metadata == other.metadata

    /**
     * The ETag is the MD5 of the content for single-part uploads, and derived from the part MD5s
     * for multipart ones, so equal ETags mean equal bytes. A multipart object uploaded with another
     * part size gets a different ETag for the same bytes: it is copied again, which is safe.
     */
    private fun hasSameContentAs(source: ObjectInfo) = etag != null && etag == source.etag
}

fun Map<String, String?>.normalizedMetadata(): Map<String, String> = this
    .filterValues { it != null }
    .mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
    .mapValues { (_, value) -> value!! }
