package io.komune.fs.script.core.config.properties

import java.io.File
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs.script.init")
data class FsScriptInitProperties(
    val source: String?,
    val sources: ArrayList<String>? = null,
) {
    fun getSourceFiles(): List<File> {
        return buildList {
            source?.let {
                add(File(it))
            }
            sources?.forEach {
                add(File(it))
            }
        }
    }
}


