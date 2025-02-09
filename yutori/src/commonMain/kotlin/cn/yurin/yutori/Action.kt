@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurin.yutori

import cn.yurin.yutori.message.MessageBuilder
import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.message

sealed class ActionNode

class ActionRoot(
	val alias: String?,
	val platform: String,
	val userId: String,
	val service: AdapterActionService,
	val yutori: Yutori,
) : ActionNode() {
	val channel = ChannelAction(platform, userId, service)
	val guild = GuildAction(platform, userId, service)
	val login = LoginAction(platform, userId, service)
	val message = MessageAction(yutori, platform, userId, service)
	val reaction = ReactionAction(platform, userId, service)
	val user = UserAction(platform, userId, service)
	val friend = FriendAction(platform, userId, service)
	val upload = UploadAction(platform, userId, service)
	val admin = AdminAction(service)
	val containers = yutori.actionsContainers.mapValues { (_, value) ->
		value(platform, userId, service)
	}
}

abstract class ActionBranch : ActionNode()

abstract class ActionLeaf(
	protected val service: AdapterActionService,
) : ActionNode()

class ChannelAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun get(
		channelId: String,
		vararg contents: Pair<String, Any>,
	) = service.channelGet(platform, userId, channelId, contents)

	suspend fun list(
		guildId: String,
		next: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.channelList(platform, userId, guildId, next, contents)

	suspend fun create(
		guildId: String,
		data: Channel,
		vararg contents: Pair<String, Any>,
	) = service.channelCreate(platform, userId, guildId, data, contents)

	suspend fun update(
		channelId: String,
		data: Channel,
		vararg contents: Pair<String, Any>,
	) = service.channelUpdate(platform, userId, channelId, data, contents)

	suspend fun delete(
		channelId: String,
		vararg contents: Pair<String, Any>,
	) = service.channelDelete(platform, userId, channelId, contents)

	suspend fun mute(
		channelId: String,
		duration: Number,
		vararg contents: Pair<String, Any>,
	) = service.channelMute(platform, userId, channelId, duration, contents)
}

class GuildAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	val member = MemberAction(platform, userId, service)
	val role = RoleAction(platform, userId, service)

	suspend fun get(
		guildId: String,
		vararg contents: Pair<String, Any>,
	) = service.guildGet(platform, userId, guildId, contents)

	suspend fun list(
		next: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.guildList(platform, userId, next, contents)

	suspend fun approve(
		messageId: String,
		approve: Boolean,
		comment: String,
		vararg contents: Pair<String, Any>,
	) = service.guildApprove(platform, userId, messageId, approve, comment, contents)

	class MemberAction(
		private val platform: String,
		private val userId: String,
		service: AdapterActionService,
	) : ActionLeaf(service) {
		val role = RoleAction(platform, userId, service)

		suspend fun get(
			guildId: String,
			userId: String,
			vararg contents: Pair<String, Any>,
		) = service.guildMemberGet(platform, this.userId, guildId, userId, contents)

		suspend fun list(
			guildId: String,
			next: String? = null,
			vararg contents: Pair<String, Any>,
		) = service.guildMemberList(platform, userId, guildId, next, contents)

		suspend fun kick(
			guildId: String,
			userId: String,
			permanent: Boolean? = null,
			vararg contents: Pair<String, Any>,
		) = service.guildMemberKick(platform, this.userId, guildId, userId, permanent, contents)

		suspend fun mute(
			guildId: String,
			userId: String,
			duration: Number,
			vararg contents: Pair<String, Any>,
		) = service.guildMemberMute(platform, this.userId, guildId, userId, duration, contents)

		suspend fun approve(
			messageId: String,
			approve: Boolean,
			comment: String,
			vararg contents: Pair<String, Any>,
		) = service.guildMemberApprove(platform, userId, messageId, approve, comment, contents)

		class RoleAction(
			private val platform: String,
			private val userId: String,
			service: AdapterActionService,
		) : ActionLeaf(service) {
			suspend fun set(
				guildId: String,
				userId: String,
				roleId: String,
				vararg contents: Pair<String, Any>,
			) = service.guildMemberRoleSet(platform, this.userId, guildId, userId, roleId, contents)

			suspend fun unset(
				guildId: String,
				userId: String,
				roleId: String,
				vararg contents: Pair<String, Any>,
			) = service.guildMemberRoleUnset(platform, this.userId, guildId, userId, roleId, contents)
		}
	}

	class RoleAction(
		private val platform: String,
		private val userId: String,
		service: AdapterActionService,
	) : ActionLeaf(service) {
		suspend fun list(
			guildId: String,
			next: String? = null,
			vararg contents: Pair<String, Any>,
		) = service.guildRoleList(platform, userId, guildId, next, contents)

		suspend fun create(
			guildId: String,
			role: GuildRole,
			vararg contents: Pair<String, Any>,
		) = service.guildRoleCreate(platform, userId, guildId, role, contents)

		suspend fun update(
			guildId: String,
			roleId: String,
			role: GuildRole,
			vararg contents: Pair<String, Any>,
		) = service.guildRoleUpdate(platform, userId, guildId, roleId, role, contents)

		suspend fun delete(
			guildId: String,
			roleId: String,
			vararg contents: Pair<String, Any>,
		) = service.guildRoleDelete(platform, userId, guildId, roleId, contents)
	}
}

class LoginAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun get(vararg contents: Pair<String, Any>) = service.loginGet(platform, userId, contents)
}

class MessageAction(
	private val yutori: Yutori,
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun create(
		channelId: String,
		content: List<MessageElement>,
		vararg contents: Pair<String, Any>,
	) = service.messageCreate(platform, userId, channelId, content, contents)

	suspend fun create(
		channelId: String,
		content: MessageBuilder.() -> Unit,
		vararg contents: Pair<String, Any>,
	) = service.messageCreate(platform, userId, channelId, message(yutori, content), contents)

	suspend fun get(
		channelId: String,
		messageId: String,
		vararg contents: Pair<String, Any>,
	) = service.messageGet(platform, userId, channelId, messageId, contents)

	suspend fun delete(
		channelId: String,
		messageId: String,
		vararg contents: Pair<String, Any>,
	) = service.messageDelete(platform, userId, channelId, messageId, contents)

	suspend fun update(
		channelId: String,
		messageId: String,
		content: List<MessageElement>,
		vararg contents: Pair<String, Any>,
	) = service.messageUpdate(platform, userId, channelId, messageId, content, contents)

	suspend fun update(
		channelId: String,
		messageId: String,
		content: MessageBuilder.() -> Unit,
		vararg contents: Pair<String, Any>,
	) = service.messageUpdate(platform, userId, channelId, messageId, message(yutori, content), contents)

	suspend fun list(
		channelId: String,
		next: String? = null,
		direction: BidiPagingList.Direction? = null,
		limit: Number? = null,
		order: BidiPagingList.Order? = null,
		vararg contents: Pair<String, Any>,
	) = service.messageList(platform, userId, channelId, next, direction, limit, order, contents)
}

class ReactionAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun create(
		channelId: String,
		messageId: String,
		emoji: String,
		vararg contents: Pair<String, Any>,
	) = service.reactionCreate(platform, userId, channelId, messageId, emoji, contents)

	suspend fun delete(
		channelId: String,
		messageId: String,
		emoji: String,
		userId: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.reactionDelete(platform, this.userId, channelId, messageId, emoji, userId, contents)

	suspend fun clear(
		channelId: String,
		messageId: String,
		emoji: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.reactionClear(platform, userId, channelId, messageId, emoji, contents)

	suspend fun list(
		channelId: String,
		messageId: String,
		emoji: String,
		next: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.reactionList(platform, userId, channelId, messageId, emoji, next, contents)
}

class UserAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	val channel = ChannelAction(platform, userId, service)

	suspend fun get(
		userId: String,
		vararg contents: Pair<String, Any>,
	) = service.userGet(platform, this.userId, userId, contents)

	class ChannelAction(
		private val platform: String,
		private val userId: String,
		service: AdapterActionService,
	) : ActionLeaf(service) {
		suspend fun create(
			userId: String,
			guildId: String? = null,
			vararg contents: Pair<String, Any>,
		) = service.userChannelCreate(platform, this.userId, userId, guildId, contents)
	}
}

class FriendAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun list(
		next: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.friendList(platform, userId, next, contents)

	suspend fun approve(
		messageId: String,
		approve: Boolean,
		comment: String? = null,
		vararg contents: Pair<String, Any>,
	) = service.friendApprove(platform, userId, messageId, approve, comment, contents)
}

class UploadAction(
	private val platform: String,
	private val userId: String,
	service: AdapterActionService,
) : ActionLeaf(service) {
	suspend fun create(vararg contents: FormData) = service.uploadCreate(platform, userId, contents)
}

class AdminAction(
	service: AdapterActionService,
) {
	val login = LoginAction(service)
	val webhook = WebhookAction(service)

	class LoginAction(
		service: AdapterActionService,
	) : ActionLeaf(service) {
		suspend fun list(vararg contents: Pair<String, Any>) = service.adminLoginList(contents)
	}

	class WebhookAction(
		service: AdapterActionService,
	) : ActionLeaf(service) {
		suspend fun create(
			url: String,
			token: String? = null,
			vararg contents: Pair<String, Any>,
		) = service.adminWebhookCreate(url, token, contents)

		suspend fun delete(
			url: String,
			vararg contents: Pair<String, Any>,
		) = service.adminWebhookDelete(url, contents)
	}
}