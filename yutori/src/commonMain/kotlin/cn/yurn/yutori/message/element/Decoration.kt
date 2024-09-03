@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

class Bold : NodeMessageElement("b") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Bold()
    }
}

class Strong : NodeMessageElement("strong") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Strong()
    }
}

class Idiomatic : NodeMessageElement("i") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Idiomatic()
    }
}

class Em : NodeMessageElement("em") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Em()
    }
}

class Underline : NodeMessageElement("u") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Underline()
    }
}

class Ins : NodeMessageElement("ins") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Ins()
    }
}

class Strikethrough : NodeMessageElement("s") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Strikethrough()
    }
}

class Delete : NodeMessageElement("del") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Delete()
    }
}

class Spl : NodeMessageElement("spl") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Spl()
    }
}

class Code : NodeMessageElement("code") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Code()
    }
}

class Sup : NodeMessageElement("sup") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Sup()
    }
}

class Sub : NodeMessageElement("sub") {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            attributes: Map<String, Any?>
        ) = Sub()
    }
}