@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.message.element

import com.fleeksoft.ksoup.nodes.Element

class Image(
    src: String,
    title: String? = null,
    cache: Boolean? = null,
    timeout: String? = null,
    width: Number? = null,
    height: Number? = null
) : NodeMessageElement(
    "img",
    "src" to src,
    "title" to title,
    "cache" to cache,
    "timeout" to timeout,
    "width" to width,
    "height" to height
) {
    var src: String by properties
    var title: String? by properties
    var cache: Boolean? by properties
    var timeout: String? by properties
    var width: Number? by properties
    var height: Number? by properties

    companion object : MessageElementContainer(
        "src" to "",
        "title" to "",
        "cache" to false,
        "timeout" to "",
        "width" to 0,
        "height" to 0
    ) {
        override operator fun invoke(element: Element) = Image(element.attr("src"))
    }
}

class Audio(
    src: String,
    title: String? = null,
    cache: Boolean? = null,
    timeout: String? = null,
    duration: Number? = null,
    poster: String? = null
) : NodeMessageElement(
    "audio",
    "src" to src,
    "title" to title,
    "cache" to cache,
    "timeout" to timeout,
    "duration" to duration,
    "poster" to poster
) {
    var src: String by properties
    var title: String? by properties
    var cache: Boolean? by properties
    var timeout: String? by properties
    var duration: Number? by properties
    var poster: String? by properties

    companion object : MessageElementContainer(
        "src" to "",
        "title" to "",
        "cache" to false,
        "timeout" to "",
        "duration" to 0,
        "poster" to ""
    ) {
        override operator fun invoke(element: Element) = Audio(element.attr("src"))
    }
}

class Video(
    src: String,
    title: String? = null,
    cache: Boolean? = null,
    timeout: String? = null,
    width: Number? = null,
    height: Number? = null,
    duration: Number? = null,
    poster: String? = null
) : NodeMessageElement(
    "video",
    "src" to src,
    "title" to title,
    "cache" to cache,
    "timeout" to timeout,
    "width" to width,
    "height" to height,
    "duration" to duration,
    "poster" to poster
) {
    var src: String by properties
    var title: String? by properties
    var cache: Boolean? by properties
    var timeout: String? by properties
    var width: Number? by properties
    var height: Number? by properties
    var duration: Number? by properties
    var poster: String? by properties

    companion object : MessageElementContainer(
        "src" to "",
        "title" to "",
        "cache" to false,
        "timeout" to "",
        "width" to 0,
        "height" to 0,
        "duration" to 0,
        "poster" to ""
    ) {
        override operator fun invoke(element: Element) = Video(element.attr("src"))
    }
}

class File(
    src: String,
    title: String? = null,
    cache: Boolean? = null,
    timeout: String? = null,
    poster: String? = null
) : NodeMessageElement(
    "file",
    "src" to src,
    "title" to title,
    "cache" to cache,
    "timeout" to timeout,
    "poster" to poster
) {
    var src: String by properties
    var title: String? by properties
    var cache: Boolean? by properties
    var timeout: String? by properties
    var poster: String? by properties

    companion object : MessageElementContainer(
        "src" to "",
        "title" to "",
        "cache" to false,
        "timeout" to "",
        "poster" to ""
    ) {
        override operator fun invoke(element: Element) = File(element.attr("src"))
    }
}