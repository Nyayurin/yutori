@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.toPairArray

object Br : MessageElement(
    elementName = "br",
    properties = emptyMap(),
    children = emptyList()
) {
    object Container : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Br
    }
}

class Paragraph(children: List<MessageElement>) : MessageElement(
    elementName = "paragraph",
    properties = emptyMap(),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Paragraph(children)
    }
}

class Message(
    val id: String?,
    val forward: Boolean?,
    extendProperties: Map<String, Any?> = emptyMap(),
    children: List<MessageElement> = emptyList()
) : MessageElement(
    elementName = "message",
    properties = mapOf("id" to id, "forward" to forward, *extendProperties.toPairArray()),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Message(
            id = properties.remove("id") as String?,
            forward = properties.remove("forward") as Boolean?,
            extendProperties = properties,
            children = children
        )
    }
}