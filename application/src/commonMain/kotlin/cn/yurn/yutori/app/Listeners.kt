package cn.yurn.yutori.app

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cn.yurn.yutori.Context
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Satori
import cn.yurn.yutori.channel
import cn.yurn.yutori.guild
import cn.yurn.yutori.member
import cn.yurn.yutori.message
import cn.yurn.yutori.module.adapter.satori.SatoriActionService
import cn.yurn.yutori.nick
import cn.yurn.yutori.user

suspend fun onConnect(
    viewModel: MainViewModel,
    service: SatoriActionService,
    satori: Satori,
    platform: String,
    selfId: String,
    requestChannels: Boolean
) {
    if (viewModel.ready) return
    viewModel.ready = true
    viewModel.actions = RootActions(
        platform = platform,
        self_id = selfId,
        service = service,
        satori = satori
    )
    if (!requestChannels) return
    var next: String? = null
    val temp = mutableListOf<Chat>()
    do {
        val list = viewModel.actions!!.guild.list(next)
        for (guild in list.data) {
            temp += Chat(
                id = guild.id,
                avatar = guild.avatar.toString(),
                name = guild.name.toString(),
                content = ""
            )
        }
        next = list.next
    } while (next != null)
    do {
        val list = viewModel.actions!!.friend.list(next)
        for (user in list.data) {
            temp += Chat(
                id = "private:${user.id}",
                avatar = user.avatar.toString(),
                name = user.nick ?: user.name.toString(),
                content = ""
            )
            if (user.id == selfId) {
                viewModel.self = temp.last()
            }
        }
        next = list.next
    } while (next != null)
    temp.sortByDescending { it.updateTime }
    viewModel.chats += temp.distinctBy { it.id }
    for (chat in viewModel.chats) {
        val response = viewModel.actions!!.message.list(channel_id = chat.id)
        viewModel.messages[chat.id] = response.data.toMutableStateList()
    }
}

fun Context<MessageEvent>.onMessageCreated(viewModel: MainViewModel) {
    if (viewModel.self == null && event.user.id == event.self_id) {
        viewModel.self = Chat(
            id = "private:${event.user.id}",
            avatar = event.user.avatar.toString(),
            name = event.user.nick ?: event.user.name.toString(),
            content = ""
        )
    }
    val list = viewModel.messages.getOrPut(
        key = event.channel.id,
        defaultValue = { mutableStateListOf() }
    )
    if (list.find { it.id == event.message.id } != null) return
    list += event.message.copy(
        channel = event.channel,
        guild = event.guild,
        member = event.member,
        user = event.user,
        created_at = event.message.created_at ?: event.timestamp
    )
    val isPrivate = event.channel.id.startsWith("private:")
    val content =
        if (isPrivate) event.message.content else "${event.nick()}: ${event.message.content}"
    if (viewModel.chats.find { it.id == event.channel.id } == null) {
        viewModel.chats += Chat(
            id = event.channel.id,
            avatar = (if (isPrivate) event.user.avatar else event.guild?.avatar).toString(),
            name = (event.channel.name ?: if (isPrivate) event.user.nick
                ?: event.user.name else event.guild?.name).toString(),
            content = content,
            updateTime = event.timestamp.toLong(),
            unread = event.user.id != event.self_id
        )
    } else {
        val chat = viewModel.chats.find { it.id == event.channel.id }
        if (chat != null) {
            viewModel.chats[viewModel.chats.indexOf(chat)] = chat.copy(
                content = content,
                updateTime = event.timestamp.toLong(),
                unread = event.channel.id != viewModel.chatting?.id && event.user.id != event.self_id
            )
        }
    }
    viewModel.chats.sortByDescending { it.updateTime }
}