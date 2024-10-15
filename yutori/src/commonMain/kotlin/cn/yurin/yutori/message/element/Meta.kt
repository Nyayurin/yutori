@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori.message.element

import cn.yurin.yutori.toPairArray

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
            properties: MutableMap<String, String?>,
            children: List<MessageElement>
        ) = Quote(
            id = properties.remove("id"),
            forward = properties.remove("forward")?.convert(),
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
            properties: MutableMap<String, String?>,
            children: List<MessageElement>
        ) = Author(
            id = properties.remove("id"),
            name = properties.remove("name"),
            avatar = properties.remove("avatar"),
            extendProperties = properties,
            children = children
        )
    }
}