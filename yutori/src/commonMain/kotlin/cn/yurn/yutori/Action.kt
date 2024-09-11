@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurn.yutori

import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.message

abstract class Actions
abstract class Action(
    val platform: String?, val self_id: String?, val resource: String, val service: ActionService
) {
    protected suspend inline fun <reified T> send(
        method: String, vararg content: Pair<String, Any?>
    ): T = service.send(resource, method, platform, self_id, mapOf(*content)) as T

    protected suspend fun upload(method: String, content: List<FormData>): Map<String, String> =
        service.upload(resource, method, platform!!, self_id!!, content)
}

class RootActions(
    val platform: String,
    val self_id: String,
    val service: ActionService,
    val yutori: Yutori
) : Actions() {
    val channel = ChannelAction(platform, self_id, service)
    val guild = GuildAction(platform, self_id, service)
    val login = LoginAction(platform, self_id, service)
    val message = MessageAction(yutori, platform, self_id, service)
    val reaction = ReactionAction(platform, self_id, service)
    val user = UserAction(platform, self_id, service)
    val friend = FriendAction(platform, self_id, service)
    val upload = UploadAction(platform, self_id, service)
    val admin = AdminAction(service)
    val containers = mutableMapOf<String, Actions>().apply {
        for ((key, value) in yutori.actions_containers) this[key] =
            value(platform, self_id, service)
    }

    class ChannelAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "channel", service
    ) {
        suspend fun get(
            channel_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Channel = send("get", "channel_id" to channel_id, *contents)

        suspend fun list(
            guild_id: String, next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<Channel> = send("list", "guild_id" to guild_id, "next" to next, *contents)

        suspend fun create(
            guild_id: String, data: Channel, vararg contents: Pair<String, Any> = arrayOf()
        ): Channel = send("create", "guild_id" to guild_id, "data" to data, *contents)

        suspend fun update(
            channel_id: String, data: Channel, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("update", "channel_id" to channel_id, "data" to data, *contents)

        suspend fun delete(
            channel_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("delete", "channel_id" to channel_id, *contents)

        suspend fun mute(
            channel_id: String, duration: Number, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("mute", "channel_id" to channel_id, "duration" to duration, *contents)
    }

    class GuildAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "guild", service
    ) {
        val member = MemberAction(platform, self_id, service)
        val role = RoleAction(platform, self_id, service)

        suspend fun get(
            guild_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Guild = send("get", "guild_id" to guild_id, *contents)

        suspend fun list(
            next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<Guild> = send("list", "next" to next, *contents)

        suspend fun approve(
            message_id: String,
            approve: Boolean,
            comment: String,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "approve",
            "message_id" to message_id,
            "approve" to approve,
            "comment" to comment,
            *contents
        )

        class MemberAction(platform: String, self_id: String, service: ActionService) : Action(
            platform, self_id, "guild.member", service
        ) {
            val role = RoleAction(platform, self_id, service)

            suspend fun get(
                guild_id: String, user_id: String, vararg contents: Pair<String, Any> = arrayOf()
            ): GuildMember = send("get", "guild_id" to guild_id, "user_id" to user_id, *contents)

            suspend fun list(
                guild_id: String,
                next: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): PagingList<GuildMember> =
                send("list", "guild_id" to guild_id, "next" to next, *contents)

            suspend fun kick(
                guild_id: String,
                user_id: String,
                permanent: Boolean? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "kick",
                "guild_id" to guild_id,
                "user_id" to user_id,
                "permanent" to permanent,
                *contents
            )

            suspend fun mute(
                guild_id: String,
                user_id: String,
                duration: Number,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "mute",
                "guild_id" to guild_id,
                "user_id" to user_id,
                "duration" to duration,
                *contents
            )

            suspend fun approve(
                message_id: String,
                approve: Boolean,
                comment: String,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "approve",
                "message_id" to message_id,
                "approve" to approve,
                "comment" to comment,
                *contents
            )

            class RoleAction(platform: String, self_id: String, service: ActionService) : Action(
                platform, self_id, "guild.member.role", service
            ) {
                suspend fun set(
                    guild_id: String,
                    user_id: String,
                    role_id: String,
                    vararg contents: Pair<String, Any> = arrayOf()
                ): Unit = send(
                    "set",
                    "guild_id" to guild_id,
                    "user_id" to user_id,
                    "role_id" to role_id,
                    *contents
                )

                suspend fun unset(
                    guild_id: String,
                    user_id: String,
                    role_id: String,
                    vararg contents: Pair<String, Any> = arrayOf()
                ): Unit = send(
                    "unset",
                    "guild_id" to guild_id,
                    "user_id" to user_id,
                    "role_id" to role_id,
                    *contents
                )
            }
        }

        class RoleAction(platform: String, self_id: String, service: ActionService) : Action(
            platform, self_id, "guild.role", service
        ) {
            suspend fun list(
                guild_id: String,
                next: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): PagingList<GuildRole> =
                send("list", "guild_id" to guild_id, "next" to next, *contents)

            suspend fun create(
                guild_id: String, role: GuildRole, vararg contents: Pair<String, Any> = arrayOf()
            ): GuildRole = send("create", "guild_id" to guild_id, "role" to role, *contents)

            suspend fun update(
                guild_id: String,
                role_id: String,
                role: GuildRole,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send(
                "update", "guild_id" to guild_id, "role_id" to role_id, "role" to role, *contents
            )

            suspend fun delete(
                guild_id: String, role_id: String, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("delete", "guild_id" to guild_id, "role_id" to role_id, *contents)
        }
    }

    class LoginAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "login", service
    ) {
        suspend fun get(
            vararg contents: Pair<String, Any> = arrayOf()
        ): Login = send("get", *contents)
    }

    class MessageAction(
        private val yutori: Yutori, platform: String, self_id: String, service: ActionService
    ) : Action(
        platform, self_id, "message", service
    ) {
        suspend fun create(
            channel_id: String,
            content: List<MessageElement>,
            vararg contents: Pair<String, Any> = arrayOf()
        ): List<Message> =
            send("create", "channel_id" to channel_id, "content" to content, *contents)

        suspend fun create(
            channel_id: String,
            content: MessageBuilder.() -> Unit,
            vararg contents: Pair<String, Any> = arrayOf()
        ): List<Message> = create(channel_id, message(yutori, content), *contents)

        suspend fun get(
            channel_id: String, message_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Message = send("get", "channel_id" to channel_id, "message_id" to message_id, *contents)

        suspend fun delete(
            channel_id: String, message_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send("delete", "channel_id" to channel_id, "message_id" to message_id, *contents)

        suspend fun update(
            channel_id: String,
            message_id: String,
            content: List<MessageElement>,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "update",
            "channel_id" to channel_id,
            "message_id" to message_id,
            "content" to content,
            *contents
        )

        suspend fun update(
            channel_id: String,
            message_id: String,
            content: MessageBuilder.() -> Unit,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = update(channel_id, message_id, message(yutori, content), *contents)

        suspend fun list(
            channel_id: String,
            next: String? = null,
            direction: BidiPagingList.Direction? = null,
            limit: Number? = null,
            order: BidiPagingList.Order? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): BidiPagingList<Message> = send(
            "list",
            "channel_id" to channel_id,
            "next" to next,
            "direction" to direction?.value,
            "limit" to limit,
            "order" to order?.value,
            *contents
        )
    }

    class ReactionAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "reaction", service
    ) {
        suspend fun create(
            channel_id: String,
            message_id: String,
            emoji: String,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "create",
            "channel_id" to channel_id,
            "message_id" to message_id,
            "emoji" to emoji,
            *contents
        )

        suspend fun delete(
            channel_id: String,
            message_id: String,
            emoji: String,
            user_id: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "delete",
            "channel_id" to channel_id,
            "message_id" to message_id,
            "emoji" to emoji,
            "user_id" to user_id,
            *contents
        )

        suspend fun clear(
            channel_id: String,
            message_id: String,
            emoji: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "clear",
            "channel_id" to channel_id,
            "message_id" to message_id,
            "emoji" to emoji,
            *contents
        )

        suspend fun list(
            channel_id: String,
            message_id: String,
            emoji: String,
            next: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<User> = send(
            "list",
            "channel_id" to channel_id,
            "message_id" to message_id,
            "emoji" to emoji,
            "next" to next,
            *contents
        )
    }

    class UserAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "user", service
    ) {
        val channel = ChannelAction(platform, self_id, service)

        suspend fun get(
            user_id: String, vararg contents: Pair<String, Any> = arrayOf()
        ): User = send("get", "user_id" to user_id, *contents)

        class ChannelAction(platform: String, self_id: String, service: ActionService) : Action(
            platform, self_id, "user.channel", service
        ) {
            suspend fun create(
                user_id: String,
                guild_id: String? = null,
                vararg contents: Pair<String, Any> = arrayOf()
            ): Channel = send("create", "user_id" to user_id, "guild_id" to guild_id, *contents)
        }
    }

    class FriendAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "friend", service
    ) {
        suspend fun list(
            next: String? = null, vararg contents: Pair<String, Any> = arrayOf()
        ): PagingList<User> = send("list", "next" to next, *contents)

        suspend fun approve(
            message_id: String,
            approve: Boolean,
            comment: String? = null,
            vararg contents: Pair<String, Any> = arrayOf()
        ): Unit = send(
            "approve",
            "message_id" to message_id,
            "approve" to approve,
            "comment" to comment,
            *contents
        )
    }

    class UploadAction(platform: String, self_id: String, service: ActionService) : Action(
        platform, self_id, "upload", service
    ) {
        suspend fun create(
            vararg contents: FormData
        ): Map<String, String> = upload("create", contents.toList())
    }

    class AdminAction(service: ActionService) {
        val login = LoginAction(service)
        val webhook = WebhookAction(service)

        class LoginAction(service: ActionService) : Action(null, null, "login", service) {
            suspend fun list(
                vararg contents: Pair<String, Any> = arrayOf()
            ): List<Login> = send("list", *contents)
        }

        class WebhookAction(service: ActionService) : Action(null, null, "webhook", service) {
            suspend fun create(
                url: String, token: String? = null, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("list", "url" to url, "token" to token, *contents)

            suspend fun delete(
                url: String, vararg contents: Pair<String, Any> = arrayOf()
            ): Unit = send("approve", "url" to url, *contents)
        }
    }
}