@file:Suppress(
    "MemberVisibilityCanBePrivate",
    "unused",
    "HttpUrlsUsage",
    "UastIncorrectHttpHeaderInspection"
)

package cn.yurn.yutori.module.satori.adapter

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.FormData
import cn.yurn.yutori.SatoriProperties
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.module.satori.serialize
import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json

class SatoriActionService(val properties: SatoriProperties, val name: String) : ActionService {
    override suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        self_id: String?,
        content: Map<String, Any?>
    ): Any = HttpClient {
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
                                is String -> "\"${value.replace("\"", "\\\"")}\""
                                is List<*> -> "\"${
                                    (value as List<MessageElement>).serialize()
                                        .replace("\n", "\\n")
                                        .replace("\"", "\\\"")
                                }\""

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
        val body = response.bodyAsText()
        when (resource) {
            "channel" -> when (method) {
                "get" -> TODO()
                "list" -> TODO()
                "create" -> TODO()
                "update" -> TODO()
                "delete" -> TODO()
                "mute" -> TODO()
                else -> TODO()
            }

            "user.channel" -> when (method) {
                "create" -> TODO()
                else -> TODO()
            }

            "guild" -> when (method) {
                "get" -> TODO()
                "list" -> TODO()
                "approve" -> TODO()
                else -> TODO()
            }

            "guild.member" -> when (method) {
                "get" -> TODO()
                "list" -> TODO()
                "kick" -> TODO()
                "mute" -> TODO()
                "approve" -> TODO()
                else -> TODO()
            }

            "guild.member.role" -> when (method) {
                "set" -> TODO()
                "unset" -> TODO()
                else -> TODO()
            }

            "guild.role" -> when (method) {
                "list" -> TODO()
                "create" -> TODO()
                "update" -> TODO()
                "delete" -> TODO()
                else -> TODO()
            }

            "login" -> when (method) {
                "get" -> TODO()
                else -> TODO()
            }

            "message" -> when (method) {
                "create" -> TODO()
                "get" -> TODO()
                "delete" -> TODO()
                "update" -> TODO()
                "list" -> TODO()
                else -> TODO()
            }

            "reaction" -> when (method) {
                "create" -> TODO()
                "delete" -> TODO()
                "clear" -> TODO()
                "list" -> TODO()
                else -> TODO()
            }

            "user" -> when (method) {
                "get" -> TODO()
                else -> TODO()
            }

            "friend" -> when (method) {
                "list" -> TODO()
                "approve" -> TODO()
                else -> TODO()
            }

            else -> TODO()
        }
    }

    override suspend fun upload(
        resource: String,
        method: String,
        platform: String,
        self_id: String,
        content: List<FormData>
    ): Map<String, String> =
        HttpClient {
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