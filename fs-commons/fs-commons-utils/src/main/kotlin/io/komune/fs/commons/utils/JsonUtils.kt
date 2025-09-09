package io.komune.fs.commons.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val jsonMapper = jacksonObjectMapper()
    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun <T> String.parseJsonTo(targetClass: Class<T>): T {
    return jsonMapper.readValue(this, targetClass)
}

fun <T> String.parseJsonTo(targetClass: Class<Array<T>>): List<T> {
    return jsonMapper.readValue(this, targetClass).toList()
}

fun <T> T.toJson(): String {
    return jsonMapper.writeValueAsString(this)
}
