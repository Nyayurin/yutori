@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurn.yutori

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
abstract class SigningEvent : ArgvNullableEvent,
    ButtonNullableEvent,
    ChannelNullableEvent,
    GuildNullableEvent,
    LoginNullableEvent,
    MemberNullableEvent,
    MessageNullableEvent,
    OperatorNullableEvent,
    RoleNullableEvent,
    UserNullableEvent

interface ArgvNullableEvent
interface ButtonNullableEvent
interface ChannelNullableEvent
interface GuildNullableEvent
interface LoginNullableEvent
interface MemberNullableEvent
interface MessageNullableEvent
interface OperatorNullableEvent
interface RoleNullableEvent
interface UserNullableEvent
interface ArgvNotNullEvent
interface ButtonNotNullEvent
interface ChannelNotNullEvent
interface GuildNotNullEvent
interface LoginNotNullEvent
interface MemberNotNullEvent
interface MessageNotNullEvent
interface OperatorNotNullEvent
interface RoleNotNullEvent
interface UserNotNullEvent

val <T> Event<T>.argv: Interaction.Argv? where T : SigningEvent, T : ArgvNullableEvent
    @JvmName("nullable_argv") get() = nullable_argv

val <T> Event<T>.button: Interaction.Button? where T : SigningEvent, T : ButtonNullableEvent
    @JvmName("nullable_button") get() = nullable_button

val <T> Event<T>.channel: Channel? where T : SigningEvent, T : ChannelNullableEvent
    @JvmName("nullable_channel") get() = nullable_channel

val <T> Event<T>.guild: Guild? where T : SigningEvent, T : GuildNullableEvent
    @JvmName("nullable_guild") get() = nullable_guild

val <T> Event<T>.login: Login? where T : SigningEvent, T : LoginNullableEvent
    @JvmName("nullable_login") get() = nullable_login

val <T> Event<T>.member: GuildMember? where T : SigningEvent, T : MemberNullableEvent
    @JvmName("nullable_member") get() = nullable_member

val <T> Event<T>.message: Message? where T : SigningEvent, T : MessageNullableEvent
    @JvmName("nullable_message") get() = nullable_message

val <T> Event<T>.operator: User? where T : SigningEvent, T : OperatorNullableEvent
    @JvmName("nullable_operator") get() = nullable_operator

val <T> Event<T>.role: GuildRole? where T : SigningEvent, T : RoleNullableEvent
    @JvmName("nullable_role") get() = nullable_role

val <T> Event<T>.user: User? where T : SigningEvent, T : UserNullableEvent
    @JvmName("nullable_user") get() = nullable_user

val <T> Event<T>.argv: Interaction.Argv where T : SigningEvent, T : ArgvNotNullEvent
    get() = nullable_argv!!

val <T> Event<T>.button: Interaction.Button where T : SigningEvent, T : ButtonNotNullEvent
    get() = nullable_button!!

val <T> Event<T>.channel: Channel where T : SigningEvent, T : ChannelNotNullEvent
    get() = nullable_channel!!

val <T> Event<T>.guild: Guild where T : SigningEvent, T : GuildNotNullEvent
    get() = nullable_guild!!

val <T> Event<T>.login: Login where T : SigningEvent, T : LoginNotNullEvent
    get() = nullable_login!!

val <T> Event<T>.member: GuildMember where T : SigningEvent, T : MemberNotNullEvent
    get() = nullable_member!!

val <T> Event<T>.message: Message where T : SigningEvent, T : MessageNotNullEvent
    get() = nullable_message!!

val <T> Event<T>.operator: User where T : SigningEvent, T : OperatorNotNullEvent
    get() = nullable_operator!!

val <T> Event<T>.role: GuildRole where T : SigningEvent, T : RoleNotNullEvent
    get() = nullable_role!!

val <T> Event<T>.user: User where T : SigningEvent, T : UserNotNullEvent
    get() = nullable_user!!

/**
 * 群组事件列表
 */
object GuildEvents {
    const val Added = "guild-added"
    const val Updated = "guild-updated"
    const val Removed = "guild-removed"
    const val Request = "guild-request"
    val Types = arrayOf(Added, Updated, Removed, Request)
}

/**
 * 群组事件实体类
 */
class GuildEvent : SigningEvent(), GuildNotNullEvent

/**
 * 群组成员事件列表
 */
object GuildMemberEvents {
    const val Added = "guild-member-added"
    const val Updated = "guild-member-updated"
    const val Removed = "guild-member-removed"
    const val Request = "guild-member-request"
    val Types = arrayOf(Added, Updated, Removed, Request)
}

/**
 * 群组成员事件实体类
 */
class GuildMemberEvent : SigningEvent(), GuildNotNullEvent, MemberNotNullEvent, UserNotNullEvent

/**
 * 群组角色事件列表
 */
object GuildRoleEvents {
    const val Created = "guild-role-created"
    const val Updated = "guild-role-updated"
    const val Deleted = "guild-role-deleted"
    val Types = arrayOf(Created, Updated, Deleted)
}

/**
 * 群组角色事件实体类
 */
class GuildRoleEvent : SigningEvent(), GuildNotNullEvent, RoleNotNullEvent

/**
 * 交互事件列表
 */
object InteractionEvents {
    const val Button = "interaction/button"
    const val Command = "interaction/command"
    val Types = arrayOf(Button, Command)
}

/**
 * 交互事件 interaction/button 实体类
 */
class InteractionButtonEvent : SigningEvent(), ButtonNotNullEvent

/**
 * 交互事件 interaction/command 实体类
 */
class InteractionCommandEvent : SigningEvent()

/**
 * 登录事件列表
 */
object LoginEvents {
    const val Added = "login-added"
    const val Removed = "login-removed"
    const val Updated = "login-updated"
    val Types = arrayOf(Added, Removed, Updated)
}

/**
 * 登录事件实体类
 */
class LoginEvent : SigningEvent(), LoginNotNullEvent

/**
 * 消息事件列表
 */
object MessageEvents {
    const val Created = "message-created"
    const val Updated = "message-updated"
    const val Deleted = "message-deleted"
    val Types = arrayOf(Created, Updated, Deleted)
}

/**
 * 消息事件实体类
 */
class MessageEvent : SigningEvent(), ChannelNotNullEvent, MessageNotNullEvent, UserNotNullEvent

/**
 * 表态事件列表
 */
object ReactionEvents {
    const val Added = "reaction-added"
    const val Removed = "reaction-removed"
    val Types = arrayOf(Added, Removed)
}

/**
 * 表态事件实体类
 */
class ReactionEvent : SigningEvent()

/**
 * 用户事件列表
 */
object UserEvents {
    const val Friend_Request = "friend-request"
    val Types = arrayOf(Friend_Request)
}

/**
 * 用户事件实体类
 */
class UserEvent : SigningEvent(), UserNotNullEvent