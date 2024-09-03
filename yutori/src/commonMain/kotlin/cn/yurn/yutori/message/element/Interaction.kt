@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

class Button(
    id: String? = null,
    type: String? = null,
    href: String? = null,
    text: String? = null,
    theme: String? = null
) : NodeMessageElement("button", "id" to id, "type" to type, "href" to href, "text" to text, "theme" to theme) {
    var id: String? by properties
    var type: String? by properties
    var href: String? by properties
    var text: String? by properties
    var theme: String? by properties

    companion object : MessageElementContainer("id" to "", "type" to "", "href" to "", "text" to "", "theme" to "") {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Button()
    }
}