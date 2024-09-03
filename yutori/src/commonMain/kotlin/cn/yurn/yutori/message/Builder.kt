@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.yurn.yutori.message

import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.Satori
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Message
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video

inline fun message(satori: Satori, block: MessageBuilder.() -> Unit) =
    MessageBuilder(satori).apply(block).elements

interface ChildedMessageBuilder {
    val elements: MutableList<MessageElement>
    operator fun get(index: Int) = elements[index]
    operator fun set(index: Int, element: MessageElement) {
        elements[index] = element
    }
}

interface PropertiedMessageBuilder : ChildedMessageBuilder {
    val properties: MutableMap<String, Any?>
    operator fun get(key: String) = properties[key]
    operator fun set(key: String, value: Any?) {
        properties[key] = value
    }

    fun buildElement(): NodeMessageElement

    fun buildElement(element: NodeMessageElement): NodeMessageElement {
        element.properties.putAll(this.properties)
        element.children.addAll(this.elements)
        return element
    }
}

abstract class ExtendedMessageBuilder(builder: MessageBuilder) {
    val satori: Satori = builder.satori
    val elements: MutableList<MessageElement> = builder.elements
}

@BuilderMarker
open class MessageBuilder(val satori: Satori) : ChildedMessageBuilder {
    override val elements = mutableListOf<MessageElement>()
    val builders = mutableMapOf<String, ExtendedMessageBuilder>().apply {
        for ((key, value) in satori.message_builders) this[key] = value(this@MessageBuilder)
    }

    fun element(element: MessageElement) = elements.add(element)

    inline fun text(block: () -> String) = Text(block()).apply { elements += this }
    inline fun node(name: String, block: Node.() -> Unit) =
        Node(name, satori).apply(block).buildElement().apply { elements += this }

    inline fun at(block: At.() -> Unit) =
        At(satori).apply(block).buildElement().apply { elements += this }

    inline fun sharp(block: Sharp.() -> Unit) =
        Sharp(satori).apply(block).buildElement().apply { elements += this }

    inline fun a(block: Href.() -> Unit) =
        Href(satori).apply(block).buildElement().apply { elements += this }

    inline fun img(block: Image.() -> Unit) =
        Image(satori).apply(block).buildElement().apply { elements += this }

    inline fun audio(block: Audio.() -> Unit) =
        Audio(satori).apply(block).buildElement().apply { elements += this }

    inline fun video(block: Video.() -> Unit) =
        Video(satori).apply(block).buildElement().apply { elements += this }

    inline fun file(block: File.() -> Unit) =
        File(satori).apply(block).buildElement().apply { elements += this }

    inline fun b(block: Bold.() -> Unit) =
        Bold(satori).apply(block).buildElement().apply { elements += this }

    inline fun strong(block: Bold.() -> Unit) =
        Bold(satori).apply(block).buildElement().apply { elements += this }

    inline fun i(block: Idiomatic.() -> Unit) =
        Idiomatic(satori).apply(block).buildElement().apply { elements += this }

    inline fun em(block: Idiomatic.() -> Unit) =
        Idiomatic(satori).apply(block).buildElement().apply { elements += this }

    inline fun u(block: Underline.() -> Unit) =
        Underline(satori).apply(block).buildElement().apply { elements += this }

    inline fun ins(block: Underline.() -> Unit) =
        Underline(satori).apply(block).buildElement().apply { elements += this }

    inline fun s(block: Delete.() -> Unit) =
        Delete(satori).apply(block).buildElement().apply { elements += this }

    inline fun del(block: Delete.() -> Unit) =
        Delete(satori).apply(block).buildElement().apply { elements += this }

    inline fun spl(block: Spl.() -> Unit) =
        Spl(satori).apply(block).buildElement().apply { elements += this }

    inline fun code(block: Code.() -> Unit) =
        Code(satori).apply(block).buildElement().apply { elements += this }

    inline fun sup(block: Sup.() -> Unit) =
        Sup(satori).apply(block).buildElement().apply { elements += this }

    inline fun sub(block: Sub.() -> Unit) =
        Sub(satori).apply(block).buildElement().apply { elements += this }

    inline fun br(block: Br.() -> Unit) =
        Br(satori).apply(block).buildElement().apply { elements += this }

    inline fun p(block: Paragraph.() -> Unit) =
        Paragraph(satori).apply(block).buildElement().apply { elements += this }

    inline fun message(block: Message.() -> Unit) =
        Message(satori).apply(block).buildElement().apply { elements += this }

    inline fun quote(block: Quote.() -> Unit) =
        Quote(satori).apply(block).buildElement().apply { elements += this }

