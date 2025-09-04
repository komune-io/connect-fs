package io.komune.fs.script.imports

import com.fasterxml.jackson.module.kotlin.readValue
import io.komune.fs.script.core.model.ImportSettings
import io.komune.fs.script.core.utils.jsonMapper
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import io.komune.fs.script.core.config.properties.AuthProperties
import io.komune.fs.script.core.config.properties.ApiKeyProperties
import io.komune.fs.script.core.config.properties.FsApiProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingsParsingTest {

    @Test
    fun `should parse settings json with bucket`() {
        val json = """
            {
              "buckets": {
                "bucket": "my-space"
              }
            }
        """.trimIndent()
        val settings = jsonMapper.readValue<ImportSettings>(json)
        requireNotNull(settings.buckets)
        assertEquals("my-space", settings.buckets?.bucket)
    }

    @Test
    fun `FsScriptInitProperties should aggregate source files`() {
        val props = FsScriptInitProperties(
            auth = AuthProperties(url = "https://auth", realmId = "realm"),
            admin = ApiKeyProperties(name = "n", clientId = "id", clientSecret = "secret"),
            fs = FsApiProperties(url = "https://fs-api"),
            source = "/tmp/root1",
            sources = arrayListOf("/tmp/root2", "/tmp/root3")
        )
        val files = props.getSourceFiles()
        assertEquals(3, files.size)
        assertEquals("/tmp/root1", files[0].path)
        assertEquals("/tmp/root2", files[1].path)
        assertEquals("/tmp/root3", files[2].path)
    }
}
