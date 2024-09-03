package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.FormData
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.Message
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import cn.yurn.yutori.module.yhchat.YhChatProperties
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class YhChatActionService(val properties: YhChatProperties, val name: String) : ActionService {
    override suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        self_id: String?,
        content: Map<String, Any?>
    ): String = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.use { client ->
        val response = client.request {
            when (resource) {
                "message" -> when (method) {
                    "create" -> sendMessageCreate(content)
                    "update" -> sendMessageUpdate(content)
                    "delete" -> sendMessageDelete(content)
                    "list" -> sendMessageList(content)
                    else -> throw RuntimeException("Unsupported Method: $method")
                }

                else -> throw RuntimeException("Unsupported Resource: $resource")
            }
            Logger.d(name) {
                """
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        Logger.d(name) { "YhChat Action Response: $response" }
        val body = response.bodyAsText()
        when (resource) {
            "message" -> when (method) {
                "create" -> TODO()
                "delete" -> TODO()
                "update" -> TODO()
                "list" -> TODO()
                else -> TODO()
            }

            else -> TODO()
        }
    }

    private fun HttpRequestBuilder.sendMessageCreate(content: Map<String, Any?>) {
        method = HttpMethod.Post
        url {
            protocol = URLProtocol.HTTPS
            host = "chat-go.jwzhd.com"
            parameter("token", properties.token)
            appendPathSegments("open-apis", "v1", "bot", "send")
        }
        contentType(ContentType.Application.Json)
        val channelId = content["channel_id"] as String
        val msg = content["content"] as List<MessageElement>
        setBody(buildJsonObject {
            put("recvId", channelId.removePrefix("private:"))
            put(
                "recvType", when {
                    channelId.startsWith("private:") -> "user"
                    else -> "group"
                }
            )
            put("contentType", "text")
            putJsonObject("content") {
                put(
                    "text",
                    parseElements(msg).replace("\"", "\\\"")
                )
            }
        })
    }

    private fun HttpRequestBuilder.sendMessageUpdate(content: Map<String, Any?>) {
        method = HttpMethod.Post
        url {
            protocol = URLProtocol.HTTPS
            host = "chat-go.jwzhd.com"
            parameter("token", properties.token)
            appendPathSegments("open-apis", "v1", "bot", "edit")
        }
        contentType(ContentType.Application.Json)
        val channelId = content["channel_id"] as String
        val msg = content["content"] as List<MessageElement>
        setBody(buildJsonObject {
            put("msgId", content["message_id"] as String)
            put("recvId", channelId.removePrefix("private:"))
            put(
                "recvType", when {
                    channelId.startsWith("private:") -> "user"
                    else -> "group"
                }
            )
            put("contentType", "text")
            putJsonObject("content") {
                put(
                    "text",
                    parseElements(msg).replace("\"", "\\\"")
                )
            }
        })
    }

    private fun HttpRequestBuilder.sendMessageDelete(content: Map<String, Any?>) {
        method = HttpMethod.Post
        url {
            protocol = URLProtocol.HTTPS
            host = "chat-go.jwzhd.com"
            parameter("token", properties.token)
            appendPathSegments("open-apis", "v1", "bot", "recall")
        }
        contentType(ContentType.Application.Json)
        val channelId = content["channel_id"] as String
        setBody(buildJsonObject {
            put("msgId", content["message_id"] as String)
            put("recvId", channelId.removePrefix("private:"))
            put(
                "recvType", when {
                    channelId.startsWith("private:") -> "user"
                    else -> "group"
                }
            )
        })
    }

    private fun HttpRequestBuilder.sendMessageList(content: Map<String, Any?>) {
        method = HttpMethod.Get
        url {
            protocol = URLProtocol.HTTPS
            host = "chat-go.jwzhd.com"
            parameter("token", properties.token)
            appendPathSegments("open-apis", "v1", "bot", "messages")
        }
        contentType(ContentType.Application.Json)
        val channelId = content["channel_id"] as String
        setBody(buildJsonObject {
            put("chat-id", channelId.removePrefix("private:"))
            put(
                "chat-type", when {
                    channelId.startsWith("private:") -> "user"
                    else -> "group"
                }
            )
        })
    }

    private fun parseElements(elements: List<MessageElement>) = buildString {
        for (element in elements) {
            when (element) {
                is Text -> append(element.text)
                is At -> {}
                is Sharp -> {}
                is Href -> append(element.href)
                is Image -> {}
                is Audio -> {}
                is Video -> {}
                is File -> {}
                is Bold, is Strong -> {}
                is Idiomatic, is Em -> {}
                is Underline, is Ins -> {}
                is Strikethrough, is Delete -> {}
                is Spl -> {}
                is Code -> {}
                is Sup -> {}
                is Sub -> {}
                is Br -> append("\n")
                is Paragraph -> {}
                is Message -> {}
                is Quote -> {}
                is Author -> {}
                is Button -> {}
            }
        }
    }

    override suspend fun upload(
        resource: String, method: String, platform: String, self_id: String, content: List<FormData>
    ): Map<String, String> = TODO("Unsupported File Upload")
}