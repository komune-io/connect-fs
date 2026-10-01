package io.komune.fs.script.migrate

import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class InMemoryMigrationStore : MigrationStore {

    private val buckets = ConcurrentHashMap<String, ConcurrentHashMap<String, Pair<ObjectInfo, ByteArray>>>()
    private val policies = ConcurrentHashMap<String, String>()
    val failingKeys: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val writeCount = AtomicInteger()
    val writes get() = writeCount.get()

    fun put(bucket: String, key: String, content: String, contentType: String? = "text/plain",
            metadata: Map<String, String> = emptyMap()) {
        val bytes = content.toByteArray()
        createBucket(bucket)
        objects(bucket)[key] = ObjectInfo(key, bytes.size.toLong(), contentType, metadata) to bytes
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
        objects(bucket)[info.key] = info to content.readBytes()
    }

    override fun remove(bucket: String, key: String) {
        objects(bucket).remove(key)
    }

    override fun getBucketPolicy(bucket: String) = policies[bucket]
    override fun setBucketPolicy(bucket: String, policy: String) {
        objects(bucket)
        policies[bucket] = policy
    }

    private fun objects(bucket: String) = buckets[bucket] ?: error("NoSuchBucket: $bucket")
}
