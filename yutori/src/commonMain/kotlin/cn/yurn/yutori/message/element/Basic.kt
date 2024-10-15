@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.toPairArray

class Text(val content: String) : MessageElement(
    elementName = "text",
    properties = mapOf("content" to content),
    children = emptyList()
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Text(properties["content"] as String)
    }
}

class At(
    val id: String?,
    val name: String?,
    val role: String?,
    val type: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "at",
    properties = mapOf(
        "id" to id,
        "name" to name,
        "role" to role,
        "type" to type,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = At(
            id = properties.remove("id") as String?,
            name = properties.remove("name") as String?,
            role = properties.remove("role") as String?,
            type = properties.remove("type") as String?,
            extendProperties = properties,
            children = children
        )
    }
}

class Sharp(
    val id: String,
    val name: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "sharp",
    properties = mapOf("id" to id, "name" to name, *extendProperties.toPairArray()),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Sharp(
            id = properties.remove("id") as String,
            name = properties.remove("name") as String?,
            extendProperties = properties,
            children = children
        )
    }
}

class Href(
    val href: String,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "href",
    properties = mapOf("href" to href, *extendProperties.toPairArray()),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Href(
            href = properties.remove("href") as String,
            extendProperties = properties,
            children = children
        )
    }
}