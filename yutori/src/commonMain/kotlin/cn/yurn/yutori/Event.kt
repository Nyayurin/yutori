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
    @JvmName("nullableArgv") get() = nullableArgv

val <T> Event<T>.button: Interaction.Button? where T : SigningEvent, T : ButtonNullableEvent
    @JvmName("nullableButton") get() = nullableButton

val <T> Event<T>.channel: Channel? where T : SigningEvent, T : ChannelNullableEvent
    @JvmName("nullableChannel") get() = nullableChannel

val <T> Event<T>.guild: Guild? where T : SigningEvent, T : GuildNullableEvent
    @JvmName("nullableGuild") get() = nullableGuild

val <T> Event<T>.login: Login? where T : SigningEvent, T : LoginNullableEvent
    @JvmName("nullableLogin") get() = nullableLogin

val <T> Event<T>.member: GuildMember? where T : SigningEvent, T : MemberNullableEvent
    @JvmName("nullableMember") get() = nullableMember

val <T> Event<T>.message: Message? where T : SigningEvent, T : MessageNullableEvent
    @JvmName("nullableMessage") get() = nullableMessage

val <T> Event<T>.operator: User? where T : SigningEvent, T : OperatorNullableEvent
    @JvmName("nullableOperator") get() = nullableOperator

val <T> Event<T>.role: GuildRole? where T : SigningEvent, T : RoleNullableEvent
    @JvmName("nullableRole") get() = nullableRole

val <T> Event<T>.user: User? where T : SigningEvent, T : UserNullableEvent
    @JvmName("nullableUser") get() = nullableUser

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

object GuildEvents {
    const val ADDED = "guild-added"
    const val UPDATED = "guild-updated"
    const val REMOVED = "guild-removed"
    const val REQUEST = "guild-request"
    val Types = setOf(ADDED, UPDATED, REMOVED, REQUEST)
}

object GuildMemberEvents {
    const val ADDED = "guild-member-added"
    const val UPDATED = "guild-member-updated"
    const val REMOVED = "guild-member-removed"
    const val REQUEST = "guild-member-request"
    val Types = setOf(ADDED, UPDATED, REMOVED, REQUEST)
}

object GuildRoleEvents {
    const val CREATED = "guild-role-created"
    const val UPDATED = "guild-role-updated"
    const val DELETED = "guild-role-deleted"
    val Types = setOf(CREATED, UPDATED, DELETED)
}

object InteractionEvents {
    const val BUTTON = "interaction/button"
    const val COMMAND = "interaction/command"
    val Types = setOf(BUTTON, COMMAND)
}

object LoginEvents {
    const val ADDED = "login-added"
    const val REMOVED = "login-removed"
    const val UPDATED = "login-updated"
    val Types = setOf(ADDED, REMOVED, UPDATED)
}

object MessageEvents {
    const val CREATED = "message-created"
    const val UPDATED = "message-updated"
    const val DELETED = "message-deleted"
    val Types = setOf(CREATED, UPDATED, DELETED)
}

object ReactionEvents {
    const val ADDED = "reaction-added"
    const val REMOVED = "reaction-removed"
    val Types = setOf(ADDED, REMOVED)
}

object UserEvents {
    const val FRIEND_REQUEST = "friend-request"
    val Types = setOf(FRIEND_REQUEST)
}

object GuildEvent : SigningEvent(), GuildNotNullEvent
object GuildMemberEvent : SigningEvent(), GuildNotNullEvent, MemberNotNullEvent, UserNotNullEvent
object GuildRoleEvent : SigningEvent(), GuildNotNullEvent, RoleNotNullEvent
object InteractionButtonEvent : SigningEvent(), ButtonNotNullEvent
object InteractionCommandEvent : SigningEvent()
object LoginEvent : SigningEvent(), LoginNotNullEvent
object MessageEvent : SigningEvent(), ChannelNotNullEvent, MessageNotNullEvent, UserNotNullEvent
object ReactionEvent : SigningEvent()
object UserEvent : SigningEvent(), UserNotNullEvent