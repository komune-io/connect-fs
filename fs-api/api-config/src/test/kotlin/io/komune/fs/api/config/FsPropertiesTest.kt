package io.komune.fs.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FsPropertiesTest {

    @Test
    fun `s3 dns style defaults to path style`() {
        val s3 = S3Properties(
            internalUrl = "http://minio:9000",
            externalUrl = "http://localhost:9000",
            username = "user",
            password = "password"
        )

        assertThat(s3.dns).isFalse()
        assertThat(s3.region).isNull()
    }

    @Test
    fun `the jwt claim defaults to the documented space claim`() {
        assertThat(JwtProperties().claim).isEqualTo(SPACE_CLAIM_NAME)
        assertThat(SPACE_CLAIM_NAME).isEqualTo("space")
    }

    @Test
    fun `role names are the strings the gateway annotations rely on`() {
        // These are compared against JWT roles at runtime; renaming one silently denies access.
        assertThat(Roles.READ_FILE).isEqualTo("fs_file_read")
        assertThat(Roles.WRITE_FILE).isEqualTo("fs_file_write")
        assertThat(Roles.WRITE_POLICY).isEqualTo("fs_policy_write")
    }

    @Test
    fun `bucket init properties keep the raw comma-separated string`() {
        assertThat(BucketInitProperties(buckets = "a,b,c").buckets).isEqualTo("a,b,c")
    }
}
