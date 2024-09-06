@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

abstract class Module {
    abstract fun install(yutori: Yutori)
    abstract fun uninstall(yutori: Yutori)

    companion object
}

abstract class Adapter : Module() {
    abstract suspend fun start(yutori: Yutori)
    abstract fun stop(yutori: Yutori)

    companion object
}

interface Reinstallable