@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori

abstract class AdapterActionService {
    abstract suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        userId: String?,
        content: Map<String, Any?>
    ): Any

    abstract suspend fun upload(
        resource: String,
        method: String,
        platform: String,
        userId: String,
        content: List<FormData>
    ): Map<String, String>
}

abstract class AdapterEventService(val alias: String?) {
    abstract suspend fun connect()
    abstract fun disconnect()
}

abstract class ServerService(val alias: String?) {
    abstract suspend fun start()
    abstract suspend fun pushEvent(event: Event<SigningEvent>)
    abstract fun stop()
}