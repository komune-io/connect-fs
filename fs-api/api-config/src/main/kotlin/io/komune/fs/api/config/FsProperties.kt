package io.komune.fs.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs")
data class FsProperties (
    val s3: S3Properties,
    val init: BucketInitProperties?,
    val space: SpaceProperties?,
)

data class S3Properties(
    var internalUrl: String,
    var externalUrl: String,
    var region: String,
    var username: String,
    var password: String,
    @Deprecated("Use fs.space.name")
    var bucket: String?,
    var dns: Boolean = false,
)

/**
 * There is two way to configure the bucket name:
 *   * Specify a hardcoded name
 *   * By a claim in a jwt
 */
data class SpaceProperties(
    val name: String?,
    val jwt: JwtProperties?

)

const val SPACE_CLAIM_NAME = "space"

/**
 * Specify the name of the JWT Claim Fs uses to identify the bucket to attach to the authenticated user.
 * @default space
 */
data class JwtProperties(
    val claim: String? = SPACE_CLAIM_NAME,
)

/**
 * Specify the name of bucket separated by a coma.
 */
data class BucketInitProperties(
    val buckets: String,
)
