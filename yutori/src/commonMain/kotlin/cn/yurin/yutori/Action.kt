@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurin.yutori

import cn.yurin.yutori.message.MessageBuilder
import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.message

abstract class Actions
abstract class Action(
    val platform: String?,
    val userId: String?,
    val resource: String,
    val service: AdapterActionService
) {
    protected suspend inline fun <reified T> send(
        method: String, vararg content: Pair<String, Any?>
    ): T = service.send(resource, method, platform, userId, mapOf(*content)) as T

    protected suspend fun upload(method: String, content: List<FormData>): Map<String, String> =
        service.upload(resource, method, platform!!, userId!!, content)
}

class RootActions(
    val alias: String?,
    val platform: String,
    val userId: String,
    val service: AdapterActionService,
    val yutori: Yutori
) : Actions() {
    val channel = ChannelAction(platform, userId, service)
    val guild = GuildAction(platform, userId, service)
    val login = LoginAction(platform, userId, service)
    val message = MessageAction(yutori, platform, userId, service)
    val reaction = ReactionAction(platform, userId, service)
    val user = UserAction(platform, userId, service)
    val friend = FriendAction(platform, userId, service)
    val upload = UploadAction(platform, userId, service)
    val admin = AdminAction(service)
    val containers = mutableMapOf<String, Actions>().apply {
        for ((key, value) in yutori.actionsContainers) this[key] = value(platform, userId, service)
    }

    class ChannelAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "channel", service) {
        suspend fun get(
            channelId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Channel = send("get", "channel_id" to channelId, *contents)

        suspend fun list(
            guildId: String, next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<Channel> = send("list", "guild_id" to guildId, "next" to next, *contents)

        suspend fun create(
            guildId: String, data: Channel, vararg contents: Pair<String, Any> = arrayOf()
        ): Channel = send("create", "guild_id" to guildId, "data" to data, *contents)

        suspend fun update(
            channelId: String, data: Channel, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("update", "channel_id" to channelId, "data" to data, *contents)

        suspend fun delete(
            channelId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("delete", "channel_id" to channelId, *contents)

        suspend fun mute(
            channelId: String, duration: Number, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("mute", "channel_id" to channelId, "duration" to duration, *contents)
    }

    class GuildAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "guild", service) {
        val member = MemberAction(platform, userId, service)
        val role = RoleAction(platform, userId, service)

        suspend fun get(
            guildId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Guild = send("get", "guild_id" to guildId, *contents)

        suspend fun list(
            next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<Guild> = send("list", "next" to next, *contents)

        suspend fun approve(
            messageId: String,
            approve: Boolean,
            comment: String,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "approve",
            "message_id" to messageId,
            "approve" to approve,
            "comment" to comment,
            *contents
        )

        class MemberAction(
            platform: String, userId: String, service: AdapterActionService
        ) : Action(platform, userId, "guild.member", service) {
            val role = RoleAction(platform, userId, service)

            suspend fun get(
                guildId: String, userId: String, vararg contents: Pair<String, Any> = arrayOf()
            ): GuildMember = send("get", "guild_id" to guildId, "user_id" to userId, *contents)

            suspend fun list(
                guildId: String,
                next: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): PagingList<GuildMember> =
                send("list", "guild_id" to guildId, "next" to next, *contents)

            suspend fun kick(
                guildId: String,
                userId: String,
                permanent: Boolean? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "kick",
                "guild_id" to guildId,
                "user_id" to userId,
                "permanent" to permanent,
                *contents
            )

            suspend fun mute(
                guildId: String,
                userId: String,
                duration: Number,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "mute",
                "guild_id" to guildId,
                "user_id" to userId,
                "duration" to duration,
                *contents
            )

            suspend fun approve(
                messageId: String,
                approve: Boolean,
                comment: String,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "approve",
                "message_id" to messageId,
                "approve" to approve,
                "comment" to comment,
                *contents
            )

            class RoleAction(
                platform: String, userId: String, service: AdapterActionService
            ) : Action(platform, userId, "guild.member.role", service) {
                suspend fun set(
                    guildId: String,
                    userId: String,
                    roleId: String,
                    vararg contents: Pair<String, Any> = arrayOf()
                ): Unit = send(
                    "set",
                    "guild_id" to guildId,
                    "user_id" to userId,
                    "role_id" to roleId,
                    *contents
                )

                suspend fun unset(
                    guildId: String,
                    userId: String,
                    roleId: String,
                    vararg contents: Pair<String, Any> = arrayOf()
                ): Unit = send(
                    "unset",
                    "guild_id" to guildId,
                    "user_id" to userId,
                    "role_id" to roleId,
                    *contents
                )
            }
        }

        class RoleAction(
            platform: String, userId: String, service: AdapterActionService
        ) : Action(platform, userId, "guild.role", service) {
            suspend fun list(
                guildId: String,
                next: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): PagingList<GuildRole> =
                send("list", "guild_id" to guildId, "next" to next, *contents)

            suspend fun create(
                guildId: String, role: GuildRole, vararg contents: Pair<String, Any> = arrayOf()
            ): GuildRole = send("create", "guild_id" to guildId, "role" to role, *contents)

            suspend fun update(
                guildId: String,
                roleId: String,
                role: GuildRole,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "update", "guild_id" to guildId, "role_id" to roleId, "role" to role, *contents
            )

            suspend fun delete(
                guildId: String, roleId: String, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("delete", "guild_id" to guildId, "role_id" to roleId, *contents)
        }
    }

    class LoginAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "login", service) {
        suspend fun get(
            vararg contents: Pair<String, Any> = arrayOf()
        ): Login = send("get", *contents)
    }

    class MessageAction(
        private val yutori: Yutori, platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "message", service) {
        suspend fun create(
            channelId: String,
            content: List<MessageElement>,
            vararg contents: Pair<String, Any> = arrayOf()
        ): List<Message> =
            send("create", "channel_id" to channelId, "content" to content, *contents)

        suspend fun create(
            channelId: String,
            content: MessageBuilder.() -> Unit,
            vararg contents: Pair<String, Any> = arrayOf()
        ): List<Message> = create(channelId, message(yutori, content), *contents)

        suspend fun get(
            channelId: String, messageId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Message = send("get", "channel_id" to channelId, "message_id" to messageId, *contents)

        suspend fun delete(
            channelId: String, messageId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("delete", "channel_id" to channelId, "message_id" to messageId, *contents)

        suspend fun update(
            channelId: String,
            messageId: String,
            content: List<MessageElement>,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "update",
            "channel_id" to channelId,
            "message_id" to messageId,
            "content" to content,
            *contents
        )

        suspend fun update(
            channelId: String,
            messageId: String,
            content: MessageBuilder.() -> Unit,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = update(channelId, messageId, message(yutori, content), *contents)

        suspend fun list(
            channelId: String,
            next: String? = null,
            direction: BidiPagingList.Direction? = null,
            limit: Number? = null,
            order: BidiPagingList.Order? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): BidiPagingList<Message> = send(
            "list",
            "channel_id" to channelId,
            "next" to next,
            "direction" to direction?.value,
            "limit" to limit,
            "order" to order?.value,
            *contents
        )
    }

    class ReactionAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "reaction", service) {
        suspend fun create(
            channelId: String,
            messageId: String,
            emoji: String,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "create",
            "channel_id" to channelId,
            "message_id" to messageId,
            "emoji" to emoji,
            *contents
        )

        suspend fun delete(
            channelId: String,
            messageId: String,
            emoji: String,
            userId: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "delete",
            "channel_id" to channelId,
            "message_id" to messageId,
            "emoji" to emoji,
            "user_id" to userId,
            *contents
        )

        suspend fun clear(
            channelId: String,
            messageId: String,
            emoji: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "clear",
            "channel_id" to channelId,
            "message_id" to messageId,
            "emoji" to emoji,
            *contents
        )

        suspend fun list(
            channelId: String,
            messageId: String,
            emoji: String,
            next: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<User> = send(
            "list",
            "channel_id" to channelId,
            "message_id" to messageId,
            "emoji" to emoji,
            "next" to next,
            *contents
        )
    }

    class UserAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "user", service) {
        val channel = ChannelAction(platform, userId, service)

        suspend fun get(
            userId: String, vararg contents: Pair<String, Any> = arrayOf()
        ): User = send("get", "user_id" to userId, *contents)

        class ChannelAction(
            platform: String, userId: String, service: AdapterActionService
        ) : Action(platform, userId, "user.channel", service) {
            suspend fun create(
                userId: String,
                guildId: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Channel = send("create", "user_id" to userId, "guild_id" to guildId, *contents)
        }
    }

    class FriendAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "friend", service) {
        suspend fun list(
            next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<User> = send("list", "next" to next, *contents)

        suspend fun approve(
            messageId: String,
            approve: Boolean,
            comment: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "approve",
            "message_id" to messageId,
            "approve" to approve,
            "comment" to comment,
            *contents
        )
    }

    class UploadAction(
        platform: String, userId: String, service: AdapterActionService
    ) : Action(platform, userId, "upload", service) {
        suspend fun create(
            vararg contents: FormData
        ): Map<String, String> = upload("create", contents.toList())
    }

    class AdminAction(service: AdapterActionService) {
        val login = LoginAction(service)
        val webhook = WebhookAction(service)

        class LoginAction(
            service: AdapterActionService
        ) : Action(null, null, "login", service) {
            suspend fun list(
                vararg contents: Pair<String, Any> = arrayOf()
            ): List<Login> = send("list", *contents)
        }

        class WebhookAction(
            service: AdapterActionService
        ) : Action(null, null, "webhook", service) {
            suspend fun create(
                url: String, token: String? = null, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("list", "url" to url, "token" to token, *contents)

            suspend fun delete(
                url: String, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("approve", "url" to url, *contents)
        }
    }
}