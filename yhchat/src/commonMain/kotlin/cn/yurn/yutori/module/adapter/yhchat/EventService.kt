package cn.yurn.yutori.module.adapter.yhchat

import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.EventService
import cn.yurn.yutori.Guild
import cn.yurn.yutori.GuildMember
import cn.yurn.yutori.GuildMemberEvents
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
                                val event = call.receive<YhChatEvent>()
                                onEvent(
                                    when (event.header.eventType) {
                                        "message.receive.normal" -> parseMessageCreatedEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "message.receive.instruction" -> parseInteractionCommandEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "bot.followed" -> parseBotFollowedEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "bot.unfollowed" -> parseBotUnfollowedEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "group.join" -> parseGuildMemberAddedEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "group.leave" -> parseGuildMemberRemovedEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        "button.report.inline" -> parseInteractionButtonEvent(
                                            event.header.eventTime,
                                            event.event
                                        )

                                        else -> throw RuntimeException("Unsupported event: ${event.header.eventType}")
                                    }
                                )
                            } catch (e: Throwable) {
                                Logger.e("YhChat", e)
                            }
                        }
                    }
                }.start(wait = true)
            }
        }
    }

    private fun parseMessageCreatedEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ) = Event<SigningEvent>(
        id = last++,
        type = MessageEvents.Created,
        platform = "YhChat",
        self_id = properties.selfId,
        timestamp = timestamp,
        argv = null,
        button = null,
        channel = Channel(
            id = when (event.chat.chatType) {
                "bot" -> "private:${event.sender.senderId}"
                "group" -> event.chat.chatId
                else -> throw RuntimeException("Unknown chatType: ${event.chat.chatType}")
            },
            type = when (event.chat.chatType) {
                "bot" -> Channel.Type.DIRECT
                "group" -> Channel.Type.TEXT
                else -> throw RuntimeException("Unknown chatType: ${event.chat.chatType}")
            },
            name = when (event.chat.chatType) {
                "bot" -> event.sender.senderNickname
                else -> null
            },
            parent_id = null
        ),
        guild = when (event.chat.chatType) {
            "group" -> Guild(
                id = event.chat.chatId,
                name = null,
                avatar = null
            )

            else -> null
        },
        login = null,
        member = when (event.chat.chatType) {
            "group" -> GuildMember(
                user = null,
                nick = event.sender.senderNickname,
                avatar = null,
                joined_at = null
            )

            else -> null
        },
        message = Message(
            id = event.message.msgId,
            content = event.message.content.text!!,
            channel = null,
            guild = null,
            member = null,
            user = null,
            created_at = event.message.sendTime,
            updated_at = event.message.sendTime
        ),
        operator = null,
        role = GuildRole(
            id = event.sender.senderUserLevel,
            name = null
        ),
        user = User(
            id = event.sender.senderId,
            name = event.sender.senderNickname,
            nick = event.sender.senderNickname,
            avatar = null,
            is_bot = false
        )
    )

    private fun parseInteractionCommandEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseBotFollowedEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseBotUnfollowedEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseGuildMemberAddedEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ) = Event<SigningEvent>(
        id = last++,
        type = GuildMemberEvents.Added,
        platform = "YhChat",
        self_id = properties.selfId,
        timestamp = timestamp,
        argv = null,
        button = null,
        channel = Channel(
            id = event.chat.chatId,
            type = Channel.Type.TEXT,
            name = null,
            parent_id = null
        ),
        guild = Guild(
            id = event.chat.chatId,
            name = null,
            avatar = null
        ),
        login = null,
        member = GuildMember(
            user = null,
            nick = event.sender.senderNickname,
            avatar = null,
            joined_at = event.message.sendTime
        ),
        message = Message(
            id = event.message.msgId,
            content = event.message.content.text!!,
            channel = null,
            guild = null,
            member = null,
            user = null,
            created_at = event.message.sendTime,
            updated_at = event.message.sendTime
        ),
        operator = null,
        role = GuildRole(
            id = event.sender.senderUserLevel,
            name = null
        ),
        user = User(
            id = event.sender.senderId,
            name = event.sender.senderNickname,
            nick = event.sender.senderNickname,
            avatar = null,
            is_bot = false
        )
    )

    private fun parseGuildMemberRemovedEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ) = Event<SigningEvent>(
        id = last++,
        type = GuildMemberEvents.Removed,
        platform = "YhChat",
        self_id = properties.selfId,
        timestamp = timestamp,
        argv = null,
        button = null,
        channel = Channel(
            id = event.chat.chatId,
            type = Channel.Type.TEXT,
            name = null,
            parent_id = null
        ),
        guild = Guild(
            id = event.chat.chatId,
            name = null,
            avatar = null
        ),
        login = null,
        member = GuildMember(
            user = null,
            nick = event.sender.senderNickname,
            avatar = null,
            joined_at = event.message.sendTime
        ),
        message = Message(
            id = event.message.msgId,
            content = event.message.content.text!!,
            channel = null,
            guild = null,
            member = null,
            user = null,
            created_at = event.message.sendTime,
            updated_at = event.message.sendTime
        ),
        operator = null,
        role = GuildRole(
            id = event.sender.senderUserLevel,
            name = null
        ),
        user = User(
            id = event.sender.senderId,
            name = event.sender.senderNickname,
            nick = event.sender.senderNickname,
            avatar = null,
            is_bot = false
        )
    )

    private fun parseInteractionButtonEvent(
        timestamp: Long,
        event: cn.yurn.yutori.module.adapter.yhchat.Event
    ): Event<SigningEvent> = TODO()

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