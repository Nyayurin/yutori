@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.encode
import com.fleeksoft.ksoup.nodes.Element

abstract class MessageElementContainer(vararg pairs: Pair<String, Any>) {
    val properties_default = mutableMapOf(*pairs)
    abstract operator fun invoke(element: Element): NodeMessageElement
}

abstract class MessageElement {
    abstract override fun toString(): String
}

open class NodeMessageElement(val node_name: String, vararg pairs: Pair<String, Any?>) : MessageElement() {
    val properties = mutableMapOf(*pairs)
    val children = mutableListOf<MessageElement>()

    override fun toString() = buildString {
        append("<$node_name")
        for (item in properties) {
            val key = item.key
            val value = item.value ?: continue
            append(" ").append(
                when (value) {
                    is String -> "$key=\"${value.encode()}\""
                    is Number -> "$key=\"$value\""
                    is Boolean -> if (value) key else ""
                    else -> throw Exception("Invalid type")
                }
            )
        }
        if (children.isEmpty()) {
            append("/>")
        } else {
            append(">")
            for (item in children) append(item)
            append("</$node_name>")
        }
    }

    fun select(element: String): MessageElement? {
        if (node_name == element) return this
        for (child in children) {
            if (element == "text" && child is Text) return child
            child as NodeMessageElement
            if (child.node_name == element) return child
            return child.select(element) ?: continue
        }
        return null
    }

    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = NodeMessageElement(element.tagName())
    }
}