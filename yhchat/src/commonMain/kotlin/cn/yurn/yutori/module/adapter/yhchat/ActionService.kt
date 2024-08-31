package cn.yurn.yutori.module.adapter.yhchat

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.FormData
import cn.yurn.yutori.module.adapter.satori.platformEngine
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class SatoriActionService(val properties: YhChatProperties, val name: String) : ActionService {
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
        val response = client.request {
            val suffix = "$resource.$method"
            this.method = when (suffix) {
                "message.create", "message.update", "message.delete" -> HttpMethod.Post
                "message.list" -> HttpMethod.Get
                else -> throw RuntimeException("Unsupported Action: $suffix")
            }
            url {
                protocol = URLProtocol.HTTPS
                host = "chat-go.jwzhd.com"
                parameter("token", properties.token)
                appendPathSegments(
                    "open-apis", "v1", "bot", when (suffix) {
                        "message.create" -> "send"
                        "message.update" -> "edit"
                        "message.delete" -> "recall"
                        "message.list" -> "messages"
                        else -> throw RuntimeException("Unsupported Action: $suffix")
                    }
                )
            }
            contentType(ContentType.Application.Json)
            val channelId = content["channel_id"] as String
            val message = (content["content"] as String).replace("\\n", "\n")
            setBody(
                buildJsonObject {
                    when (suffix) {
                        "message.create", "message.delete", "message.update" -> {
                            when (suffix) {
                                "message.create", "message.delete" -> {
                                    put("msgId", content["message_id"] as String)
                                }
                            }
                            put("recvId", channelId.removePrefix("private:"))
                            put(
                                "recvType", when {
                                    channelId.startsWith("private:") -> "user"
                                    else -> "group"
                                }
                            )

                            when (suffix) {
                                "message.create", "message.update" -> {
                                    put("contentType", "text")
                                    putJsonObject("content") {
                                        put("text", message)
                                    }
                                }
                            }
                        }

                        "message.list" -> {
                            put("chat-id", channelId.removePrefix("private:"))
                            put(
                                "chat-type", when {
                                    channelId.startsWith("private:") -> "user"
                                    else -> "group"
                                }
                            )
                        }
                    }
                }
            )
            Logger.d(name) {
                """
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        Logger.d(name) { "YhChat Action Response: $response" }
        response.body(typeInfo)
    }

    override suspend fun upload(
        resource: String,
        method: String,
        platform: String,
        self_id: String,
        content: List<FormData>
    ): Map<String, String> =
        mapOf()
}