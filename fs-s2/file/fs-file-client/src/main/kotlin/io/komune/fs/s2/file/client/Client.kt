package io.komune.fs.s2.file.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson

open class Client(
    protected val baseUrl: String,
    protected val block: HttpClientConfig<*>.() -> Unit = {}
) {
    protected val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        block()
    }

    protected suspend inline fun <reified T> post(path: String, jsonBody: Any): T {
        return httpClient.post {
            url("$baseUrl/$path")
            header("Content-Type", ContentType.Application.Json)
            header("Accept", ContentType.Application.Json)
            header("Accept", ContentType.Application.OctetStream)
            setBody(jsonBody)
        }.body()
    }

    protected suspend inline fun <reified T> postFormData(
        path: String, crossinline block: FormDataBodyBuilder.() -> Unit
    ): T {
        return httpClient.submitFormWithBinaryData(
            url = "$baseUrl/$path",
            formData = FormDataBodyBuilder().apply(block).toFormData()
        ).body()
    }

    protected class FormDataBodyBuilder {
        private val formParts = mutableListOf<FormPart<*>>()

        fun toFormData() = formData { formParts.forEach { append(it) } }

        fun param(key: String, value: String, contentType: String? = null) {
            val headers = contentType
                ?.let { Headers.build { append(HttpHeaders.ContentType, contentType) } }
                ?: Headers.Empty

            FormPart(
                key = key,
                value = value,
                headers = headers
            ).let(formParts::add)
        }

        fun <T> param(key: String, value: T) {
            param(key, value.toJson(), "application/json")
        }

        fun file(key: String, file: ByteArray, filename: String) {
            FormPart(
                key = key,
                value = file,
                headers = Headers.build { append(HttpHeaders.ContentDisposition, "filename=$filename") }
            ).let(formParts::add)
        }

        fun <T> T.toJson(): String = ObjectMapper()
            .registerKotlinModule()
            .writeValueAsString(this)
    }

}
