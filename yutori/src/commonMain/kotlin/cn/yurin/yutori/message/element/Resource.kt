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
    children: List<MessageElement>,
) : MessageElement(
    elementName = "image",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "width" to width,
        "height" to height,
        *extendProperties.toPairArray(),
    ),
    children = children,
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>,
        ) = Image(
            src = properties.remove("src")!!,
            title = properties.remove("title"),
            cache = properties.remove("cache")?.convert(),
            timeout = properties.remove("timeout"),
            width = properties.remove("width")?.convert<Int>(),
            height = properties.remove("height")?.convert<Int>(),
            extendProperties = properties,
            children = children,
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
    children: List<MessageElement>,
) : MessageElement(
    elementName = "audio",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "duration" to duration,
        "poster" to poster,
        *extendProperties.toPairArray(),
    ),
    children = children,
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>,
        ) = Audio(
            src = properties.remove("src")!!,
            title = properties.remove("title"),
            cache = properties.remove("cache")?.convert(),
            timeout = properties.remove("timeout"),
            duration = properties.remove("duration")?.convert<Int>(),
            poster = properties.remove("poster"),
            extendProperties = properties,
            children = children,
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
    children: List<MessageElement>,
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
        *extendProperties.toPairArray(),
    ),
    children = children,
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>,
        ) = Video(
            src = properties.remove("src")!!,
            title = properties.remove("title"),
            cache = properties.remove("cache")?.convert(),
            timeout = properties.remove("timeout"),
            width = properties.remove("width")?.convert<Int>(),
            height = properties.remove("height")?.convert<Int>(),
            duration = properties.remove("duration")?.convert<Int>(),
            poster = properties.remove("poster"),
            extendProperties = properties,
            children = children,
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
    children: List<MessageElement>,
) : MessageElement(
    elementName = "file",
    properties = mapOf(
        "src" to src,
        "title" to title,
        "cache" to cache,
        "timeout" to timeout,
        "poster" to poster,
        *extendProperties.toPairArray(),
    ),
    children = children,
) {
    companion object : MessageElementContainer() {
        override operator fun invoke(
            properties: MutableMap<String, String?>,
            children: List<MessageElement>,
        ) = File(
            src = properties.remove("src")!!,
            title = properties.remove("title"),
            cache = properties.remove("cache")?.convert(),
            timeout = properties.remove("timeout"),
            poster = properties.remove("poster"),
            extendProperties = properties,
            children = children,
        )
    }
}