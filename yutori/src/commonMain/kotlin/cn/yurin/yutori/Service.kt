@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori

import cn.yurin.yutori.message.element.MessageElement

abstract class AdapterActionService {
	abstract suspend fun channelGet(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		contents: Array<out Pair<String, Any>>,
	): Channel

	abstract suspend fun channelList(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<Channel>

	abstract suspend fun channelCreate(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		data: Channel,
		contents: Array<out Pair<String, Any>>,
	): Channel

	abstract suspend fun channelUpdate(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		data: Channel,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun channelDelete(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun channelMute(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		duration: Number,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildGet(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		contents: Array<out Pair<String, Any>>,
	): Guild

	abstract suspend fun guildList(
		headerPlatform: String,
		headerUserId: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<Guild>

	abstract suspend fun guildApprove(
		headerPlatform: String,
		headerUserId: String,
		messageId: String,
		approve: Boolean,
		comment: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildMemberGet(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		userId: String,
		contents: Array<out Pair<String, Any>>,
	): GuildMember

	abstract suspend fun guildMemberList(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<GuildMember>

	abstract suspend fun guildMemberKick(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		userId: String,
		permanent: Boolean?,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildMemberMute(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		userId: String,
		duration: Number,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildMemberApprove(
		headerPlatform: String,
		headerUserId: String,
		messageId: String,
		approve: Boolean,
		comment: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildMemberRoleSet(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		userId: String,
		roleId: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildMemberRoleUnset(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		userId: String,
		roleId: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildRoleList(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<GuildRole>

	abstract suspend fun guildRoleCreate(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		role: GuildRole,
		contents: Array<out Pair<String, Any>>,
	): GuildRole

	abstract suspend fun guildRoleUpdate(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		roleId: String,
		role: GuildRole,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun guildRoleDelete(
		headerPlatform: String,
		headerUserId: String,
		guildId: String,
		roleId: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun loginGet(
		headerPlatform: String,
		headerUserId: String,
		contents: Array<out Pair<String, Any>>,
	): Login

	abstract suspend fun messageCreate(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		content: List<MessageElement>,
		contents: Array<out Pair<String, Any>>,
	): List<Message>

	abstract suspend fun messageGet(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		contents: Array<out Pair<String, Any>>,
	): Message

	abstract suspend fun messageDelete(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun messageUpdate(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		content: List<MessageElement>,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun messageList(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		next: String?,
		direction: BidiPagingList.Direction?,
		limit: Number?,
		order: BidiPagingList.Order?,
		contents: Array<out Pair<String, Any>>,
	): BidiPagingList<Message>

	abstract suspend fun reactionCreate(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		emoji: String,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun reactionDelete(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		emoji: String,
		userId: String?,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun reactionClear(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		emoji: String?,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun reactionList(
		headerPlatform: String,
		headerUserId: String,
		channelId: String,
		messageId: String,
		emoji: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<User>

	abstract suspend fun userGet(
		headerPlatform: String,
		headerUserId: String,
		userId: String,
		contents: Array<out Pair<String, Any>>,
	): User

	abstract suspend fun userChannelCreate(
		headerPlatform: String,
		headerUserId: String,
		userId: String,
		guildId: String?,
		contents: Array<out Pair<String, Any>>,
	): Channel

	abstract suspend fun friendList(
		headerPlatform: String,
		headerUserId: String,
		next: String?,
		contents: Array<out Pair<String, Any>>,
	): PagingList<User>

	abstract suspend fun friendApprove(
		headerPlatform: String,
		headerUserId: String,
		messageId: String,
		approve: Boolean,
		comment: String?,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun uploadCreate(
		headerPlatform: String,
		headerUserId: String,
		contents: Array<out FormData>,
	): Map<String, String>

	abstract suspend fun adminLoginList(
		contents: Array<out Pair<String, Any>>
	): List<Login>

	abstract suspend fun adminWebhookCreate(
		url: String,
		token: String?,
		contents: Array<out Pair<String, Any>>,
	)

	abstract suspend fun adminWebhookDelete(
		url: String,
		contents: Array<out Pair<String, Any>>,
	)
}

abstract class AdapterEventService(
	val alias: String?,
) {
	abstract suspend fun connect()

	abstract fun disconnect()
}

abstract class ServerService(
	val alias: String?,
) {
	abstract suspend fun start()

	abstract suspend fun pushEvent(event: Event<SigningEvent>)

	abstract fun stop()
}