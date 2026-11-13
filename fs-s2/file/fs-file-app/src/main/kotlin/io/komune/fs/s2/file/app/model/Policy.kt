package io.komune.fs.s2.file.app.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Policy(
    @JsonProperty("Version")
    val version: String = "2012-10-17",
    @JsonProperty("Statement")
    val statements: MutableList<Statement> = mutableListOf()
)

fun Policy.getOrAddStatementWith(effect: S3Effect, action: S3Action): Statement =
    getStatementWith(effect = effect, action = action)
        ?: Statement.with(effect = effect, action = action).also(statements::add)

fun Policy.getStatementWith(effect: S3Effect, action: S3Action): Statement? = statements.firstOrNull { statement ->
    statement.effect == effect.id && statement.actions.contains(action.id)
}

data class Statement(
    @JsonProperty("Effect")
    val effect: String,
    @JsonProperty("Principal")
    val principals: MutableMap<String, Any>,
    @JsonProperty("Action")
    val actions: MutableSet<String>,
    @JsonProperty("Resource")
    val resources: MutableSet<String>
) {
    companion object {
        const val RESOURCE_PREFIX = "arn:aws:s3:::"

        fun with(effect: S3Effect, action: S3Action) = Statement(
            effect = effect.id,
            principals = mutableMapOf("AWS" to listOf("*")),
            actions = mutableSetOf(action.id),
            resources = mutableSetOf()
        )

        fun resourcePath(bucket: String, path: String) = "$RESOURCE_PREFIX$bucket/$path"
    }
}

fun Statement.addResource(bucket: String, path: String) {
    resources.add(Statement.resourcePath(bucket = bucket, path = path))
}

fun Statement.removeResource(bucket: String, path: String) {
    resources.remove(Statement.resourcePath(bucket = bucket, path = path))
}

enum class S3Effect(val id: String) {
    ALLOW("Allow"),
    DENY("Deny")
}

enum class S3Action(val id: String) {
    GET_OBJECT("s3:GetObject")
}
