@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori.message.element

import cn.yurin.yutori.toPairArray

class Button(
    val id: String?,
    val type: String?,
    val href: String?,
    val text: String?,
    val theme: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "button",
    properties = mapOf(
        "id" to id,
        "type" to type,
        "href" to href,
        "text" to text,
        "theme" to theme,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>
        ) = Button(
            id = properties.remove("id"),
            type = properties.remove("type"),
            href = properties.remove("href"),
            text = properties.remove("text"),
            theme = properties.remove("theme"),
            extendProperties = properties,
            children = children
        )
    }
}