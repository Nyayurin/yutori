@file:Suppress("unused", "UNCHECKED_CAST")

package cn.yurn.yutori

import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Text
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.*
import kotlin.reflect.KProperty

@DslMarker
annotation class BuilderMarker

fun String.encode() = replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
fun String.decode() = replace("&gt;", ">").replace("&lt;", "<").replace("&quot;", "\"").replace("&amp;", "&")
fun String.toElements(satori: Satori) = MessageUtil.parse(satori, this)

object MessageUtil {
    fun select(element: String, vararg elements: MessageElement): MessageElement? {
        for (e in elements) {
            if (e is NodeMessageElement) return e.select(element) ?: continue
            if (e is Text && element == "text") return e
        }
        return null
    }

    fun parse(satori: Satori, context: String): List<MessageElement> {
        val nodes = Ksoup.parse(context).body().childNodes().filter {
            it !is Comment && it !is DocumentType
        }
        return List(nodes.size) { i -> parseElement(satori, nodes[i]) }
    }

    private fun parseElement(satori: Satori, node: Node): MessageElement = when (node) {
        is TextNode -> Text(node.text())
        is Element -> {
            val container = satori.elements[node.tagName()] ?: NodeMessageElement
            container(node).apply {
                for (attr in node.attributes()) {
                    val key = attr.key
                    val value = attr.value
                    this.properties[key] = when (val default = container.properties_default[key] ?: "") {
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
                for (child in node.childNodes()) this.children += parseElement(satori, child)
            }
        }

        else -> throw MessageElementParsingException(node.toString())
    }
}

suspend fun Context<MessageEvent>.reply(quote: Boolean = true, content: MessageBuilder.() -> Unit) {
    actions.message.create(
        channel_id = event.channel.id,
        content = {
            if (quote) quote { id = event.message.id }
            content()
        }
    )
}

fun Event<*>.nick() = nullable_member?.nick ?: nullable_user?.nick ?: nullable_user?.name

fun <T> MutableMap<String, Any?>.delegate(key: String) = Delegate<T>(this, key)
fun <T> MutableMap<String, Any?>.delegateNullable(key: String) = DelegateNullable<T>(this, key)

class Delegate<T>(private val map: MutableMap<String, Any?>, private val key: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = map[key] as T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Any) {
        map[key] = value
    }
}

class DelegateNullable<T>(private val map: MutableMap<String, Any?>, private val key: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = map[key] as T?
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Any?) {
        map[key] = value
    }
}