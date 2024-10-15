@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.toPairArray

class Quote(
    val id: String?,
    val forward: Boolean?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "quote",
    properties = mapOf("id" to id, "forward" to forward, *extendProperties.toPairArray()),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Quote(
            id = properties.remove("id") as String?,
            forward = properties.remove("forward") as Boolean?,
            extendProperties = properties,
            children = children
        )
    }
}

class Author(
    val id: String?,
    val name: String?,
    val avatar: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "author",
    properties = mapOf(
        "id" to id,
        "name" to name,
        "avatar" to avatar,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Author(
            id = properties.remove("id") as String?,
            name = properties.remove("name") as String?,
            avatar = properties.remove("avatar") as String?,
            extendProperties = properties,
            children = children
        )
    }
}