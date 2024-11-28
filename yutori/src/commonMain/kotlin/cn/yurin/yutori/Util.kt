@file:Suppress("unused")

package cn.yurin.yutori

import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.element.Text

@Target(AnnotationTarget.TYPE)
@DslMarker
annotation class BuilderMarker

object MessageUtil {
    fun select(
        element: String,
        vararg elements: MessageElement,
    ): MessageElement? {
        for (e in elements) return e.select(element) ?: continue
        return null
    }
}

fun Event<*>.nick() = nullableMember?.nick ?: nullableUser?.nick ?: nullableUser?.name

fun List<MessageElement>.textContent(): String = filterIsInstance<Text>().joinToString("") { it.content }

fun <K, V> Map<K, V>.toPairArray(): Array<Pair<K, V>> = map { it.key to it.value }.toTypedArray()