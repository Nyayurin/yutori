@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurin.yutori

import cn.yurin.yutori.message.element.MessageElement
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
abstract class SigningRequest

interface PlatformNotNullRequest

interface SelfIdNotNullRequest

interface ChannelIdNotNullRequest

interface GuildIdNotNullRequest

interface NextNullableRequest

interface ChannelDataNotNullRequest

interface DurationNotNullRequest

interface UserIdNotNullRequest

interface GuildIdNullableRequest

interface MessageIdNotNullRequest

interface ApproveNotNullRequest

interface CommentNotNullRequest

interface PermanentNullableRequest

interface CommentNullableRequest

interface RoleIdNotNullRequest

interface RoleNotNullRequest

interface ContentNotNullRequest

interface DirectionNullableRequest

interface LimitNullableRequest

interface OrderNullableRequest

interface EmojiNotNullRequest

interface UserIdNullableRequest

interface EmojiNullableRequest

val <T> Request<T>.platform: String where T : SigningRequest, T : PlatformNotNullRequest
    get() = header["platform"] as String

val <T> Request<T>.selfId: String where T : SigningRequest, T : SelfIdNotNullRequest
    get() = header["selfId"] as String

val <T> Request<T>.channelId: String where T : SigningRequest, T : ChannelIdNotNullRequest
    get() = body["channelId"] as String

val <T> Request<T>.guildId: String where T : SigningRequest, T : GuildIdNotNullRequest
    get() = body["guildId"] as String

val <T> Request<T>.next: String? where T : SigningRequest, T : NextNullableRequest
    get() = body["next"] as String?

val <T> Request<T>.data: Channel where T : SigningRequest, T : ChannelDataNotNullRequest
    get() = body["data"] as Channel

val <T> Request<T>.duration: Number where T : SigningRequest, T : DurationNotNullRequest
    get() = body["duration"] as Number

val <T> Request<T>.userId: String where T : SigningRequest, T : UserIdNotNullRequest
    get() = body["userId"] as String

val <T> Request<T>.guildId: String? where T : SigningRequest, T : GuildIdNullableRequest
    @JvmName("nullableGuildId")
    get() = body["guildId"] as String?

val <T> Request<T>.messageId: String where T : SigningRequest, T : MessageIdNotNullRequest
    get() = body["messageId"] as String

val <T> Request<T>.approve: Boolean where T : SigningRequest, T : ApproveNotNullRequest
    get() = body["approve"] as Boolean

val <T> Request<T>.comment: String where T : SigningRequest, T : CommentNotNullRequest
    get() = body["comment"] as String

val <T> Request<T>.permanent: Boolean? where T : SigningRequest, T : PermanentNullableRequest
    get() = body["permanent"] as Boolean?

val <T> Request<T>.comment: String? where T : SigningRequest, T : CommentNullableRequest
    @JvmName("nullableComment")
    get() = body["comment"] as String?

val <T> Request<T>.roleId: String where T : SigningRequest, T : RoleIdNotNullRequest
    get() = body["roleId"] as String

val <T> Request<T>.role: GuildRole where T : SigningRequest, T : RoleNotNullRequest
    get() = body["role"] as GuildRole

val <T> Request<T>.content: List<MessageElement> where T : SigningRequest, T : ContentNotNullRequest
    get() = body["content"] as List<MessageElement>

val <T> Request<T>.direction: BidiPagingList.Direction? where T : SigningRequest, T : DirectionNullableRequest
    get() = body["direction"] as BidiPagingList.Direction?

val <T> Request<T>.limit: Number? where T : SigningRequest, T : LimitNullableRequest
    get() = body["limit"] as Number?

val <T> Request<T>.order: BidiPagingList.Order? where T : SigningRequest, T : OrderNullableRequest
    get() = body["order"] as BidiPagingList.Order?

val <T> Request<T>.emoji: String where T : SigningRequest, T : EmojiNotNullRequest
    get() = body["emoji"] as String

val <T> Request<T>.userId: String? where T : SigningRequest, T : UserIdNullableRequest
    @JvmName("nullableUserId")
    get() = body["userId"] as String?

val <T> Request<T>.emoji: String? where T : SigningRequest, T : EmojiNullableRequest
    @JvmName("nullableEmoji")
    get() = body["emoji"] as String?

object ChannelRequests {
    const val GET = "/channel.get"
    const val LIST = "/channel.list"
    const val CREATE = "/channel.create"
    const val UPDATE = "/channel.update"
    const val DELETE = "/channel.delete"
    const val MUTE = "/channel.mute"
    val Types = setOf(GET, LIST, CREATE, UPDATE, DELETE, MUTE)
}

