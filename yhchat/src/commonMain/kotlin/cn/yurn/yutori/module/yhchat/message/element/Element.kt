package cn.yurn.yutori.module.yhchat.message.element

import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.MessageElementContainer

class Markdown(val content: String) : MessageElement(
    elementName = "yhchat:markdown",
    properties = mapOf("content" to content),
    children = emptyList()
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Markdown(properties["content"] as String)
    }
}

class HTML(val content: String) : MessageElement(
    elementName = "yhchat:html",
    properties = mapOf("content" to content),
    children = emptyList()
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = HTML(properties["content"] as String)
    }
}