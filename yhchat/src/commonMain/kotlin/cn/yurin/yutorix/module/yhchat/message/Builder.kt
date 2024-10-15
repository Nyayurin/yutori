package cn.yurin.yutorix.module.yhchat.message

import cn.yurin.yutori.BuilderMarker
import cn.yurin.yutori.message.ExtendedMessageBuilder
import cn.yurin.yutori.message.MessageBuilder
import cn.yurin.yutorix.module.yhchat.message.element.HTML
import cn.yurin.yutorix.module.yhchat.message.element.Markdown

val MessageBuilder.yhchat: YhChatMessageBuilder
    get() = builders["yhchat"] as YhChatMessageBuilder

@BuilderMarker
class YhChatMessageBuilder(builder: MessageBuilder) : ExtendedMessageBuilder(builder) {
    inline fun markdown(content: String) = Markdown(content).apply { elements += this }
    inline fun html(content: String) = HTML(content).apply { elements += this }
}