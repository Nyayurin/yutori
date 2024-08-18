@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import com.fleeksoft.ksoup.nodes.Element

class Quote(
    id: String? = null,
    forward: Boolean? = null
) : NodeMessageElement("quote", "id" to id, "forward" to forward) {
    var id: String? by properties
    var forward: Boolean? by properties

    companion object : MessageElementContainer("id" to "", "forward" to false) {
        override operator fun invoke(element: Element) = Quote()
    }
}

class Author(
    id: String? = null,
    name: String? = null,
    avatar: String? = null
) : NodeMessageElement("author", "id" to id, "name" to name, "avatar" to avatar) {
    var id: String? by properties
    var name: String? by properties
    var avatar: String? by properties

    companion object : MessageElementContainer("id" to "", "name" to "", "avatar" to "") {
        override operator fun invoke(element: Element) = Author()
    }
}