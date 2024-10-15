package cn.yurn.yutori.module.yhchat.message

import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.message.ExtendedMessageBuilder
import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.module.yhchat.message.element.HTML
import cn.yurn.yutori.module.yhchat.message.element.Markdown

val MessageBuilder.yhchat: YhChatMessageBuilder
    get() = builders["yhchat"] as YhChatMessageBuilder

@BuilderMarker
class YhChatMessageBuilder(builder: MessageBuilder) : ExtendedMessageBuilder(builder) {
    inline fun markdown(content: String) = Markdown(content).apply { elements += this }
    inline fun html(content: String) = HTML(content).apply { elements += this }
}