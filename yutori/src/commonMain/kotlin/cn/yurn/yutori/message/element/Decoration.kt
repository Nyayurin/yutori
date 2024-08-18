@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import com.fleeksoft.ksoup.nodes.Element

class Bold : NodeMessageElement("b") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Bold()
    }
}

class Strong : NodeMessageElement("strong") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Strong()
    }
}

class Idiomatic : NodeMessageElement("i") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Idiomatic()
    }
}

class Em : NodeMessageElement("em") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Em()
    }
}

class Underline : NodeMessageElement("u") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Underline()
    }
}

class Ins : NodeMessageElement("ins") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Ins()
    }
}

class Strikethrough : NodeMessageElement("s") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Strikethrough()
    }
}

class Delete : NodeMessageElement("del") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Delete()
    }
}

class Spl : NodeMessageElement("spl") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Spl()
    }
}

class Code : NodeMessageElement("code") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Code()
    }
}

class Sup : NodeMessageElement("sup") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Sup()
    }
}

class Sub : NodeMessageElement("sub") {
    companion object : MessageElementContainer() {
        override operator fun invoke(element: Element) = Sub()
    }
}