    inline fun author(block: Author.() -> Unit) =
        Author(satori).apply(block).buildElement().apply { elements += this }

    inline fun button(block: Button.() -> Unit) =
        Button(satori).apply(block).buildElement().apply { elements += this }

    @BuilderMarker
    class Node(private val name: String, satori: Satori) : MessageBuilder(satori),
        PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement(): NodeMessageElement = buildElement(NodeMessageElement(name))
    }

    @BuilderMarker
    class At(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties =
            mutableMapOf<String, Any?>("id" to null, "name" to null, "role" to null, "type" to null)
        var id: String? by properties
        var name: String? by properties
        var role: String? by properties
        var type: String? by properties
        override fun buildElement() = buildElement(At(id, name, role, type))
    }

    @BuilderMarker
    class Sharp(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>("id" to "", "name" to null)
        var id: String by properties
        var name: String? by properties
        override fun buildElement() = buildElement(Sharp(id, name))
    }

    @BuilderMarker
    class Href(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>("href" to "")
        var href: String by properties
        override fun buildElement() = buildElement(Href(href))
    }

    @BuilderMarker
    class Image(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>(
            "src" to "", "title" to null, "cache" to null, "timeout" to null, "width" to null, "height" to null
        )
        var src: String by properties
        var title: String? by properties
        var cache: Boolean? by properties
        var timeout: String? by properties
        var width: Number? by properties
        var height: Number? by properties
        override fun buildElement() = buildElement(Image(src, title, cache, timeout, width, height))
    }

    @BuilderMarker
    class Audio(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>(
            "src" to "", "title" to null, "cache" to null, "timeout" to null, "duration" to null, "poster" to null
        )
        var src: String by properties
        var title: String? by properties
        var cache: Boolean? by properties
        var timeout: String? by properties
        var duration: Number? by properties
        var poster: String? by properties
        override fun buildElement() = buildElement(Audio(src, title, cache, timeout, duration, poster))
    }

    @BuilderMarker
    class Video(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>(
            "src" to "", "title" to null, "cache" to null, "timeout" to null, "width" to null, "height" to null,
            "duration" to null, "poster" to null
        )
        var src: String by properties
        var title: String? by properties
        var cache: Boolean? by properties
        var timeout: String? by properties
        var width: Number? by properties
        var height: Number? by properties
        var duration: Number? by properties
        var poster: String? by properties
        override fun buildElement() =
            buildElement(Video(src, title, cache, timeout, width, height, duration, poster))
    }

    @BuilderMarker
    class File(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>(
            "src" to "", "title" to null, "cache" to null, "timeout" to null, "poster" to null
        )
        var src: String by properties
        var title: String? by properties
        var cache: Boolean? by properties
        var timeout: String? by properties
        var poster: String? by properties
        override fun buildElement() = buildElement(File(src, title, cache, timeout, poster))
    }

    @BuilderMarker
    class Bold(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Bold())
    }

    @BuilderMarker
    class Idiomatic(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Idiomatic())
    }

    @BuilderMarker
    class Underline(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Underline())
    }

    @BuilderMarker
    class Delete(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Strikethrough())
    }

    @BuilderMarker
    class Spl(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Spl())
    }

    @BuilderMarker
    class Code(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Code())
    }

    @BuilderMarker
    class Sup(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Sup())
    }

    @BuilderMarker
    class Sub(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Sub())
    }

    @BuilderMarker
    class Br(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Br())
    }

    @BuilderMarker
    class Paragraph(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>()
        override fun buildElement() = buildElement(Paragraph())
    }

    @BuilderMarker
    class Message(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>("id" to null, "forward" to null)
        var id: String? by properties
        var forward: Boolean? by properties
        override fun buildElement() = buildElement(Message(id, forward))
    }

    @BuilderMarker
    class Quote(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>("id" to null, "forward" to null)
        var id: String? by properties
        var forward: Boolean? by properties
        override fun buildElement() = buildElement(Quote(id, forward))
    }

    @BuilderMarker
    class Author(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties = mutableMapOf<String, Any?>("id" to null, "name" to null, "avatar" to null)
        var id: String? by properties
        var name: String? by properties
        var avatar: String? by properties
        override fun buildElement() = buildElement(Author(id, name, avatar))
    }

    @BuilderMarker
    class Button(satori: Satori) : MessageBuilder(satori), PropertiedMessageBuilder {
        override val properties =
            mutableMapOf<String, Any?>("id" to null, "type" to null, "href" to null, "text" to null, "theme" to null)
        var id: String? by properties
        var type: String? by properties
        var href: String? by properties
        var text: String? by properties
        var theme: String? by properties
        override fun buildElement() = buildElement(Button(id, type, href, text, theme))
    }
}