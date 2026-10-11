package io.komune.fs.script.migrate

import java.io.InputStream
import java.security.MessageDigest
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Fake S3 server. Like a real one, it sets the ETag (MD5 of the content) and the last modified date
 * of what it stores; the clock is shared by all instances and ticks one second per write.
 */
class InMemoryMigrationStore : MigrationStore {

    companion object {
        private val clock = AtomicLong(1_700_000_000)
    }

    private val buckets = ConcurrentHashMap<String, ConcurrentHashMap<String, Pair<ObjectInfo, ByteArray>>>()
    private val policies = ConcurrentHashMap<String, String>()
    val failingKeys: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val writeCount = AtomicInteger()
    val writes get() = writeCount.get()

    fun put(bucket: String, key: String, content: String, contentType: String? = "text/plain",
            metadata: Map<String, String> = emptyMap()) {
        val bytes = content.toByteArray()
        createBucket(bucket)
        store(bucket, ObjectInfo(key, bytes.size.toLong(), contentType, metadata), bytes)
    }

    /** Overrides the stored ETag, as a multipart upload with another part size would. */
    fun setEtag(bucket: String, key: String, etag: String) {
        objects(bucket).computeIfPresent(key) { _, (info, bytes) -> info.copy(etag = etag) to bytes }
    }

    private fun store(bucket: String, info: ObjectInfo, bytes: ByteArray) {
        val etag = MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }
        val lastModified = Instant.ofEpochSecond(clock.incrementAndGet())
        objects(bucket)[info.key] = info.copy(etag = etag, lastModified = lastModified) to bytes
    }

    fun content(bucket: String, key: String) = buckets[bucket]?.get(key)?.second?.decodeToString()

    fun keys(bucket: String) = buckets[bucket]?.keys?.toSet().orEmpty()

    override fun listBuckets() = buckets.keys.sorted()
    override fun bucketExists(bucket: String) = buckets.containsKey(bucket)
    override fun createBucket(bucket: String) {
        buckets.putIfAbsent(bucket, ConcurrentHashMap())
    }

    override fun listKeys(bucket: String) = objects(bucket).keys.sorted().asSequence()
    override fun stat(bucket: String, key: String) = objects(bucket)[key]?.first

    override fun read(bucket: String, key: String): InputStream {
        check(key !in failingKeys) { "cannot read $key" }
        return objects(bucket).getValue(key).second.inputStream()
    }

    override fun write(bucket: String, info: ObjectInfo, content: InputStream) {
        writeCount.incrementAndGet()
        store(bucket, info, content.readBytes())
    }

    override fun remove(bucket: String, key: String) {
        objects(bucket).remove(key)
    }

    override fun getBucketPolicy(bucket: String) = policies[bucket]
    override fun setBucketPolicy(bucket: String, policy: String) {
        objects(bucket)
        policies[bucket] = policy
    }

    override fun deleteBucketPolicy(bucket: String) {
        objects(bucket)
        policies.remove(bucket)
    }

    private fun objects(bucket: String) = buckets[bucket] ?: error("NoSuchBucket: $bucket")
}
