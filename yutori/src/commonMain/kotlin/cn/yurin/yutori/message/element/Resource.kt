@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori.message.element

import cn.yurin.yutori.toPairArray

class Image(
    val src: String,
    val title: String?,
    val cache: Boolean?,
    val timeout: String?,
    val width: Number?,
    val height: Number?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "image",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "width" to width,
        "height" to height,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Image(
            src = properties.remove("src") as String,
            title = properties.remove("title") as String?,
            cache = properties.remove("cache") as Boolean?,
            timeout = properties.remove("timeout") as String?,
            width = properties.remove("width") as Number?,
            height = properties.remove("height") as Number?,
            extendProperties = properties,
            children = children
        )
    }
}

class Audio(
    val src: String,
    val title: String?,
    val cache: Boolean?,
    val timeout: String?,
    val duration: Number?,
    val poster: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "audio",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "duration" to duration,
        "poster" to poster,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Audio(
            src = properties.remove("src") as String,
            title = properties.remove("title") as String?,
            cache = properties.remove("cache") as Boolean?,
            timeout = properties.remove("timeout") as String?,
            duration = properties.remove("duration") as Number?,
            poster = properties.remove("poster") as String?,
            extendProperties = properties,
            children = children
        )
    }
}

class Video(
    val src: String,
    val title: String?,
    val cache: Boolean?,
    val timeout: String?,
    val width: Number?,
    val height: Number?,
    val duration: Number?,
    val poster: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "video",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "width" to width,
        "height" to height,
        "duration" to duration,
        "poster" to poster,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = Video(
            src = properties.remove("src") as String,
            title = properties.remove("title") as String?,
            cache = properties.remove("cache") as Boolean?,
            timeout = properties.remove("timeout") as String?,
            width = properties.remove("width") as Number?,
            height = properties.remove("height") as Number?,
            duration = properties.remove("duration") as Number?,
            poster = properties.remove("poster") as String?,
            extendProperties = properties,
            children = children
        )
    }
}

class File(
    val src: String,
    val title: String?,
    val cache: Boolean?,
    val timeout: String?,
    val poster: String?,
    extendProperties: Map<String, Any?>,
    children: List<MessageElement>
) : MessageElement(
    elementName = "file",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "poster" to poster,
        *extendProperties.toPairArray()
    ),
    children = children
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, Any?>,
            children: List<MessageElement>
        ) = File(
            src = properties.remove("src") as String,
            title = properties.remove("title") as String?,
            cache = properties.remove("cache") as Boolean?,
            timeout = properties.remove("timeout") as String?,
            poster = properties.remove("poster") as String?,
            extendProperties = properties,
            children = children
        )
    }
}