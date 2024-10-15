@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.toPairArray

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
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Button(
            id = properties.remove("id") as String?,
            type = properties.remove("type") as String?,
            href = properties.remove("href") as String?,
            text = properties.remove("text") as String?,
            theme = properties.remove("theme") as String?,
            extendProperties = properties,
            children = children
        )
    }
}