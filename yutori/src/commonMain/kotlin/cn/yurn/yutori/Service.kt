@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

interface ActionService {
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

interface EventService {
    suspend fun connect()
    fun disconnect()
}