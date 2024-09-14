package cn.yurn.yutori.module.satori

import cn.yurn.yutori.MessageElementParsingException
import cn.yurn.yutori.MessageElementPropertyParsingException
import cn.yurn.yutori.NumberParsingException
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Text
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.DocumentType
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

fun String.encode() = replace("&", "&amp;")
        .replace("\"", "&quot;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

fun String.decode() = replace("&gt;", ">")
    .replace("&lt;", "<")
    .replace("&quot;", "\"")
    .replace("&amp;", "&")

fun String.deserialize(yutori: Yutori): List<MessageElement> {
    val nodes = Ksoup.parse(this).body().childNodes().filter {
        it !is Comment && it !is DocumentType
    }
    return List(nodes.size) { i -> parseElement(yutori, nodes[i]) }
}

private fun parseElement(yutori: Yutori, node: Node): MessageElement = when (node) {
    is TextNode -> Text(node.text())
    is Element -> {
        val container = yutori.elements[node.tagName()]
        val attributes = buildMap<String, Any?> {
            for ((key, value) in node.attributes()) {
                put(key, value)
            }
        }
        container?.invoke(attributes)?.apply {
            for (attr in node.attributes()) {
                val key = attr.key
                val value = attr.value
                this.properties[key] =
                    when (val default = container.properties_default[key] ?: "") {
                        is String -> value
                        is Number -> try {
                            if (value.contains(".")) {
                                value.toDouble()
                            } else {
                                runCatching {
                                    value.toInt()
                                }.getOrElse {
                                    value.toLong()
                                }
                            }
                        } catch (_: NumberFormatException) {
                            throw NumberParsingException(value)
                        }

                        is Boolean -> try {
                            if (attr.toString().contains("=")) value.toBooleanStrict() else true
                        } catch (_: IllegalArgumentException) {
                            throw NumberParsingException(value)
                        }

                        else -> throw MessageElementPropertyParsingException(default::class.toString())
                    }
            }
            for (child in node.childNodes()) this.children += parseElement(yutori, child)
        } ?: NodeMessageElement(
            node.tagName()
        )
    }

    else -> throw MessageElementParsingException(node.toString())
}

fun List<MessageElement>.serialize() = joinToString("") { it.serialize() }

fun MessageElement.serialize() = when (this) {
    is Text -> serialize()
    is NodeMessageElement -> serialize()
    else -> throw UnsupportedOperationException("Unknown element type: $this")
}

private fun Text.serialize() = text.encode()
private fun NodeMessageElement.serialize() = buildString {
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