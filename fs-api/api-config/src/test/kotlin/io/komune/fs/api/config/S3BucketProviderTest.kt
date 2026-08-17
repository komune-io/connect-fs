package io.komune.fs.api.config

import io.komune.fs.s2.file.domain.error.NoBucketConfiguredError
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class S3BucketProviderTest {

    private fun properties(spaceName: String?) = FsProperties(
        s3 = S3Properties(
            internalUrl = "http://minio:9000",
            externalUrl = "http://localhost:9000",
            username = "user",
            password = "password"
        ),
        init = null,
        space = spaceName?.let { SpaceProperties(name = it, jwt = null) }
    )

    @Test
    fun `a configured space name is used as the bucket`() = runTest {
        val provider = S3BucketProvider(properties(spaceName = "my-bucket"))

        assertThat(provider.getBucket()).isEqualTo("my-bucket")
    }

    @Test
    fun `no configured space and no authenticated tenant is an error, not an empty bucket`() = runTest {
        // Falling back to "" here would silently write into the wrong place.
        val provider = S3BucketProvider(properties(spaceName = null))

        assertThatThrownBy { kotlinx.coroutines.runBlocking { provider.getBucket() } }
            .hasMessageContaining(NoBucketConfiguredError().message)
    }

    @Test
    fun `space properties with a null name fall through rather than yielding null`() = runTest {
        val props = properties(spaceName = null).copy(space = SpaceProperties(name = null, jwt = null))
        val provider = S3BucketProvider(props)

        assertThatThrownBy { kotlinx.coroutines.runBlocking { provider.getBucket() } }
            .isInstanceOf(Exception::class.java)
    }
}
