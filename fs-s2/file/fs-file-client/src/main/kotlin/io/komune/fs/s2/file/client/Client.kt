package io.komune.fs.s2.file.client

import f2.client.ktor.http.F2DefaultJson
import f2.client.ktor.http.plugin.F2Auth
import f2.client.ktor.http.plugin.model.AuthRealm
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

open class Client(
    protected val baseUrl: String,
    private val authProvider: AuthProvider? = null,
    private val block: HttpClientConfig<*>.() -> Unit = {}
) {
    private val logger = LoggerFactory.getLogger(Client::class.java)

    val jsonConverter = Json {
        ignoreUnknownKeys = true
    }

    protected val httpClient = HttpClient(CIO) {
        install(HttpTimeout) {
            @SuppressWarnings("MagicNumber")
            requestTimeoutMillis = 60000
        }
        if(logger.isDebugEnabled) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        install(ContentNegotiation) {
            this.json(F2DefaultJson)
        }
        authProvider?.let {
            install(F2Auth) {
                getAuth = authProvider
            }
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
            formData = FormDataBodyBuilder(jsonConverter).apply(block).toFormData()
        ).body()
    }

    protected class FormDataBodyBuilder(
        val json: Json
    ) {
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

        inline fun <reified T> param(key: String, value: T) {
            val encoded = json.encodeToString(value)
            param(key, encoded, "application/json")
        }

        fun file(key: String, file: ByteArray, filename: String) {
            FormPart(
                key = key,
                value = file,
                headers = Headers.build { append(HttpHeaders.ContentDisposition, "filename=$filename") }
            ).let(formParts::add)
        }

    }
}
