package cn.yurn.yutori.module.adapter.yhchat

import cn.yurn.yutori.ActionService
import cn.yurn.yutori.FormData
import cn.yurn.yutori.module.adapter.satori.platformEngine
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
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
        val response = client.post {
            url {
                protocol = URLProtocol.HTTPS
                host = "chat-go.jwzhd.com"
                parameter("token", properties.token)
                appendPathSegments("open-apis", "v1", "bot", "send")
            }
            contentType(ContentType.Application.Json)
            val channelId = content["channel_id"] as String
            val message = (content["content"] as String).replace("\\n", "\n")
            setBody(
                buildJsonObject {
                    put("recvId", channelId.removePrefix("private:"))
                    put("recvType", when {
                        channelId.startsWith("private:") -> "user"
                        else -> "group"
                    })
                    put("contentType", "text")
                    putJsonObject("content") {
                        put("text", message)
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