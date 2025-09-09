package io.komune.fs.script.core.config.properties

import java.io.File
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs.script.init")
data class FsScriptInitProperties(
    val sources: List<String>? = null,
) {
    fun getSourceFiles(): List<File> {
        return sources?.map(::File) ?: emptyList()
    }
}


