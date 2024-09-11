@file:Suppress("MemberVisibilityCanBePrivate", "unused", "HttpUrlsUsage")

package cn.yurn.yutori.module.satori.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.Login
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.SatoriProperties
import cn.yurn.yutori.Yutori
import co.touchlab.kermit.Logger
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay

val Adapter.Companion.Satori: SatoriAdapter
    get() = SatoriAdapter()

@BuilderMarker
class SatoriAdapter : Adapter(), Reinstallable {
    var host: String = "127.0.0.1"
    var port: Int = 5500
    var path: String = ""
    var token: String? = null
    var version: String = "v1"
    var onStart: suspend WebSocketEventService.() -> Unit = { }
    var onConnect: suspend WebSocketEventService.(List<Login>) -> Unit = { }
    var onClose: suspend () -> Unit = { }
    var onError: suspend () -> Unit = { }
    private var connecting by atomic(false)
    private var service: WebSocketEventService? by atomic(null)

    fun onStart(block: suspend WebSocketEventService.() -> Unit) {
        onStart = block
    }

    fun onConnect(block: suspend WebSocketEventService.(List<Login>) -> Unit) {
        onConnect = block
    }

    fun onClose(block: suspend () -> Unit) {
        onClose = block
    }

    fun onError(block: suspend () -> Unit) {
        onError = block
    }

    override fun install(yutori: Yutori) {}
    override fun uninstall(yutori: Yutori) {}

    override suspend fun start(yutori: Yutori) {
        val properties = SatoriProperties(host, port, path, token, version)
        connecting = true
        var sequence: Number? = null
        do {
            service = WebSocketEventService(properties, onConnect, onClose, onError, yutori, sequence)
            service!!.onStart()
            service!!.connect()
            if (connecting) {
                sequence = (service as WebSocketEventService).sequence
                Logger.i(yutori.name) { "将在5秒后尝试重新连接" }
                delay(5000)
                Logger.i(yutori.name) { "尝试重新连接" }
            }
        } while (connecting)
    }

    override fun stop(yutori: Yutori) {
        connecting = false
        service?.disconnect()
        service = null
    }
}