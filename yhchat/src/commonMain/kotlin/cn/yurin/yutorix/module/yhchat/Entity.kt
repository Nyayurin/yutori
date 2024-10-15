package cn.yurin.yutorix.module.yhchat

import kotlinx.serialization.Serializable

data class YhChatProperties(
    val host: String = "0.0.0.0",
    val port: Int = 8080,
    val path: String = "",
    val token: String = "",
    val userId: String = ""
)

@Serializable
data class YhChatEvent(
    val version: String,
    val header: Header,
    val event: Event
)

@Serializable
data class Header(
    val eventId: String,
    val eventTime: Long,
    val eventType: String
)

@Serializable
data class Event(
    val sender: Sender,
    val chat: Chat,
    val message: Message
)

@Serializable
data class Sender(
    val senderId: String,
    val senderType: String,
    val senderUserLevel: String,
    val senderNickname: String
)

@Serializable
data class Chat(
    val chatId: String,
    val chatType: String
)

@Serializable
data class Message(
    val msgId: String,
    val parentId: String,
    val sendTime: Long,
    val chatId: String,
    val chatType: String,
    val contentType: String,
    val content: Content,
    val commandId: Int,
    val commandName: String
)

@Serializable
data class Content(
    val text: String? = null,
    val imageUrl: String? = null,
    val fileName: String? = null,
    val fileUrl: String? = null,
    val buttons: List<Button>? = null
)

@Serializable
data class Button(
    val text: String,
    val actionType: Int,
    val url: String,
    val value: String
)

@Serializable
data class MessageInfo(
    val msgId: String,
    val recvId: String,
    val recvType: String
)

@Serializable
data class Messages(
    val list: List<Message>,
    val total: Int
) {
    @Serializable
    data class Message(
        val msgId: String,
        val parentId: String,
        val senderId: String,
        val senderType: String,
        val senderNickname: String,
        val contentType: String,
        val content: Content,
        val sendTime: Long,
        val commandId: Int,
        val commandName: String
    )
}