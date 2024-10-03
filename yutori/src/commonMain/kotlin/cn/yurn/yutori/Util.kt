@file:Suppress("unused", "UNCHECKED_CAST")

package cn.yurn.yutori

import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Text
import kotlin.reflect.KProperty

@DslMarker
annotation class BuilderMarker

object MessageUtil {
    fun select(element: String, vararg elements: MessageElement): MessageElement? {
        for (e in elements) {
            if (e is NodeMessageElement) return e.select(element) ?: continue
            if (e is Text && element == "text") return e
        }
        return null
    }
}

suspend fun AdapterContext<MessageEvent>.reply(quote: Boolean = true, content: MessageBuilder.() -> Unit) {
    actions.message.create(
        channelId = event.channel.id,
        content = {
            if (quote) quote { id = event.message.id }
            content()
        }
    )
}

fun Event<*>.nick() = nullableMember?.nick ?: nullableUser?.nick ?: nullableUser?.name

fun List<MessageElement>.textContent(): String =
    filterIsInstance<Text>().joinToString("") { it.text }

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