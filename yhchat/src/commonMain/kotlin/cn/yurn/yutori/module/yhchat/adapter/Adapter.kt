package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.EventService
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.Satori
import cn.yurn.yutori.module.yhchat.YhChatProperties
import kotlinx.atomicfu.atomic

val Adapter.Companion.YhChat: YhChatAdapter
    get() = YhChatAdapter()

@BuilderMarker
class YhChatAdapter : Adapter(), Reinstallable {
    var host: String = "0.0.0.0"
    var port: Int = 8080
    var path: String = ""
    var token: String = ""
    var selfId: String = ""
    private var service: EventService? by atomic(null)

    override fun install(satori: Satori) {}
    override fun uninstall(satori: Satori) {}

    override suspend fun start(satori: Satori) {
        val properties = YhChatProperties(host, port, path, token, selfId)
        service = YhChatEventService(properties, satori)
        service!!.connect()
    }

    override fun stop(satori: Satori) {
        service?.disconnect()
        service = null
    }
}