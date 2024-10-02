@file:Suppress("MemberVisibilityCanBePrivate")

package cn.yurn.yutori

import cn.yurn.yutori.message.element.MessageElement

/**
 * 频道
 * @property id 频道 ID
 * @property type 频道类型
 * @property name 频道名称
 * @property parentId 父频道 ID
 */
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

/**
 * 群组
 * @property id 群组 ID
 * @property name 群组名称
 * @property avatar 群组头像
 */
data class Guild(
    val id: String,
    val name: String? = null,
    val avatar: String? = null
)

/**
 * 群组成员
 * @property user 用户对象
 * @property nick 用户在群组中的名称
 * @property avatar 用户在群组中的头像
 * @property joinedAt 加入时间
 */
data class GuildMember(
    val user: User? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val joinedAt: Number? = null
)

/**
 * 群组角色
 * @property id 角色 ID
 * @property name 角色名称
 */
data class GuildRole(
    val id: String,
    val name: String? = null
)

/**
 * 交互
 */
sealed class Interaction {
    /**
     * Argv
     * @property name 指令名称
     * @property arguments 参数
     * @property options 选项
     */
    data class Argv(
        val name: String,
        val arguments: List<Any>,
        val options: Any
    ) : Interaction()

    /**
     * Button
     * @property id 按钮 ID
     */
    data class Button(val id: String) : Interaction()
}

/**
 * 登录信息
 * @property user 用户对象
 * @property selfId 平台账号
 * @property platform 平台名称
 * @property status 登录状态
 */
data class Login(
    val user: User? = null,
    val selfId: String? = null,
    val platform: String? = null,
    val status: Number,
    val features: List<String> = listOf(),
    val proxyUrls: List<String> = listOf(),
) {
    object Status {
        const val OFFLINE = 0
        const val ONLINE = 1
        const val CONNECT = 2
        const val DISCONNECT = 3
        const val RECONNECT = 4
    }
}

/**
 * 消息
 * @property id 消息 ID
 * @property content 消息内容
 * @property channel 频道对象
 * @property guild 群组对象
 * @property member 成员对象
 * @property user 用户对象
 * @property createdAt 消息发送的时间戳
 * @property updatedAt 消息修改的时间戳
 */
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

/**
 * 用户
 * @property id 用户 ID
 * @property name 用户名称
 * @property nick 用户昵称
 * @property avatar 用户头像
 * @property isBot 是否为机器人
 */
data class User(
    val id: String,
    val name: String? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val isBot: Boolean? = null
)

/**
 * 分页列表
 * @param T 数据类型
 * @property data 数据
 * @property next 下一页的令牌
 */
data class PagingList<T>(val data: List<T>, val next: String? = null)

/**
 * 双向分页列表
 * @param T 数据类型
 * @property data 数据
 * @property next 下一页的令牌
 */
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

data class Context<T : SigningEvent>(
    val actions: RootActions,
    val event: Event<T>,
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