@file:Suppress(
    "MemberVisibilityCanBePrivate",
    "unused",
    "HttpUrlsUsage",
    "UastIncorrectHttpHeaderInspection"
)

package cn.yurn.yutori.module.adapter.satori

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.FormData
import cn.yurn.yutori.SatoriProperties
import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.*
import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json

expect val platformEngine: HttpClientEngineFactory<*>

class SatoriActionService(val properties: SatoriProperties, val name: String) : ActionService {
    override suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        self_id: String?,
        content: Map<String, Any?>,
        typeInfo: TypeInfo
    ): Any = HttpClient(platformEngine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.use { client ->
        val response = client.post {
            url {
                host = properties.host
                port = properties.port
                appendPathSegments(properties.path, properties.version, "$resource.$method")
            }
            contentType(ContentType.Application.Json)
            headers {
                properties.token?.let {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${properties.token}"
                    )
                }
                platform?.let { append("X-Platform", platform) }
                self_id?.let { append("X-Self-ID", self_id) }
            }
            setBody(content.entries.filter { it.value != null }
                .joinToString(",", "{", "}") { (key, value) ->
                    buildString {
                        append("\"$key\":")
                        append(
                            when (value) {
                                is String -> "\"$value\""
                                else -> value.toString()
                            }
                        )
                    }
                })
            Logger.d(name) {
                """
                Satori Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        Logger.d(name) { "Satori Action Response: $response" }
        response.body(typeInfo)
    }

    override suspend fun upload(
        resource: String,
        method: String,
        platform: String,
        self_id: String,
        content: List<FormData>
    ): Map<String, String> =
        HttpClient(platformEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }.use { client ->
            val url = URLBuilder().apply {
                host = properties.host
                port = properties.port
                appendPathSegments(properties.path, properties.version, "$resource.$method")
                headers {
                    properties.token?.let {
                        append(
                            HttpHeaders.Authorization,
                            "Bearer ${properties.token}"
                        )
                    }
                    append("X-Platform", platform)
                    append("X-Self-ID", self_id)
                }
            }.buildString()
            val formData = formData {
                for (data in content) {
                    append(data.name, data.content, Headers.build {
                        data.filename?.let { filename ->
                            append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                        }
                        append(HttpHeaders.ContentType, data.type)
                    })
                }
            }
            Logger.d(name) {
                """
                Satori Action: url: $url,
                    body: $formData
                """.trimIndent()
            }
            val response = client.submitFormWithBinaryData(url, formData)
            Logger.d(name) { "Satori Action Response: $response" }
            response.body()
        }
}