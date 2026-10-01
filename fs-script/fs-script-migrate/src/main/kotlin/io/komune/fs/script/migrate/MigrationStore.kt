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
 * @property lastModified as returned by the server. Ignored when writing.
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
     * Same ETag means same bytes for single-part uploads (it is their MD5). ETags of multipart uploads
     * depend on the part size and may differ between servers, so a copy written strictly after the
     * source was last modified also counts as up to date: an overwrite of the source since then would
     * make the source the newer one. Equal timestamps (second precision) are treated as stale.
     */
    private fun hasSameContentAs(source: ObjectInfo): Boolean {
        val sameEtag = etag != null && etag == source.etag
        val writtenAfter = lastModified != null && source.lastModified != null
            && lastModified.isAfter(source.lastModified)
        return sameEtag || writtenAfter
    }
}

fun Map<String, String?>.normalizedMetadata(): Map<String, String> = this
    .filterValues { it != null }
    .mapKeys { (key) -> key.lowercase().removePrefix("x-amz-meta-") }
    .mapValues { (_, value) -> value!! }
