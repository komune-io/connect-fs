package io.komune.fs.script.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportPolicies(
    val retention: RetentionPolicy? = null,
    val access: AccessPolicy? = null,
    @SerialName("compression")
    @JsonProperty("compression")
    val isCompressed: Boolean? = null,
    @SerialName("encryption")
    @JsonProperty("encryption")
    val isEncrypted: Boolean? = null
)

@Serializable
data class RetentionPolicy(
    val days: Int? = null,
    val versions: Int? = null
)

@Serializable
data class AccessPolicy(
    @SerialName("public")
    @JsonProperty("public")
    val isPublic: Boolean = false,
    val allowedOrigins: List<String>? = null
)