object UserChannelRequests {
    const val CREATE = "/user.channel.create"
    val Types = setOf(CREATE)
}

object GuildRequests {
    const val GET = "/guild.get"
    const val LIST = "/guild.list"
    const val APPROVE = "/guild.approve"
    val Types = setOf(GET, LIST, APPROVE)
}

object GuildMemberRequests {
    const val GET = "/guild.member.get"
    const val LIST = "/guild.member.list"
    const val KICK = "/guild.member.kick"
    const val MUTE = "/guild.member.mute"
    const val APPROVE = "/guild.member.approve"
    val Types = setOf(GET, LIST, KICK, MUTE, APPROVE)
}

object GuildMemberRoleRequests {
    const val SET = "/guild.member.role.set"
    const val UNSET = "/guild.member.role.unset"
    val Types = setOf(SET, UNSET)
}

object GuildRoleRequests {
    const val LIST = "/guild.role.list"
    const val CREATE = "/guild.role.create"
    const val UPDATE = "/guild.role.update"
    const val DELETE = "/guild.role.delete"
    val Types = setOf(LIST, CREATE, UPDATE, DELETE)
}

object LoginRequests {
    const val GET = "/login.get"
    val Types = setOf(GET)
}

object MessageRequests {
    const val CREATE = "/message.create"
    const val GET = "/message.get"
    const val DELETE = "/message.delete"
    const val UPDATE = "/message.update"
    const val LIST = "/message.list"
    val Types = setOf(CREATE, GET, DELETE, UPDATE, LIST)
}

object ReactionRequests {
    const val CREATE = "/reaction.create"
    const val DELETE = "/reaction.delete"
    const val CLEAR = "/reaction.clear"
    const val LIST = "/reaction.list"
    val Types = setOf(CREATE, DELETE, CLEAR, LIST)
}

object UserRequests {
    const val GET = "/user.get"
    val Types = setOf(GET)
}

object FriendRequests {
    const val LIST = "/friend.list"
    const val APPROVE = "/friend.approve"
    val Types = setOf(LIST, APPROVE)
}

object ChannelGetRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ChannelListRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ChannelCreateRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    ChannelDataNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ChannelUpdateRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    ChannelDataNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ChannelDeleteRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ChannelMuteRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    DurationNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object UserChannelCreateRequest :
    SigningRequest(),
    UserIdNotNullRequest,
    GuildIdNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildGetRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildListRequest :
    SigningRequest(),
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildApproveRequest :
    SigningRequest(),
    MessageIdNotNullRequest,
    ApproveNotNullRequest,
    CommentNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberGetRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    UserIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberListRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberKickRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    UserIdNotNullRequest,
    PermanentNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberMuteRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    UserIdNotNullRequest,
    DurationNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberApproveRequest :
    SigningRequest(),
    MessageIdNotNullRequest,
    ApproveNotNullRequest,
    CommentNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberRoleSetRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    UserIdNotNullRequest,
    RoleIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildMemberRoleUnsetRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    UserIdNotNullRequest,
    RoleIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildRoleListRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildRoleCreateRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    RoleNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildRoleUpdateRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    RoleIdNotNullRequest,
    RoleNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object GuildRoleDeleteRequest :
    SigningRequest(),
    GuildIdNotNullRequest,
    RoleIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object LoginGetRequest : SigningRequest(), PlatformNotNullRequest, SelfIdNotNullRequest

object MessageCreateRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    ContentNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object MessageGetRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object MessageDeleteRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object MessageUpdateRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    ContentNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object MessageListRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    NextNullableRequest,
    DirectionNullableRequest,
    LimitNullableRequest,
    OrderNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ReactionCreateRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    EmojiNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ReactionDeleteRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    EmojiNotNullRequest,
    UserIdNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ReactionClearRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    EmojiNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object ReactionListRequest :
    SigningRequest(),
    ChannelIdNotNullRequest,
    MessageIdNotNullRequest,
    EmojiNotNullRequest,
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object UserGetRequest :
    SigningRequest(),
    UserIdNotNullRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object FriendListRequest :
    SigningRequest(),
    NextNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest

object FriendApproveRequest :
    SigningRequest(),
    MessageIdNotNullRequest,
    ApproveNotNullRequest,
    CommentNullableRequest,
    PlatformNotNullRequest,
    SelfIdNotNullRequest