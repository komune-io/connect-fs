package io.komune.fs.commons.kb

import io.komune.fs.commons.kb.domain.command.VectorAskFunction
import io.komune.fs.commons.kb.domain.command.VectorAskedEventDTOBase
import io.komune.fs.commons.kb.domain.command.VectorCreateFunction
import f2.client.F2Client
import f2.client.ktor.F2ClientBuilder
import f2.client.ktor.common.F2DefaultJson
import f2.client.ktor.http.HttpF2Client
import f2.dsl.fnc.F2Function
import f2.dsl.fnc.F2SupplierSingle
import f2.dsl.fnc.f2SupplierSingle
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.slf4j.LoggerFactory

//TODO Make it multiplaform
//expect fun F2Client.kbClient(): F2SupplierSingle<KbClient>
//expect fun kbClient(urlBase: String, accessToken: String): F2SupplierSingle<KbClient>

fun F2Client.kbClient(): F2SupplierSingle<KbClient> = f2SupplierSingle {
    KbClient(this)
}


fun kbClient(urlBase: String, requestTimeout: Long = 60000): F2SupplierSingle<KbClient> = f2SupplierSingle {
    val log = LoggerFactory.getLogger(KbClient::class.java)
    KbClient(
        F2ClientBuilder.get(urlBase) {
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout
            }
            if(log.isDebugEnabled) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
        }
    )
}

open class KbClient(val client: F2Client) {
    fun knowledgeAsk(): VectorAskFunction = F2Function { msgs ->
        msgs.map { cmd ->
            val httpF2Client = (client as HttpF2Client)
            val tt: String = httpF2Client.httpClient.post(
                "${httpF2Client.urlBase}/ask"
            ){
                headers.append(HttpHeaders.ContentType, "application/json")
                headers.append(HttpHeaders.Accept, "application/json")
                setBody(
                buildJsonObject {
                    put("question", cmd.question)
                    put("messages", buildJsonArray {
                        cmd.history.forEach { message ->
                            add(buildJsonObject {
                                put("content", message.content)
                                put("type", message.type)
                                put("additional_kwargs", buildJsonObject{})
                            })
                        }
                    })
                    put("targeted_files", buildJsonArray {
                        cmd.targetedFiles.forEach(::add)
                    })
                })
            }.body()!!
            VectorAskedEventDTOBase(tt)
        }
    }

    fun vectorCreateFunction(): VectorCreateFunction = F2Function  { msgs ->
        msgs.map { cmd ->
            val httpF2Client = (client as HttpF2Client)
            httpF2Client.httpClient.submitFormWithBinaryData(
                url = httpF2Client.urlBase,
                formData = FormDataBodyBuilder().apply {
                    param("metadata", cmd.metadata)
                    param("path", cmd.path.toString())
                    file("file", cmd.file, cmd.path.name)
                }.toFormData()
            ).body()
        }
    }
}


//TODO PUT THAT in F2
class FormDataBodyBuilder {
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
        val json = F2DefaultJson.encodeToString(value)
        param(key, json, "application/json")
    }

    fun file(key: String, file: ByteArray, filename: String) {
        FormPart(
            key = key,
            value = file,
            headers = Headers.build { append(HttpHeaders.ContentDisposition, "filename=$filename") }
        ).let(formParts::add)
    }
}
