@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurn.yutori

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * 频道
 * @property id 频道 ID
 * @property type 频道类型
 * @property name 频道名称
 * @property parent_id 父频道 ID
 */
@Serializable
data class Channel(
    val id: String,
    val type: Type,
    val name: String? = null,
    val parent_id: String? = null
) {
    @Serializable(TypeSerializer::class)
    enum class Type(val number: Number) {
        TEXT(0), DIRECT(1), CATEGORY(2), VOICE(3);

        override fun toString() = number.toString()
    }
}

/**
 * 群组
 * @property id 群组 ID
 * @property name 群组名称
 * @property avatar 群组头像
 */
@Serializable
data class Guild(val id: String, val name: String? = null, val avatar: String? = null)

/**
 * 群组成员
 * @property user 用户对象
 * @property nick 用户在群组中的名称
 * @property avatar 用户在群组中的头像
 * @property joined_at 加入时间
 */
@Serializable
data class GuildMember(
    val user: User? = null,
    val nick: String? = null,
    val avatar: String? = null,
    @Serializable(NumberNullableSerializer::class)
    val joined_at: Number? = null
)

/**
 * 群组角色
 * @property id 角色 ID
 * @property name 角色名称
 */
@Serializable
data class GuildRole(val id: String, val name: String? = null)

/**
 * 交互
 */
@Serializable
sealed class Interaction {
    /**
     * Argv
     * @property name 指令名称
     * @property arguments 参数
     * @property options 选项
     */
    @Serializable
    data class Argv @OptIn(ExperimentalSerializationApi::class) constructor(
        val name: String,
        val arguments: List<@Serializable(DynamicLookupSerializer::class) Any>,
        @Serializable(DynamicLookupSerializer::class)
        val options: Any
    ) : Interaction()

    /**
     * Button
     * @property id 按钮 ID
     */
    @Serializable
    data class Button(val id: String) : Interaction()
}

/**
 * 登录信息
 * @property user 用户对象
 * @property self_id 平台账号
 * @property platform 平台名称
 * @property status 登录状态
 */
@Serializable
data class Login(
    val user: User? = null,
    val self_id: String? = null,
    val platform: String? = null,
    val status: Status,
    val features: List<String> = listOf(),
    val proxy_urls: List<String> = listOf(),
) {
    @Serializable(StatusSerializer::class)
    enum class Status(val number: Number) {
        OFFLINE(0), ONLINE(1), CONNECT(2), DISCONNECT(3), RECONNECT(4);

        override fun toString() = number.toString()
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
 * @property created_at 消息发送的时间戳
 * @property updated_at 消息修改的时间戳
 */
@Serializable
data class Message(
    val id: String,
    val content: String,
    val channel: Channel? = null,
    val guild: Guild? = null,
    val member: GuildMember? = null,
    val user: User? = null,
    @Serializable(NumberNullableSerializer::class)
    val created_at: Number? = null,
    @Serializable(NumberNullableSerializer::class)
    val updated_at: Number? = null
)

/**
 * 用户
 * @property id 用户 ID
 * @property name 用户名称
 * @property nick 用户昵称
 * @property avatar 用户头像
 * @property is_bot 是否为机器人
 */
@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val is_bot: Boolean? = null
)

@Serializable(SignalSerializer::class)
sealed class Signal {
    @Serializable(NumberSerializer::class)
    abstract val op: Number

    @Serializable
    sealed class Body
    companion object {
        const val EVENT = 0
        const val PING = 1
        const val PONG = 2
        const val IDENTIFY = 3
        const val READY = 4
    }
}

@Serializable
data class EventSignal(val body: Event<SigningEvent>) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = EVENT
    }
}

@Serializable
class PingSignal : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = PING
    }
}

@Serializable
class PongSignal : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = PONG
    }
}

@Serializable
data class IdentifySignal(val body: Identify) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = IDENTIFY
    }
}

@Serializable
data class ReadySignal(val body: Ready) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = READY
    }
}

@Serializable
data class Identify(
    val token: String? = null,
    @Serializable(NumberNullableSerializer::class)
    val sequence: Number? = null
) : Signal.Body()

@Serializable
data class Ready(val logins: List<Login>) : Signal.Body()

/**
 * 分页列表
 * @param T 数据类型
 * @property data 数据
 * @property next 下一页的令牌
 */
@Serializable
data class PagingList<T>(val data: List<T>, val next: String? = null)

/**
 * 双向分页列表
 * @param T 数据类型
 * @property data 数据
 * @property next 下一页的令牌
 */
@Serializable
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

/**
 * Satori Server 配置
 * @property host Satori Server 主机
 * @property port Satori Server 端口
 * @property path Satori Server 路径
 * @property token Satori Server 鉴权令牌
 * @property version Satori Server 协议版本
 */
data class SatoriProperties(
    val host: String = "127.0.0.1",
    val port: Int = 5500,
    val path: String = "",
    val token: String? = null,
    val version: String = "v1"
)

data class Context<T : SigningEvent>(
    val actions: RootActions,
    val event: Event<T>,
    val satori: Satori
)

@Serializable(EventSerializer::class)
class Event<T : SigningEvent> @OptIn(ExperimentalSerializationApi::class) constructor(
    val properties: Map<String, @Serializable(DynamicLookupSerializer::class) Any?> = mapOf()
) : Signal.Body() {
    val id: Number by properties
    val type: String by properties
    val platform: String by properties
    val self_id: String by properties
    val timestamp: Number by properties
    val nullable_argv: Interaction.Argv?
        get() = properties["argv"] as Interaction.Argv?
    val nullable_button: Interaction.Button?
        get() = properties["button"] as Interaction.Button?
    val nullable_channel: Channel?
        get() = properties["channel"] as Channel?
    val nullable_guild: Guild?
        get() = properties["guild"] as Guild?
    val nullable_login: Login?
        get() = properties["login"] as Login?
    val nullable_member: GuildMember?
        get() = properties["member"] as GuildMember?
    val nullable_message: Message?
        get() = properties["message"] as Message?
    val nullable_operator: User?
        get() = properties["operator"] as User?
    val nullable_role: GuildRole?
        get() = properties["role"] as GuildRole?
    val nullable_user: User?
        get() = properties["user"] as User?

    constructor(
        id: Number,
        type: String,
        platform: String,
        self_id: String,
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
            "self_id" to self_id,
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