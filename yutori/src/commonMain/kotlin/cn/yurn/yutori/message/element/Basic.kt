@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import cn.yurn.yutori.encode
import com.fleeksoft.ksoup.nodes.Element

class Text(var text: String) : MessageElement() {
    override fun toString() = text.encode()
}

class At(
    id: String? = null,
    name: String? = null,
    role: String? = null,
    type: String? = null
) : NodeMessageElement("at", "id" to id, "name" to name, "role" to role, "type" to type) {
    var id: String? by properties
    var name: String? by properties
    var role: String? by properties
    var type: String? by properties

    companion object : MessageElementContainer("id" to "", "name" to "", "role" to "", "type" to "") {
        override operator fun invoke(element: Element) = At()
    }
}

class Sharp(id: String, name: String? = null) : NodeMessageElement("sharp", "id" to id, "name" to name) {
    var id: String by properties
    var name: String? by properties

    companion object : MessageElementContainer("id" to "", "name" to "") {
        override operator fun invoke(element: Element) = Sharp(element.attr("id"))
    }
}

class Href(href: String) : NodeMessageElement("a", "href" to href) {
    var href: String by properties

    companion object : MessageElementContainer("href" to "") {
        override operator fun invoke(element: Element) = Href(element.attr("href"))
    }
}