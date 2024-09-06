@file:Suppress("MemberVisibilityCanBePrivate", "unused", "HttpUrlsUsage")

package cn.yurn.yutori.module.satori.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.EventService
import cn.yurn.yutori.Login
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.SatoriProperties
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
    var webhook: WebHook? = null
    var onConnect: suspend (List<Login>, SatoriActionService, Yutori) -> Unit = { _, _, _ -> }
    var onClose: suspend () -> Unit = { }
    var onError: suspend () -> Unit = { }
    private var connecting by atomic(false)
    private var service: EventService? by atomic(null)

    fun onConnect(block: suspend (List<Login>, SatoriActionService, Yutori) -> Unit) {
        onConnect = block
    }

    fun onClose(block: suspend () -> Unit) {
        onClose = block
    }

    fun onError(block: suspend () -> Unit) {
        onError = block
    }

    fun useWebHook(block: WebHook.() -> Unit) {
        webhook = WebHook().apply(block)
    }

    override fun install(yutori: Yutori) {}
    override fun uninstall(yutori: Yutori) {}

    override suspend fun start(yutori: Yutori) {
        val properties = SatoriProperties(host, port, path, token, version)
        connecting = true
        var sequence: Number? = null
        do {
            service = WebSocketEventService(properties, onConnect, onClose, onError, yutori, sequence)
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

    @BuilderMarker
    class WebHook {
        var listen: String = "0.0.0.0"
        var port: Int = 8080
        var path: String = "/"
    }
}