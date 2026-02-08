package io.komune.fs.commons.utils

import tools.jackson.core.json.JsonReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder

val jsonMapper = jacksonMapperBuilder()
    .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .build()

fun <T> String.parseJsonTo(targetClass: Class<T>): T {
    return jsonMapper.readValue(this, targetClass)
}

fun <T> String.parseJsonTo(targetClass: Class<Array<T>>): List<T> {
    return jsonMapper.readValue(this, targetClass).toList()
}

fun <T> T.toJson(): String {
    return jsonMapper.writeValueAsString(this)
}
