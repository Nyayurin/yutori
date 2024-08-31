package cn.yurn.yutori.module.adapter.yhchat

import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.EventService
import cn.yurn.yutori.Guild
import cn.yurn.yutori.GuildMember
import cn.yurn.yutori.GuildRole
import cn.yurn.yutori.Message
import cn.yurn.yutori.MessageEvents
import cn.yurn.yutori.Satori
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.nick
import co.touchlab.kermit.Logger
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class YhChatEventService(
    val properties: YhChatProperties,
    val satori: Satori
) : EventService {
    private val service = SatoriActionService(properties, satori.name)
    private var job: Job? by atomic(null)
    private val idMap = mapOf<Int, String>()
    private var last = 0

    override suspend fun connect() {
        coroutineScope {
            job = launch {
                embeddedServer(
                    factory = CIO,
                    host = properties.host,
                    port = properties.port
                ) {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                    routing {
                        post("${properties.path}/") {
                            try {
                                val yhChatEvent = call.receive<YhChatEvent>()
                                when (yhChatEvent.header.eventType) {
                                    "message.receive.normal" -> {
                                        val event = Event<SigningEvent>(
                                            id = last++,
                                            type = MessageEvents.Created,
                                            platform = "YhChat",
                                            self_id = properties.selfId,
                                            timestamp = yhChatEvent.header.eventTime,
                                            argv = null,
                                            button = null,
                                            channel = Channel(
                                                id = when (yhChatEvent.event.chat.chatType) {
                                                    "bot" -> "private:${yhChatEvent.event.sender.senderId}"
                                                    "group" -> yhChatEvent.event.chat.chatId
                                                    else -> throw RuntimeException("Unknown chatType: ${yhChatEvent.event.chat.chatType}")
                                                },
                                                type = when (yhChatEvent.event.chat.chatType) {
                                                    "bot" -> Channel.Type.DIRECT
                                                    "group" -> Channel.Type.TEXT
                                                    else -> throw RuntimeException("Unknown chatType: ${yhChatEvent.event.chat.chatType}")
                                                },
                                                name = when (yhChatEvent.event.chat.chatType) {
                                                    "bot" -> yhChatEvent.event.sender.senderNickname
                                                    else -> null
                                                },
                                                parent_id = null
                                            ),
                                            guild = when (yhChatEvent.event.chat.chatType) {
                                                "group" -> Guild(
                                                    id = yhChatEvent.event.chat.chatId,
                                                    name = null,
                                                    avatar = null
                                                )
                                                else -> null
                                            },
                                            login = null,
                                            member = when (yhChatEvent.event.chat.chatType) {
                                                "group" -> GuildMember(
                                                    user = null,
                                                    nick = null,
                                                    avatar = null,
                                                    joined_at = null
                                                )
                                                else -> null
                                            },
                                            message = Message(
                                                id = yhChatEvent.event.message.msgId,
                                                content = yhChatEvent.event.message.content.text,
                                                channel = null,
                                                guild = null,
                                                member = null,
                                                user = null,
                                                created_at = yhChatEvent.event.message.sendTime,
                                                updated_at = yhChatEvent.event.message.sendTime
                                            ),
                                            operator = null,
                                            role = GuildRole(
                                                id = yhChatEvent.event.chat.chatType,
                                                name = null
                                            ),
                                            user = User(
                                                id = yhChatEvent.event.sender.senderId,
                                                name = yhChatEvent.event.sender.senderNickname,
                                                nick = yhChatEvent.event.sender.senderNickname,
                                                avatar = null,
                                                is_bot = false
                                            )
                                        )
                                        onEvent(event)
                                    }
                                }
                            } catch (e: Throwable) {
                                Logger.e("YhChat", e)
                            }
                        }
                    }
                }.start(wait = true)
            }
        }
    }

    private suspend fun onEvent(event: Event<SigningEvent>) {
        val name = satori.name
        val details = Json.encodeToString(event)
        try {
            when (event.type) {
                MessageEvents.Created -> Logger.i(name) {
                    buildString {
                        append("${event.platform}(${event.self_id}) 接收事件(${event.type}): ")
                        append("${event.nullable_channel!!.name}(${event.nullable_channel!!.id})")
                        append("-")
                        append("${event.nick()}(${event.nullable_user!!.id})")
                        append(": ")
                        append(event.nullable_message!!.content)
                    }
                }

                else -> Logger.i(name) { "${event.platform}(${event.self_id}) 接收事件: ${event.type}" }
            }
            Logger.d(name) { "事件详细信息: $details" }
            satori.client.container(event, satori, service)
        } catch (e: Exception) {
            Logger.w(name, e) { "处理事件时出错: $details" }
        }
    }

    override fun disconnect() {
        job?.cancel("Event service disconnect")
    }
}