package io.komune.fs.script.core.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

val jsonMapper = ObjectMapper()
    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerKotlinModule()

inline fun <reified T> String.parseJsonTo(): T {
    return this.parseJsonTo(T::class.java)
}

fun <T> String.parseJsonTo(targetClass: Class<T>): T {
    return this.parseTo(targetClass)
}

fun <T> String.parseJsonTo(targetClass: Class<Array<T>>): List<T> {
    val parsedValue = this.parseTo(targetClass)
    return listOf(*parsedValue)
}

private fun <T> String.parseTo(targetClass: Class<T>): T {
    return jsonMapper.readValue(this, targetClass)
}

fun <T> T.toJson(): String {
    val mapper = ObjectMapper()
        .registerKotlinModule()

    return mapper.writeValueAsString(this)
}

inline fun <reified R: Any> parseFile(path: String): R {
    return parseFileOrNull<R>(path)
        ?: throw IllegalArgumentException("File not found: $path")
}

inline fun <reified R: Any> parseFileOrNull(path: String): R? {
    val file = PathMatchingResourcePatternResolver().getResource(path)
    if (!file.exists()) {
        return null
    }
    return jsonMapper.readValue(file.inputStream, R::class.java)
}
