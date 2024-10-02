@file:Suppress("MemberVisibilityCanBePrivate")

package cn.yurn.yutori

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


typealias ServerListener<T> = suspend (context: Context<T>) -> Unit

abstract class ExtendedServerListenersContainer {
    abstract operator fun invoke(context: Context<SigningEvent>)
}

@BuilderMarker
class ServerListenersContainer {
    val any = mutableListOf<ServerListener<SigningEvent>>()
    val containers = mutableMapOf<String, ExtendedServerListenersContainer>()

    fun any(listener: suspend Context<SigningEvent>.() -> Unit) = any.add { it.listener() }

    suspend operator fun invoke(context: Context<SigningEvent>) {
        coroutineScope {
            for (listener in any) {
                launch(context.yutori.adapter.exceptionHandler) {
                    listener(context)
                }
            }
        }
        for (container in containers.values) container(context)
    }
}