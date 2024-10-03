@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

interface AdapterActionService {
    suspend fun send(
        resource: String,
        method: String,
        platform: String?,
        selfId: String?,
        content: Map<String, Any?>
    ): Any

    suspend fun upload(
        resource: String,
        method: String,
        platform: String,
        selfId: String,
        content: List<FormData>
    ): Map<String, String>
}

interface AdapterEventService {
    suspend fun connect()
    fun disconnect()
}

interface ServerService {
    suspend fun start()
    suspend fun pushEvent(event: Event<SigningEvent>)
    suspend fun respond(content: String)
    fun stop()
}