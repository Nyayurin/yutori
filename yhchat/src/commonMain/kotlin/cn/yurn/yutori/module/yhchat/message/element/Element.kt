package cn.yurn.yutori.module.yhchat.message.element

import cn.yurn.yutori.message.element.MessageElementContainer
import cn.yurn.yutori.message.element.NodeMessageElement

class Markdown : NodeMessageElement("yhchat:markdown") {
    companion object : MessageElementContainer() {
        override fun invoke(
            attributes: Map<String, Any?>
        ) = Markdown()
    }
}