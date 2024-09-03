@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

class Text(var text: String) : MessageElement() {
    override fun toString() = "text{\"$text\"}"
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

    companion object :
        MessageElementContainer("id" to "", "name" to "", "role" to "", "type" to "") {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = At()
    }
}

class Sharp(
    id: String,
    name: String? = null
) : NodeMessageElement("sharp", "id" to id, "name" to name) {
    var id: String by properties
    var name: String? by properties

    companion object : MessageElementContainer("id" to "", "name" to "") {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Sharp(attributes["id"] as String)
    }
}

class Href(href: String) : NodeMessageElement("a", "href" to href) {
    var href: String by properties

    companion object : MessageElementContainer("href" to "") {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Href(attributes["href"] as String)
    }
}