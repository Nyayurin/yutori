@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

class Br : NodeMessageElement("br") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Br()
    }
}
class Paragraph : NodeMessageElement("p") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Paragraph()
    }
}
class Message(
    id: String? = null,
    forward: Boolean? = null
) : NodeMessageElement("message", "id" to id, "forward" to forward) {
    var id: String? by properties
    var forward: Boolean? by properties

    companion object : MessageElementContainer("id" to "", "forward" to false) {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Message()
    }
}