package io.komune.fs.script.core.model

import io.komune.fs.commons.utils.parseJsonTo
import io.komune.fs.commons.utils.toJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Settings files are hand-written and read with Jackson at import time, so these pin the
 * Jackson path rather than kotlinx-serialization.
 *
 * Note: [ImportSettings] and friends carry `@Serializable`, but this module does not apply
 * the kotlinx serialization plugin, so no serializer is generated and those annotations do
 * nothing today.
 */
class ImportSettingsSerializationTest {

    @Test
    fun `an empty settings document parses to all-null`() {
        val settings = "{}".parseJsonTo(ImportSettings::class.java)

        assertThat(settings.policies).isNull()
        assertThat(settings.metadata).isNull()
    }

    @Test
    fun `a full settings document round-trips`() {
        val original = ImportSettings(
            policies = ImportPolicies(
                retention = RetentionPolicy(days = 30, versions = 3),
                access = AccessPolicy(public = true, allowedOrigins = listOf("https://example.com")),
                compression = true,
                encryption = false
            ),
            metadata = mapOf("owner" to "team-a")
        )

        assertThat(original.toJson().parseJsonTo(ImportSettings::class.java)).isEqualTo(original)
    }

    @Test
    fun `field names are stable, since settings files are written by hand`() {
        val encoded = ImportSettings(metadata = mapOf("owner" to "team-a")).toJson()

        assertThat(encoded).contains("\"metadata\"")
        assertThat(encoded).contains("\"owner\":\"team-a\"")
    }

    @Test
    fun `access policy defaults to not public`() {
        val policy = "{}".parseJsonTo(AccessPolicy::class.java)

        assertThat(policy.public).isFalse()
        assertThat(policy.allowedOrigins).isNull()
    }

    @Test
    fun `keys removed from the model are tolerated in an old settings file`() {
        val settings = """{"metadata":{"a":"b"},"somethingRemoved":1}"""
            .parseJsonTo(ImportSettings::class.java)

        assertThat(settings.metadata).containsEntry("a", "b")
    }

    @Test
    fun `a partially specified policy leaves the rest null`() {
        val policies = """{"compression":true}""".parseJsonTo(ImportPolicies::class.java)

        assertThat(policies.compression).isTrue()
        assertThat(policies.retention).isNull()
        assertThat(policies.access).isNull()
        assertThat(policies.encryption).isNull()
    }
}
