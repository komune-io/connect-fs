package io.komune.fs.commons.utils

import tools.jackson.core.json.JsonReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder

val jsonMapper: JsonMapper = jacksonMapperBuilder()
    .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .build()

fun <T> String.parseJsonTo(targetClass: Class<T>): T = jsonMapper.readValue(this, targetClass)

fun <T> String.parseJsonTo(targetClass: Class<Array<T>>): List<T> = jsonMapper.readValue(this, targetClass).toList()

fun <T> T.toJson(): String = jsonMapper.writeValueAsString(this)
