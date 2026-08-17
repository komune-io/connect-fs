package io.komune.fs.script.gateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.CommandLineRunner

class ImportScriptExceptionTest {

    @Test
    fun `carries the message the runner reports on give-up`() {
        val exception = ImportScriptException("File Import Script failed after 3 attempts")

        assertThat(exception).hasMessage("File Import Script failed after 3 attempts")
    }

    @Test
    fun `is unchecked, so the CommandLineRunner contract is not widened`() {
        assertThat(RuntimeException::class.java).isAssignableFrom(ImportScriptException::class.java)
    }

    @Test
    fun `the runner is a CommandLineRunner, which is what makes the script run at boot`() {
        assertThat(CommandLineRunner::class.java).isAssignableFrom(ScriptServiceRunner::class.java)
    }
}
