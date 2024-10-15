@file:Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurin.yutorix.module.yhchat.adapter

import cn.yurin.yutori.AdapterActionService
import cn.yurin.yutori.BidiPagingList
import cn.yurin.yutori.FormData
import cn.yurin.yutori.message.element.At
import cn.yurin.yutori.message.element.Audio
import cn.yurin.yutori.message.element.Author
import cn.yurin.yutori.message.element.Bold
import cn.yurin.yutori.message.element.Br
import cn.yurin.yutori.message.element.Button
import cn.yurin.yutori.message.element.Code
import cn.yurin.yutori.message.element.Delete
import cn.yurin.yutori.message.element.Em
import cn.yurin.yutori.message.element.File
import cn.yurin.yutori.message.element.Href
import cn.yurin.yutori.message.element.Idiomatic
import cn.yurin.yutori.message.element.Image
import cn.yurin.yutori.message.element.Ins
import cn.yurin.yutori.message.element.Message
import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.element.Paragraph
import cn.yurin.yutori.message.element.Quote
import cn.yurin.yutori.message.element.Sharp
import cn.yurin.yutori.message.element.Spl
import cn.yurin.yutori.message.element.Strikethrough
import cn.yurin.yutori.message.element.Strong
import cn.yurin.yutori.message.element.Sub
import cn.yurin.yutori.message.element.Sup
import cn.yurin.yutori.message.element.Text
import cn.yurin.yutori.message.element.Underline
import cn.yurin.yutori.message.element.Video
import cn.yurin.yutorix.module.yhchat.Content
import cn.yurin.yutorix.module.yhchat.MessageInfo
import cn.yurin.yutorix.module.yhchat.YhChatProperties
import cn.yurin.yutorix.module.yhchat.message.element.HTML
import cn.yurin.yutorix.module.yhchat.message.element.Markdown
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class YhChatAdapterActionService(
    val properties: YhChatProperties,
    val name: String
) : AdapterActionService() {
    override suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        userId: String?,
        content: Map<String, Any?>
    ): Any = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.use { client ->
        when (resource) {
            "message" -> when (method) {
                "create" -> sendMessageCreate(client, content)
                "update" -> sendMessageUpdate(client, content)
                "delete" -> sendMessageDelete(client, content)
                "list" -> sendMessageList(client, content)
                else -> throw UnsupportedOperationException("Unsupported action: $resource.$method")
            }

            else -> throw UnsupportedOperationException("Unsupported action: $resource.$method")
        }
    }

    private suspend fun sendMessageCreate(
        client: HttpClient,
        content: Map<String, Any?>
    ): List<cn.yurin.yutori.Message> {
        val channelId = content["channel_id"] as String
        val msg = content["content"] as List<MessageElement>
        val contents = msg.transToActions()
        return contents.map { (type, content) ->
            val response = client.post {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "chat-go.jwzhd.com"
                    parameter("token", properties.token)
                    appendPathSegments("open-apis", "v1", "bot", "send")
                }
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    put("recvId", channelId.removePrefix("private:"))
                    put(
                        "recvType", when {
                            channelId.startsWith("private:") -> "user"
                            else -> "group"
                        }
                    )
                    put("contentType", type)
                    putJsonObject("content") {
                        when (type) {
                            "text" -> put("text", content.text!!.replace("\"", "\\\""))
                            "image" -> put("imageUrl", content.imageUrl!!)
                            "file" -> {
                                put("fileName", content.fileName!!)
                                put("fileUrl", content.fileUrl!!)
                            }

                            "markdown" -> put("text", content.text!!.replace("\"", "\\\""))
                            "html" -> put("text", content.text!!.replace("\"", "\\\""))
                        }
                    }
                })
                Logger.d(name) {
                    """
                    YhChat Action Request: url: ${this.url},
                        headers: ${this.headers.build()},
                        body: ${this.body}
                    """.trimIndent()
                }
            }
            Logger.d(name) { "YhChat Action Response: $response" }
            val info = Json.decodeFromJsonElement<MessageInfo>(
                Json.parseToJsonElement(response.bodyAsText())
                    .jsonObject["data"]!!
                    .jsonObject["messageInfo"]!!
            )
            cn.yurin.yutori.Message(
                id = info.msgId,
                content = emptyList()
            )
        }
    }

    private suspend fun sendMessageUpdate(client: HttpClient, content: Map<String, Any?>) {
        val channelId = content["channel_id"] as String
        val messageId = content["message_id"] as String
        val msg = content["content"] as List<MessageElement>
        val contents = msg.transToActions()
        contents.map { (type, content) ->
            val response = client.post {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "chat-go.jwzhd.com"
                    parameter("token", properties.token)
                    appendPathSegments("open-apis", "v1", "bot", "edit")
                }
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    put("msgId", messageId)
                    put("recvId", channelId.removePrefix("private:"))
                    put(
                        "recvType", when {
                            channelId.startsWith("private:") -> "user"
                            else -> "group"
                        }
                    )
                    put("contentType", "text")
                    putJsonObject("content") {
                        when (type) {
                            "text" -> put("text", content.text!!.replace("\"", "\\\""))
                            "image" -> put("imageUrl", content.imageUrl!!)
                            "file" -> {
                                put("fileName", content.fileName!!)
                                put("fileUrl", content.fileUrl!!)
                            }

                            "markdown" -> put("text", content.text!!.replace("\"", "\\\""))
                            "html" -> put("text", content.text!!.replace("\"", "\\\""))
                        }
                    }
                })
                Logger.d(name) {
                    """
                    YhChat Action Request: url: ${this.url},
                        headers: ${this.headers.build()},
                        body: ${this.body}
                    """.trimIndent()
                }
            }
            Logger.d(name) { "YhChat Action Response: $response" }
        }
    }

    private suspend fun sendMessageDelete(client: HttpClient, content: Map<String, Any?>) {
        val response = client.post {
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
            Logger.d(name) {
                """
                YhChat Action Request: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        Logger.d(name) { "YhChat Action Response: $response" }
    }

    private suspend fun sendMessageList(
        client: HttpClient,
        content: Map<String, Any?>
    ): BidiPagingList<Message> {
        val response = client.get {
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
            Logger.d(name) {
                """
                YhChat Action Request: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        Logger.d(name) { "YhChat Action Response: $response" }
        return BidiPagingList(emptyList())
    }

    private fun List<MessageElement>.transToActions(): List<Pair<String, Content>> = buildList {
        val builder = StringBuilder()
        for (element in this@transToActions) {
            when (element) {
                is Text -> builder.append(element.content)
                is At -> { }
                is Sharp -> { }
                is Href -> builder.append(element.href)
                is Image -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("image" to Content(imageUrl = element.src))
                }

                is Audio -> { }
                is Video -> { }
                is File -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add(
                        "file" to Content(
                            fileUrl = element.src,
                            fileName = element.title.toString()
                        )
                    )
                }

                is Bold, is Strong -> { }
                is Idiomatic, is Em -> { }
                is Underline, is Ins -> { }
                is Strikethrough, is Delete -> { }
                is Spl -> { }
                is Code -> { }
                is Sup -> { }
                is Sub -> { }
                is Br -> builder.append("\n")
                is Paragraph -> { }
                is cn.yurin.yutori.message.element.Message -> { }
                is Quote -> { }
                is Author -> { }
                is Button -> { }
                is Markdown -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("markdown" to Content(text = element.content))
                }

                is HTML -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("html" to Content(text = element.content))
                }
            }
        }
        if (builder.isNotEmpty()) {
            add("text" to Content(text = builder.toString()))
        }
    }

    override suspend fun upload(
        resource: String, method: String, platform: String, userId: String, content: List<FormData>
    ): Map<String, String> = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.use { client ->
        val url = URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = "chat-go.jwzhd.com"
            appendPathSegments("open-apis", "v1", "image", "upload")
            parameters {
                append("token", properties.token)
            }
        }.buildString()
        val map = mutableMapOf<String, String>()
        for (data in content) {
            val formData = formData {
                append("image", data.content)
            }
            Logger.d(name) {
                """
                YhChat Action Request: url: $url,
                    body: $formData
                """.trimIndent()
            }
            val response = client.submitFormWithBinaryData(url, formData)
            Logger.d(name) { "YhChat Action Response: $response" }
            map[data.name] = Json.parseToJsonElement(response.bodyAsText())
                .jsonObject["data"]!!
                .jsonObject["imageKey"]!!
                .jsonPrimitive.content
        }
        map.toMap()
    }
}