package cn.yurn.yutori.module.yhchat.message

import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.message.ExtendedMessageBuilder
import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.message.PropertiedMessageBuilder
import cn.yurn.yutori.module.yhchat.message.element.Markdown

val MessageBuilder.YhChat: YhChatMessageBuilder
    get() = builders["yhchat"] as YhChatMessageBuilder

@BuilderMarker
class YhChatMessageBuilder(builder: MessageBuilder) : ExtendedMessageBuilder(builder) {
    inline fun markdown(block: Markdown.() -> Unit) =
        Markdown(yutori).apply(block).buildElement().apply { elements += this }

    @BuilderMarker
    class Markdown(yutori: Yutori) : MessageBuilder(yutori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Markdown())
    }
}