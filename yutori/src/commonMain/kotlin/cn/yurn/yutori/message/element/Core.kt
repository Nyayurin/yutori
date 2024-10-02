@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

abstract class MessageElementContainer(vararg pairs: Pair<String, Any>) {
    val propertiesDefault = mutableMapOf(*pairs)
    abstract operator fun invoke(attributes: Map<String, Any?> = mapOf()): NodeMessageElement
}

abstract class MessageElement {
    abstract override fun toString(): String
}

open class NodeMessageElement(
    val nodeName: String,
    vararg pairs: Pair<String, Any?>
) : MessageElement() {
    val properties = mutableMapOf(*pairs)
    val children = mutableListOf<MessageElement>()

    override fun toString() = buildString {
        append(nodeName)
        if (properties.isNotEmpty()) {
            append(properties.entries
                .filter { (_, value) -> value != null }
                .joinToString(",", "(", ")") { (key, value) ->
                    "$key=${
                        when (value) {
                            is String -> "\"$value\""
                            is Number -> value
                            is Boolean -> value
                            else -> throw Exception("Invalid type: $key = $value")
                        }
                    }"
                }
            )
        }
        if (children.isNotEmpty()) {
            append(children.joinToString(",", "{", "}") { it.toString() })
        }
    }

    fun select(element: String): MessageElement? {
        if (nodeName == element) return this
        for (child in children) {
            if (element == "text" && child is Text) return child
            child as NodeMessageElement
            if (child.nodeName == element) return child
            return child.select(element) ?: continue
        }
        return null
    }
}