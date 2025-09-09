package io.komune.fs.script.imports

import com.fasterxml.jackson.module.kotlin.readValue
import io.komune.fs.commons.utils.jsonMapper
import io.komune.fs.script.core.model.ImportSettings
import io.komune.fs.script.core.config.properties.FsScriptInitProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingsParsingTest {

    @Test
    fun `should parse settings json with policies and metadata`() {
        val json = """
            {
              "policies": {
                "retention": {
                  "days": 30,
                  "versions": 5
                },
                "compression": true
              },
              "metadata": {
                "environment": "test",
                "project": "connect-fs"
              }
            }
        """.trimIndent()
        val settings = jsonMapper.readValue<ImportSettings>(json)
        requireNotNull(settings.policies)
        assertEquals(30, settings.policies?.retention?.days)
        assertEquals(5, settings.policies?.retention?.versions)
        assertEquals(true, settings.policies?.compression)
        assertEquals("test", settings.metadata?.get("environment"))
        assertEquals("connect-fs", settings.metadata?.get("project"))
    }

    @Test
    fun `FsScriptInitProperties should aggregate source files`() {
        val props = FsScriptInitProperties(
            sources = arrayListOf("/tmp/root2", "/tmp/root3")
        )
        val files = props.getSourceFiles()
        assertEquals(2, files.size)
        assertEquals("/tmp/root2", files[0].path)
        assertEquals("/tmp/root3", files[1].path)
    }
}
