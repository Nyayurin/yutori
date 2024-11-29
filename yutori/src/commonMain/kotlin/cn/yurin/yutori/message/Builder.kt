@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurin.yutori.message

import cn.yurin.yutori.BuilderMarker
import cn.yurin.yutori.Yutori
import cn.yurin.yutori.message.element.At
import cn.yurin.yutori.message.element.Audio
import cn.yurin.yutori.message.element.Author
import cn.yurin.yutori.message.element.Bold
import cn.yurin.yutori.message.element.Br
import cn.yurin.yutori.message.element.Button
import cn.yurin.yutori.message.element.Code
import cn.yurin.yutori.message.element.Delete
import cn.yurin.yutori.message.element.Em
import cn.yurin.yutori.message.element.File
import cn.yurin.yutori.message.element.Href
import cn.yurin.yutori.message.element.Idiomatic
import cn.yurin.yutori.message.element.Image
import cn.yurin.yutori.message.element.Ins
import cn.yurin.yutori.message.element.Message
import cn.yurin.yutori.message.element.MessageElement
import cn.yurin.yutori.message.element.Paragraph
import cn.yurin.yutori.message.element.Quote
import cn.yurin.yutori.message.element.Sharp
import cn.yurin.yutori.message.element.Spl
import cn.yurin.yutori.message.element.Strikethrough
import cn.yurin.yutori.message.element.Strong
import cn.yurin.yutori.message.element.Sub
import cn.yurin.yutori.message.element.Sup
import cn.yurin.yutori.message.element.Text
import cn.yurin.yutori.message.element.Underline
import cn.yurin.yutori.message.element.Video

inline fun message(
    yutori: Yutori,
    block: @BuilderMarker MessageBuilder.() -> Unit,
) = MessageBuilder(yutori).apply(block).elements

interface ChildedMessageBuilder {
    val elements: MutableList<MessageElement>

    operator fun get(index: Int) = elements[index]
    operator fun set(
        index: Int,
        element: MessageElement,
    ) {
        elements[index] = element
    }
}

abstract class ExtendedMessageBuilder(
    builder: MessageBuilder,
) {
    val yutori: Yutori = builder.yutori
    val elements: MutableList<MessageElement> = builder.elements
}

open class MessageBuilder(
    val yutori: Yutori,
) : ChildedMessageBuilder {
    override val elements = mutableListOf<MessageElement>()
    val builders = yutori.messageBuilders.mapValues { (_, value) ->
        value(this@MessageBuilder)
    }

    fun element(element: MessageElement) = elements.add(element)

    fun text(content: String) = Text(content).apply { elements += this }

    inline fun node(
        name: String,
        properties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = MessageElement(
        elementName = name,
        properties = properties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun at(
        id: String? = null,
        name: String? = null,
        role: String? = null,
        type: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = At(
        id = id,
        name = name,
        role = role,
        type = type,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun sharp(
        id: String,
        name: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Sharp(
        id = id,
        name = name,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun href(
        href: String,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Href(
        href = href,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun image(
        src: String,
        title: String? = null,
        cache: Boolean? = null,
        timeout: String? = null,
        width: Number? = null,
        height: Number? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Image(
        src = src,
        title = title,
        cache = cache,
        timeout = timeout,
        width = width,
        height = height,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun audio(
        src: String,
        title: String? = null,
        cache: Boolean? = null,
        timeout: String? = null,
        duration: Number? = null,
        poster: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Audio(
        src = src,
        title = title,
        cache = cache,
        timeout = timeout,
        duration = duration,
        poster = poster,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun video(
        src: String,
        title: String? = null,
        cache: Boolean? = null,
        timeout: String? = null,
        width: Number? = null,
        height: Number? = null,
        duration: Number? = null,
        poster: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Video(
        src = src,
        title = title,
        cache = cache,
        timeout = timeout,
        width = width,
        height = height,
        duration = duration,
        poster = poster,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun file(
        src: String,
        title: String? = null,
        cache: Boolean? = null,
        timeout: String? = null,
        poster: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = File(
        src = src,
        title = title,
        cache = cache,
        timeout = timeout,
        poster = poster,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun bold(
        children: MessageBuilder.() -> Unit = { }
    ) = Bold(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun strong(
        children: MessageBuilder.() -> Unit = { }
    ) = Strong(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun idiomatic(
        children: MessageBuilder.() -> Unit = { }
    ) = Idiomatic(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun em(
        children: MessageBuilder.() -> Unit = { }
    ) = Em(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun underline(
        children: MessageBuilder.() -> Unit = { }
    ) = Underline(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun ins(
        children: MessageBuilder.() -> Unit = { }
    ) = Ins(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun strikethrough(
        children: MessageBuilder.() -> Unit = { }
    ) = Strikethrough(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun delete(
        children: MessageBuilder.() -> Unit = { }
    ) = Delete(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun spl(
        children: MessageBuilder.() -> Unit = { }
    ) = Spl(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun code(
        children: MessageBuilder.() -> Unit = { }
    ) = Code(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun sup(
        children: MessageBuilder.() -> Unit = { }
    ) = Sup(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun sub(
        children: MessageBuilder.() -> Unit = { }
    ) = Sub(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    fun br() = Br.apply { elements += this }

    inline fun paragraph(
        children: MessageBuilder.() -> Unit = { }
    ) = Paragraph(
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun message(
        id: String? = null,
        forward: Boolean? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Message(
        id = id,
        forward = forward,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun quote(
        id: String? = null,
        forward: Boolean? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Quote(
        id = id,
        forward = forward,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun author(
        id: String? = null,
        name: String? = null,
        avatar: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Author(
        id = id,
        name = name,
        avatar = avatar,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }

    inline fun button(
        id: String? = null,
        type: String? = null,
        href: String? = null,
        text: String? = null,
        theme: String? = null,
        extendProperties: Map<String, Any?> = emptyMap(),
        children: MessageBuilder.() -> Unit = { },
    ) = Button(
        id = id,
        type = type,
        href = href,
        text = text,
        theme = theme,
        extendProperties = extendProperties,
        children = MessageBuilder(yutori).apply(children).elements,
    ).apply { elements += this }
}