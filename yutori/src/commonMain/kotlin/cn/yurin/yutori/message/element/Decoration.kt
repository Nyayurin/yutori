@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori.message.element

class Bold(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "bold",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Bold(children)
	}
}

class Strong(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "strong",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Strong(children)
	}
}

class Idiomatic(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "idiomatic",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Idiomatic(children)
	}
}

class Em(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "em",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Em(children)
	}
}

class Underline(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "underline",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Underline(children)
	}
}

class Ins(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "ins",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Ins(children)
	}
}

class Strikethrough(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "strikethrough",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Strikethrough(children)
	}
}

class Delete(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "delete",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Delete(children)
	}
}

class Spl(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "spl",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Spl(children)
	}
}

class Code(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "code",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Code(children)
	}
}

class Sup(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "sup",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Sup(children)
	}
}

class Sub(
	children: List<MessageElement>,
) : MessageElement(
	elementName = "sub",
	properties = emptyMap(),
	children = children,
) {
	companion object : MessageElementContainer() {
		override operator fun invoke(
			properties: MutableMap<String, String?>,
			children: List<MessageElement>,
		) = Sub(children)
	}
}