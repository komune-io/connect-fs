package io.komune.fs.script.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportPolicies(
    val retention: RetentionPolicy? = null,
    val access: AccessPolicy? = null,
    val compression: Boolean? = null,
    val encryption: Boolean? = null
)

@Serializable
data class RetentionPolicy(
    val days: Int? = null,
    val versions: Int? = null
)

@Serializable
data class AccessPolicy(
    val public: Boolean = false,
    val allowedOrigins: List<String>? = null
)
