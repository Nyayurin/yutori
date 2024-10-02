@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

abstract class Module {
    abstract fun install(yutori: Yutori)
    abstract fun uninstall(yutori: Yutori)

    companion object
}

interface Startable {
    suspend fun start(yutori: Yutori)
    fun stop(yutori: Yutori)
}

abstract class Adapter : Module(), Startable {
    companion object
}

abstract class Server : Module(), Startable {
    abstract suspend fun pushEvent(event: Event<SigningEvent>)
    companion object
}

interface Reinstallable