package io.komune.fs.script.core.config.properties

import f2.client.domain.AuthRealmClientSecret
import f2.client.domain.RealmId
import java.io.File
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fs.script.init")
data class FsScriptInitProperties(
    val auth: AuthProperties,
    val admin: ApiKeyProperties,
    val fs: FsApiProperties,
    val source: String?,
    val sources: ArrayList<String>? = null,
) {
    fun asAuthRealm(): AuthRealmClientSecret {
        return AuthRealmClientSecret(
            clientId = admin.clientId,
            clientSecret = admin.clientSecret,
            serverUrl = auth.url,
            realmId = auth.realmId
        )
    }


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

data class AuthProperties(
    val url: String,
    val realmId: RealmId,
)

data class ApiKeyProperties(
    val name: String,
    val clientId: String,
    val clientSecret: String
)

data class FsApiProperties(
    val url: String
)


