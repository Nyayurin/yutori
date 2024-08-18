@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

abstract class Module {
    abstract fun install(satori: Satori)
    abstract fun uninstall(satori: Satori)

    companion object
}

abstract class Adapter : Module() {
    abstract suspend fun start(satori: Satori)
    abstract fun stop(satori: Satori)

    companion object
}

interface Reinstallable