@file:Suppress("MemberVisibilityCanBePrivate")

package cn.yurin.yutorix.module.yhchat.adapter

import cn.yurin.yutori.Channel
import cn.yurin.yutori.AdapterContext
import cn.yurin.yutori.Event
import cn.yurin.yutori.AdapterEventService
import cn.yurin.yutori.Guild
import cn.yurin.yutori.GuildMember
import cn.yurin.yutori.GuildMemberEvents
import cn.yurin.yutori.GuildRole
import cn.yurin.yutori.Message
import cn.yurin.yutori.MessageEvents
import cn.yurin.yutori.RootActions
import cn.yurin.yutori.Yutori
import cn.yurin.yutori.SigningEvent
import cn.yurin.yutori.User
import cn.yurin.yutori.message.message
import cn.yurin.yutorix.module.yhchat.YhChatEvent
import cn.yurin.yutorix.module.yhchat.YhChatProperties
import cn.yurin.yutorix.module.yhchat.message.yhchat
import cn.yurin.yutori.nick
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
import kotlinx.serialization.json.Json

class YhChatAdapterEventService(
    alias: String?,
    val properties: YhChatProperties,
    val yutori: Yutori
) : AdapterEventService(alias) {
    val service = YhChatAdapterActionService(properties, yutori.name)
    private var job by atomic<Job?>(null)
    private val idMap = mapOf<Int, String>()
    private var last = 0

    init {
        yutori.actionsList += RootActions(alias, "yhchat", properties.userId, service, yutori)
    }

    override suspend fun connect() {
        coroutineScope {
            job = launch {
                try {
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
                } catch (e: Exception) {
                    Logger.w(yutori.name, e) { "WebHook server exception" }
                }
            }
        }
    }

    private fun parseMessageCreatedEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ) = Event<SigningEvent>(
        alias = alias,
        id = last++,
        type = MessageEvents.CREATED,
        platform = "yhchat",
        selfId = properties.userId,
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
            parentId = null
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
                joinedAt = null
            )

            else -> null
        },
        message = Message(
            id = event.message.msgId,
            content = parseMessageContent(event),
            channel = null,
            guild = null,
            member = null,
            user = null,
            createdAt = event.message.sendTime,
            updatedAt = event.message.sendTime
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
            isBot = false
        )
    )

    private fun parseInteractionCommandEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseBotFollowedEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseBotUnfollowedEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseGuildMemberAddedEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ) = Event<SigningEvent>(
        alias = alias,
        id = last++,
        type = GuildMemberEvents.ADDED,
        platform = "yhchat",
        selfId = properties.userId,
        timestamp = timestamp,
        argv = null,
        button = null,
        channel = Channel(
            id = event.chat.chatId,
            type = Channel.Type.TEXT,
            name = null,
            parentId = null
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
            joinedAt = event.message.sendTime
        ),
        message = Message(
            id = event.message.msgId,
            content = parseMessageContent(event),
            channel = null,
            guild = null,
            member = null,
            user = null,
            createdAt = event.message.sendTime,
            updatedAt = event.message.sendTime
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
            isBot = false
        )
    )

    private fun parseGuildMemberRemovedEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ) = Event<SigningEvent>(
        alias = alias,
        id = last++,
        type = GuildMemberEvents.REMOVED,
        platform = "yhchat",
        selfId = properties.userId,
        timestamp = timestamp,
        argv = null,
        button = null,
        channel = Channel(
            id = event.chat.chatId,
            type = Channel.Type.TEXT,
            name = null,
            parentId = null
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
            joinedAt = event.message.sendTime
        ),
        message = Message(
            id = event.message.msgId,
            content = parseMessageContent(event),
            channel = null,
            guild = null,
            member = null,
            user = null,
            createdAt = event.message.sendTime,
            updatedAt = event.message.sendTime
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
            isBot = false
        )
    )

    private fun parseInteractionButtonEvent(
        timestamp: Long,
        event: cn.yurin.yutorix.module.yhchat.Event
    ): Event<SigningEvent> = TODO()

    private fun parseMessageContent(event: cn.yurin.yutorix.module.yhchat.Event) =
        message(yutori) {
            when (event.message.contentType) {
                "text" -> text(event.message.content.text!!)
                "image" -> image(src = event.message.content.imageUrl!!)
                "file" -> file(
                    src = event.message.content.fileUrl!!,
                    title = event.message.content.fileName!!
                )

                "markdown" -> yhchat.markdown(event.message.content.text!!)
                "html" -> yhchat.html(event.message.content.text!!)
            }
            event.message.content.buttons?.forEach { button ->
                when (button.actionType) {
                    1 -> button(
                        type = "link",
                        href = button.url
                    ) {
                        text(button.text)
                    }

                    2 -> button(
                        type = "input",
                        text = button.value
                    ) {
                        text(button.text)
                    }

                    3 -> button(
                        type = "action",
                        id = button.value
                    ) {
                        text(button.text)
                    }
                }
            }
        }

    private suspend fun onEvent(event: Event<SigningEvent>) {
        val name = yutori.name
        try {
            when (event.type) {
                MessageEvents.CREATED -> Logger.i(name) {
                    buildString {
                        append("${event.platform}(${event.selfId}) 接收事件(${event.type}): ")
                        append("${event.nullableChannel!!.name}(${event.nullableChannel!!.id})")
                        append("-")
                        append("${event.nick()}(${event.nullableUser!!.id})")
                        append(": ")
                        append(event.nullableMessage!!.content)
                    }
                }

                else -> Logger.i(name) { "${event.platform}(${event.selfId}) 接收事件: ${event.type}" }
            }
            Logger.d(name) { "事件详细信息: $event" }
            yutori.adapter.container(AdapterContext(yutori.actionsList.first {
                it.platform == "yhchat" && it.userId == properties.userId
            }, event, yutori))
        } catch (e: Exception) {
            Logger.w(name, e) { "处理事件时出错: $event" }
        }
    }

    override fun disconnect() {
        job?.cancel("Event service disconnect")
    }
}