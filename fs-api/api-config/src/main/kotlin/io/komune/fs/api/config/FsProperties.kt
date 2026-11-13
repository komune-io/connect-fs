package io.komune.fs.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.Name

@ConfigurationProperties(prefix = "fs")
data class FsProperties (
    val s3: S3Properties,
    val init: BucketInitProperties?,
    val space: SpaceProperties?,
)

data class S3Properties(
    val internalUrl: String,
    val externalUrl: String,
    val region: String? = null,
    val username: String,
    val password: String,
    @Name("dns")
    val isDns: Boolean = false,
)

/**
 * There are two ways to configure the bucket name.
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
