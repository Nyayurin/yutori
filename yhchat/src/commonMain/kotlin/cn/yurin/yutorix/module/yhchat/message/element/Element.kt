package cn.yurin.yutorix.module.yhchat.message.element

import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.element.MessageElementContainer

class Markdown(val content: String) : MessageElement(
    elementName = "yhchat:markdown",
    properties = mapOf("content" to content),
    children = emptyList()
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>
        ) = Markdown(properties["content"]!!)
    }
}

class HTML(val content: String) : MessageElement(
    elementName = "yhchat:html",
    properties = mapOf("content" to content),
    children = emptyList()
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>
        ) = HTML(properties["content"]!!)
    }
}