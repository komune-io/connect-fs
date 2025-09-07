package io.komune.fs.script.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportSettings(
    val policies: ImportPolicies? = null,
    val metadata: Map<String, String>? = null
)
