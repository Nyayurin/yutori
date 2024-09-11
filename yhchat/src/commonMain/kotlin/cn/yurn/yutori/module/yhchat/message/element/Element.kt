package cn.yurn.yutori.module.yhchat.message.element

import cn.yurn.yutori.message.element.MessageElement

class Markdown(var content: String) : MessageElement() {
    override fun toString() = content
}

class HTML(var content: String) : MessageElement() {
    override fun toString() = content
}