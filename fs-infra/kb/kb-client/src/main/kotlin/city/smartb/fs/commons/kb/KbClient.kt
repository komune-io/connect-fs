package city.smartb.fs.commons.kb

import city.smartb.fs.commons.kb.domain.command.VectorAskFunction
import city.smartb.fs.commons.kb.domain.command.VectorCreateFunction
import f2.client.F2Client
import f2.client.function
import f2.client.ktor.F2ClientBuilder
import f2.client.ktor.get
import f2.client.ktor.http.F2DefaultJson
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
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory

//TODO Make it multiplaform
//expect fun F2Client.kbClient(): F2SupplierSingle<KbClient>
//expect fun kbClient(urlBase: String, accessToken: String): F2SupplierSingle<KbClient>

fun F2Client.kbClient(): F2SupplierSingle<KbClient> = f2SupplierSingle {
    KbClient(this)
}


fun kbClient(urlBase: String, /*accessToken: String*/): F2SupplierSingle<KbClient> = f2SupplierSingle {
    val log = LoggerFactory.getLogger(KbClient::class.java)
    KbClient(
        F2ClientBuilder.get(urlBase) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
            }
            if(log.isDebugEnabled) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.HEADERS
                }
            }
        }
    )
}

open class KbClient(val client: F2Client) {
    fun knowledgeAsk(): VectorAskFunction = client.function(this::knowledgeAsk.name)

    fun vectorCreateFunction(): VectorCreateFunction = F2Function  { msgs ->
        msgs.map { cmd ->
            val httpF2Client = (client as HttpF2Client)
            httpF2Client.httpClient.submitFormWithBinaryData(
                url = "${httpF2Client.urlBase}",
                formData = FormDataBodyBuilder().apply {
                    param("metadata", cmd.metadata + ("fsPath" to cmd.path.toString()))
                    file("file", cmd.file, cmd.path.name)
                }.toFormData()
            ).body()
        }
    }
}


//TODO PUT THAT in F2 https://smartbcity.atlassian.net/jira/software/projects/FX/boards/28?selectedIssue=FX-154
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
