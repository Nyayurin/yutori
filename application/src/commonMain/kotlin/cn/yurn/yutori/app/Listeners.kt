package cn.yurn.yutori.app

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cn.yurn.yutori.Context
import cn.yurn.yutori.Login
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Satori
import cn.yurn.yutori.app.ui.components.AtMessageElementViewer
import cn.yurn.yutori.app.ui.components.AudioMessageElementViewer
import cn.yurn.yutori.app.ui.components.AuthorMessageElementViewer
import cn.yurn.yutori.app.ui.components.BoldMessageElementViewer
import cn.yurn.yutori.app.ui.components.BrMessageElementViewer
import cn.yurn.yutori.app.ui.components.ButtonMessageElementViewer
import cn.yurn.yutori.app.ui.components.CodeMessageElementViewer
import cn.yurn.yutori.app.ui.components.DeleteMessageElementViewer
import cn.yurn.yutori.app.ui.components.EmMessageElementViewer
import cn.yurn.yutori.app.ui.components.FileMessageElementViewer
import cn.yurn.yutori.app.ui.components.HrefMessageElementViewer
import cn.yurn.yutori.app.ui.components.IdiomaticMessageElementViewer
import cn.yurn.yutori.app.ui.components.ImageMessageElementViewer
import cn.yurn.yutori.app.ui.components.InsMessageElementViewer
import cn.yurn.yutori.app.ui.components.MessageMessageElementViewer
import cn.yurn.yutori.app.ui.components.ParagraphMessageElementViewer
import cn.yurn.yutori.app.ui.components.QuoteMessageElementViewer
import cn.yurn.yutori.app.ui.components.SharpMessageElementViewer
import cn.yurn.yutori.app.ui.components.SplMessageElementViewer
import cn.yurn.yutori.app.ui.components.StrikethroughMessageElementViewer
import cn.yurn.yutori.app.ui.components.StrongMessageElementViewer
import cn.yurn.yutori.app.ui.components.SubMessageElementViewer
import cn.yurn.yutori.app.ui.components.SupMessageElementViewer
import cn.yurn.yutori.app.ui.components.TextMessageElementViewer
import cn.yurn.yutori.app.ui.components.UnderlineMessageElementViewer
import cn.yurn.yutori.app.ui.components.UnsupportedMessageElementViewer
import cn.yurn.yutori.app.ui.components.VideoMessageElementViewer
import cn.yurn.yutori.channel
import cn.yurn.yutori.guild
import cn.yurn.yutori.member
import cn.yurn.yutori.message
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import cn.yurn.yutori.module.adapter.satori.SatoriActionService
import cn.yurn.yutori.nick
import cn.yurn.yutori.toElements
import cn.yurn.yutori.user

suspend fun onConnect(
    viewModel: MainViewModel,
    logins: List<Login>,
    service: SatoriActionService,
    satori: Satori,
    requestChannels: Boolean
) {
    if (viewModel.ready) return
    viewModel.ready = true
    viewModel.platform = logins[0].platform!!
    viewModel.selfId = logins[0].self_id!!
    viewModel.actions = RootActions(
        platform = viewModel.platform,
        self_id = viewModel.selfId,
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
            if (user.id == viewModel.selfId) {
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
    val content = buildString {
        if (event.user.id == event.self_id) {
            append("me")
        } else {
            append(event.nick())
        }
        append(": ")
        for (element in event.message.content.toElements(satori)) {
            append(
                when (element) {
                    is Text -> TextMessageElementViewer.preview(element)
                    is At -> AtMessageElementViewer.preview(element)
                    is Sharp -> SharpMessageElementViewer.preview(element)
                    is Href -> HrefMessageElementViewer.preview(element)
                    is Image -> ImageMessageElementViewer.preview(element)
                    is Audio -> AudioMessageElementViewer.preview(element)
                    is Video -> VideoMessageElementViewer.preview(element)
                    is File -> FileMessageElementViewer.preview(element)
                    is Bold -> BoldMessageElementViewer.preview(element)
                    is Strong -> StrongMessageElementViewer.preview(element)
                    is Idiomatic -> IdiomaticMessageElementViewer.preview(element)
                    is Em -> EmMessageElementViewer.preview(element)
                    is Underline -> UnderlineMessageElementViewer.preview(element)
                    is Ins -> InsMessageElementViewer.preview(element)
                    is Strikethrough -> StrikethroughMessageElementViewer.preview(element)
                    is Delete -> DeleteMessageElementViewer.preview(element)
                    is Spl -> SplMessageElementViewer.preview(element)
                    is Code -> CodeMessageElementViewer.preview(element)
                    is Sup -> SupMessageElementViewer.preview(element)
                    is Sub -> SubMessageElementViewer.preview(element)
                    is Br -> BrMessageElementViewer.preview(element)
                    is Paragraph -> ParagraphMessageElementViewer.preview(element)
                    is cn.yurn.yutori.message.element.Message -> MessageMessageElementViewer.preview(element)
                    is Quote -> QuoteMessageElementViewer.preview(element)
                    is Author -> AuthorMessageElementViewer.preview(element)
                    is Button -> ButtonMessageElementViewer.preview(element)
                    else -> UnsupportedMessageElementViewer.preview(element)
                }
            )
        }
    }
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