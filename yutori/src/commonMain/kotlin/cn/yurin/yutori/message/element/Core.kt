@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori.message.element

abstract class MessageElementContainer {
    abstract operator fun invoke(
        properties: MutableMap<String, String?>,
        children: List<MessageElement>,
    ): MessageElement

    protected inline fun <reified T : Any> String?.convert(): T =
        when (T::class) {
            String::class -> this!!
            Int::class -> this!!.toInt()
            Long::class -> this!!.toLong()
            Double::class -> this!!.toDouble()
            Boolean::class -> this?.toBooleanStrict() ?: true
            else -> throw RuntimeException("Message element property parse failed: ${T::class}")
        } as T
}

open class MessageElement(
    val elementName: String,
    val properties: Map<String, Any?>,
    val children: List<MessageElement>,
) {
    override fun toString(): String =
        buildString {
            append(elementName)
            if (properties.isNotEmpty()) {
                append(
                    properties.entries
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
                        },
                )
            }
            if (children.isNotEmpty()) {
                append(children.joinToString(",", "{", "}") { it.toString() })
            }
        }

    fun select(element: String): MessageElement? {
        if (elementName == element) return this
        for (child in children) return child.select(element) ?: continue
        return null
    }
}