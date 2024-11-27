@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori

abstract class Module(
    val alias: String?,
) {
    abstract fun install(yutori: YutoriBuilder)

    abstract fun uninstall(yutori: YutoriBuilder)

    companion object
}

interface Startable {
    suspend fun start(yutori: Yutori)
}

interface Stopable {
    fun stop(yutori: Yutori)
}

abstract class Adapter(
    alias: String?,
) : Module(alias),
    Startable,
    Stopable {
    companion object
}

abstract class Server(
    alias: String?,
) : Module(alias),
    Startable,
    Stopable {
    abstract suspend fun pushEvent(event: Event<SigningEvent>)

    companion object
}

interface Reinstallable