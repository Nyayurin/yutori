@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

abstract class Module(val alias: String?) {
    abstract fun install(yutori: Yutori)
    abstract fun uninstall(yutori: Yutori)

    companion object
}

interface Startable {
    suspend fun start(yutori: Yutori)
    fun stop(yutori: Yutori)
}

abstract class Adapter(alias: String?) : Module(alias), Startable {
    companion object
}

abstract class Server(alias: String?) : Module(alias), Startable {
    abstract suspend fun pushEvent(event: Event<SigningEvent>)
    companion object
}

interface Reinstallable