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
    @JvmName("nullable_argv") get() = nullableArgv

val <T> Event<T>.button: Interaction.Button? where T : SigningEvent, T : ButtonNullableEvent
    @JvmName("nullable_button") get() = nullableButton

val <T> Event<T>.channel: Channel? where T : SigningEvent, T : ChannelNullableEvent
    @JvmName("nullable_channel") get() = nullableChannel

val <T> Event<T>.guild: Guild? where T : SigningEvent, T : GuildNullableEvent
    @JvmName("nullable_guild") get() = nullableGuild

val <T> Event<T>.login: Login? where T : SigningEvent, T : LoginNullableEvent
    @JvmName("nullable_login") get() = nullableLogin

val <T> Event<T>.member: GuildMember? where T : SigningEvent, T : MemberNullableEvent
    @JvmName("nullable_member") get() = nullableMember

val <T> Event<T>.message: Message? where T : SigningEvent, T : MessageNullableEvent
    @JvmName("nullable_message") get() = nullableMessage

val <T> Event<T>.operator: User? where T : SigningEvent, T : OperatorNullableEvent
    @JvmName("nullable_operator") get() = nullableOperator

val <T> Event<T>.role: GuildRole? where T : SigningEvent, T : RoleNullableEvent
    @JvmName("nullable_role") get() = nullableRole

val <T> Event<T>.user: User? where T : SigningEvent, T : UserNullableEvent
    @JvmName("nullable_user") get() = nullableUser

val <T> Event<T>.argv: Interaction.Argv where T : SigningEvent, T : ArgvNotNullEvent
    get() = nullableArgv!!

val <T> Event<T>.button: Interaction.Button where T : SigningEvent, T : ButtonNotNullEvent
    get() = nullableButton!!

val <T> Event<T>.channel: Channel where T : SigningEvent, T : ChannelNotNullEvent
    get() = nullableChannel!!

val <T> Event<T>.guild: Guild where T : SigningEvent, T : GuildNotNullEvent
    get() = nullableGuild!!

val <T> Event<T>.login: Login where T : SigningEvent, T : LoginNotNullEvent
    get() = nullableLogin!!

val <T> Event<T>.member: GuildMember where T : SigningEvent, T : MemberNotNullEvent
    get() = nullableMember!!

val <T> Event<T>.message: Message where T : SigningEvent, T : MessageNotNullEvent
    get() = nullableMessage!!

val <T> Event<T>.operator: User where T : SigningEvent, T : OperatorNotNullEvent
    get() = nullableOperator!!

val <T> Event<T>.role: GuildRole where T : SigningEvent, T : RoleNotNullEvent
    get() = nullableRole!!

val <T> Event<T>.user: User where T : SigningEvent, T : UserNotNullEvent
    get() = nullableUser!!

/**
 * 群组事件列表
 */
object GuildEvents {
    const val ADDED = "guild-added"
    const val UPDATED = "guild-updated"
    const val REMOVED = "guild-removed"
    const val REQUEST = "guild-request"
    val Types = setOf(ADDED, UPDATED, REMOVED, REQUEST)
}

/**
 * 群组事件实体类
 */
class GuildEvent : SigningEvent(), GuildNotNullEvent

/**
 * 群组成员事件列表
 */
object GuildMemberEvents {
    const val ADDED = "guild-member-added"
    const val UPDATED = "guild-member-updated"
    const val REMOVED = "guild-member-removed"
    const val REQUEST = "guild-member-request"
    val Types = setOf(ADDED, UPDATED, REMOVED, REQUEST)
}

/**
 * 群组成员事件实体类
 */
class GuildMemberEvent : SigningEvent(), GuildNotNullEvent, MemberNotNullEvent, UserNotNullEvent

/**
 * 群组角色事件列表
 */
object GuildRoleEvents {
    const val CREATED = "guild-role-created"
    const val UPDATED = "guild-role-updated"
    const val DELETED = "guild-role-deleted"
    val Types = setOf(CREATED, UPDATED, DELETED)
}

/**
 * 群组角色事件实体类
 */
class GuildRoleEvent : SigningEvent(), GuildNotNullEvent, RoleNotNullEvent

/**
 * 交互事件列表
 */
object InteractionEvents {
    const val BUTTON = "interaction/button"
    const val COMMAND = "interaction/command"
    val Types = setOf(BUTTON, COMMAND)
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
    const val ADDED = "login-added"
    const val REMOVED = "login-removed"
    const val UPDATED = "login-updated"
    val Types = setOf(ADDED, REMOVED, UPDATED)
}

/**
 * 登录事件实体类
 */
class LoginEvent : SigningEvent(), LoginNotNullEvent

/**
 * 消息事件列表
 */
object MessageEvents {
    const val CREATED = "message-created"
    const val UPDATED = "message-updated"
    const val DELETED = "message-deleted"
    val Types = setOf(CREATED, UPDATED, DELETED)
}

/**
 * 消息事件实体类
 */
class MessageEvent : SigningEvent(), ChannelNotNullEvent, MessageNotNullEvent, UserNotNullEvent

/**
 * 表态事件列表
 */
object ReactionEvents {
    const val ADDED = "reaction-added"
    const val REMOVED = "reaction-removed"
    val Types = setOf(ADDED, REMOVED)
}

/**
 * 表态事件实体类
 */
class ReactionEvent : SigningEvent()

/**
 * 用户事件列表
 */
object UserEvents {
    const val FRIEND_REQUEST = "friend-request"
    val Types = setOf(FRIEND_REQUEST)
}

/**
 * 用户事件实体类
 */
class UserEvent : SigningEvent(), UserNotNullEvent