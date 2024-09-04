@file:Suppress(
    "MemberVisibilityCanBePrivate",
    "unused",
    "HttpUrlsUsage",
    "UastIncorrectHttpHeaderInspection"
)

package cn.yurn.yutori.module.satori.adapter

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.BidiPagingList
import cn.yurn.yutori.Channel
import cn.yurn.yutori.FormData
import cn.yurn.yutori.Guild
import cn.yurn.yutori.GuildMember
import cn.yurn.yutori.GuildRole
import cn.yurn.yutori.Message
import cn.yurn.yutori.PagingList
import cn.yurn.yutori.SatoriProperties
import cn.yurn.yutori.User
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.module.satori.ChannelSerializer
import cn.yurn.yutori.module.satori.GuildMemberSerializer
import cn.yurn.yutori.module.satori.GuildRoleSerializer
import cn.yurn.yutori.module.satori.GuildSerializer
import cn.yurn.yutori.module.satori.LoginSerializer
import cn.yurn.yutori.module.satori.MessageSerializer
import cn.yurn.yutori.module.satori.UserSerializer
import cn.yurn.yutori.module.satori.serialize
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import korlibs.io.lang.unsupported
import kotlinx.serialization.builtins.ListSerializer
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
                "get" -> Json.decodeFromString(ChannelSerializer, body)
                "list" -> Json.decodeFromString<PagingList<Channel>>(body)
                "create" -> Json.decodeFromString(ChannelSerializer, body)
                "update" -> Unit
                "delete" -> Unit
                "mute" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "user.channel" -> when (method) {
                "create" -> Json.decodeFromString(ChannelSerializer, body)
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "guild" -> when (method) {
                "get" -> Json.decodeFromString(GuildSerializer, body)
                "list" -> Json.decodeFromString<PagingList<Guild>>(body)
                "approve" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "guild.member" -> when (method) {
                "get" -> Json.decodeFromString(GuildMemberSerializer, body)
                "list" -> Json.decodeFromString<PagingList<GuildMember>>(body)
                "kick" -> Unit
                "mute" -> Unit
                "approve" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "guild.member.role" -> when (method) {
                "set" -> Unit
                "unset" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "guild.role" -> when (method) {
                "list" -> Json.decodeFromString<PagingList<GuildRole>>(body)
                "create" -> Json.decodeFromString(GuildRoleSerializer, body)
                "update" -> Unit
                "delete" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "login" -> when (method) {
                "get" -> Json.decodeFromString(LoginSerializer, body)
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "message" -> when (method) {
                "create" -> Json.decodeFromString(ListSerializer(MessageSerializer), body)
                "get" -> Json.decodeFromString(MessageSerializer, body)
                "delete" -> Unit
                "update" -> Unit
                "list" -> Json.decodeFromString<BidiPagingList<Message>>(body)
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "reaction" -> when (method) {
                "create" -> Unit
                "delete" -> Unit
                "clear" -> Unit
                "list" -> Json.decodeFromString<PagingList<User>>(body)
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "user" -> when (method) {
                "get" -> Json.decodeFromString(UserSerializer, body)
                else -> unsupported("Unsupported action: $resource.$method")
            }

            "friend" -> when (method) {
                "list" -> Json.decodeFromString<PagingList<User>>(body)
                "approve" -> Unit
                else -> unsupported("Unsupported action: $resource.$method")
            }

            else -> unsupported("Unsupported action: $resource.$method")
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