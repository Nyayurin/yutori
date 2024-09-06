package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.BidiPagingList
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
import cn.yurn.yutori.module.yhchat.Content
import cn.yurn.yutori.module.yhchat.MessageInfo
import cn.yurn.yutori.module.yhchat.YhChatProperties
import cn.yurn.yutori.module.yhchat.message.element.Markdown
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
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
    ): List<cn.yurn.yutori.Message> {
        val channelId = content["channel_id"] as String
        val msg = content["content"] as List<MessageElement>
        val contents = msg.transToActions()
        return contents.map { (type, content) ->
            client.post {
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
                        }
                    }
                })
                Logger.d(name) {
                    """
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
                }
            }
        }.map { response ->
            response.body<MessageInfo>()
        }.map { info ->
            cn.yurn.yutori.Message(
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
            client.post {
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
                            "markdown" -> TODO()
                        }
                    }
                })
                Logger.d(name) {
                    """
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
                }
            }
        }
    }

    private suspend fun sendMessageDelete(client: HttpClient, content: Map<String, Any?>) {
        client.post {
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
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
    }

    private suspend fun sendMessageList(
        client: HttpClient,
        content: Map<String, Any?>
    ): BidiPagingList<cn.yurn.yutori.Message> {
        client.get {
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
                YhChat Action: url: ${this.url},
                    headers: ${this.headers.build()},
                    body: ${this.body}
                """.trimIndent()
            }
        }
        return BidiPagingList(emptyList())
    }

    private fun List<MessageElement>.transToActions(): List<Pair<String, Content>> = buildList {
        val builder = StringBuilder()
        for (element in this@transToActions) {
            when (element) {
                is Text -> builder.append(element.text)
                is At -> TODO()
                is Sharp -> TODO()
                is Href -> builder.append(element.href)
                is Image -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("image" to Content(imageUrl = element.src))
                }

                is Audio -> TODO()
                is Video -> TODO()
                is File -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("file" to Content(fileUrl = element.src, fileName = element.title.toString()))
                }
                is Bold, is Strong -> TODO()
                is Idiomatic, is Em -> TODO()
                is Underline, is Ins -> TODO()
                is Strikethrough, is Delete -> TODO()
                is Spl -> TODO()
                is Code -> TODO()
                is Sup -> TODO()
                is Sub -> TODO()
                is Br -> builder.append("\n")
                is Paragraph -> TODO()
                is Message -> TODO()
                is Quote -> TODO()
                is Author -> TODO()
                is Button -> TODO()
                is Markdown -> {
                    if (builder.isNotEmpty()) {
                        add("text" to Content(text = builder.toString()))
                        builder.clear()
                    }
                    add("markdown" to Content(text = element.children.filterIsInstance<Text>().joinToString("") { it.text }))
                }
            }
        }
        if (builder.isNotEmpty()) {
            add("text" to Content(text = builder.toString()))
        }
    }

    override suspend fun upload(
        resource: String, method: String, platform: String, self_id: String, content: List<FormData>
    ): Map<String, String> = TODO("Unsupported File Upload")
}