@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

import cn.yurn.yutori.message.element.MessageElement

data class Channel(
    val id: String,
    val type: Number,
    val name: String? = null,
    val parentId: String? = null
) {
    object Type {
        const val TEXT = 0
        const val DIRECT = 1
        const val CATEGORY = 2
        const val VOICE = 3
    }
}

data class Guild(
    val id: String,
    val name: String? = null,
    val avatar: String? = null
)

data class GuildMember(
    val user: User? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val joinedAt: Number? = null
)

data class GuildRole(
    val id: String,
    val name: String? = null
)

sealed class Interaction {
    data class Argv(
        val name: String,
        val arguments: List<Any>,
        val options: Any
    ) : Interaction()

    data class Button(val id: String) : Interaction()
}

data class Login(
    val adapter: String,
    val platform: String? = null,
    val user: User? = null,
    val status: Number? = null,
    val features: List<String> = listOf(),
    val proxyUrls: List<String> = listOf(),
) {
    object LoginStatus {
        const val OFFLINE = 0
        const val ONLINE = 1
        const val CONNECT = 2
        const val DISCONNECT = 3
        const val RECONNECT = 4
    }
}

data class Message(
    val id: String,
    val content: List<MessageElement>,
    val channel: Channel? = null,
    val guild: Guild? = null,
    val member: GuildMember? = null,
    val user: User? = null,
    val createdAt: Number? = null,
    val updatedAt: Number? = null
)

data class User(
    val id: String,
    val name: String? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val isBot: Boolean? = null
)

data class PagingList<T>(val data: List<T>, val next: String? = null)

data class BidiPagingList<T>(
    val data: List<T>,
    val prev: String? = null,
    val next: String? = null
) {
    enum class Direction(val value: String) {
        Before("before"), After("after"), Around("around");

        override fun toString() = value
    }

    enum class Order(val value: String) {
        Asc("asc"), Desc("desc");

        override fun toString() = value
    }
}

class FormData(
    val name: String,
    val filename: String? = null,
    val type: String,
    val content: ByteArray
)

data class AdapterContext<T : SigningEvent>(
    val actions: RootActions,
    val event: Event<T>,
    val yutori: Yutori
)

data class ServerContext<T : SigningRequest>(
    val actionsList: List<RootActions>,
    val request: Request<T>,
    val response: Response,
    val yutori: Yutori
)

class Event<T : SigningEvent>(val properties: Map<String, Any?> = mapOf()) {
    val id: Number by properties
    val type: String by properties
    val platform: String by properties
    val selfId: String by properties
    val timestamp: Number by properties
    val nullableArgv: Interaction.Argv?
        get() = properties["argv"] as Interaction.Argv?
    val nullableButton: Interaction.Button?
        get() = properties["button"] as Interaction.Button?
    val nullableChannel: Channel?
        get() = properties["channel"] as Channel?
    val nullableGuild: Guild?
        get() = properties["guild"] as Guild?
    val nullableLogin: Login?
        get() = properties["login"] as Login?
    val nullableMember: GuildMember?
        get() = properties["member"] as GuildMember?
    val nullableMessage: Message?
        get() = properties["message"] as Message?
    val nullableOperator: User?
        get() = properties["operator"] as User?
    val nullableRole: GuildRole?
        get() = properties["role"] as GuildRole?
    val nullableUser: User?
        get() = properties["user"] as User?

    constructor(
        id: Number,
        type: String,
        platform: String,
        selfId: String,
        timestamp: Number,
        argv: Interaction.Argv? = null,
        button: Interaction.Button? = null,
        channel: Channel? = null,
        guild: Guild? = null,
        login: Login? = null,
        member: GuildMember? = null,
        message: Message? = null,
        operator: User? = null,
        role: GuildRole? = null,
        user: User? = null,
        vararg pair: Pair<String, Any?> = arrayOf(),
    ) : this(
        mapOf(
            "id" to id,
            "type" to type,
            "platform" to platform,
            "selfId" to selfId,
            "timestamp" to timestamp,
            "argv" to argv,
            "button" to button,
            "channel" to channel,
            "guild" to guild,
            "login" to login,
            "member" to member,
            "message" to message,
            "operator" to operator,
            "role" to role,
            "user" to user,
            *pair
        )
    )
}

class Request<T : SigningRequest>(
    val api: String,
    val properties: Map<String, Any?> = mapOf()
)

class Response(private val onRespond: suspend (String) -> Unit) {
    suspend fun respond(content: String) = onRespond(content)
}