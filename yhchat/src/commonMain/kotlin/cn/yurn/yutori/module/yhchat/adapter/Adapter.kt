package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.EventService
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.module.yhchat.YhChatProperties
import cn.yurn.yutori.module.yhchat.message.YhChatMessageBuilder
import cn.yurn.yutori.module.yhchat.message.element.HTML
import cn.yurn.yutori.module.yhchat.message.element.Markdown
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
    private lateinit var properties: YhChatProperties
    private var service: EventService? by atomic(null)

    override fun install(yutori: Yutori) {
        properties = YhChatProperties(host, port, path, token, selfId)
        yutori.message_builders["yhchat"] = { YhChatMessageBuilder(it) }
        yutori.elements["yhchat:markdown"] = Markdown
        yutori.elements["yhchat:html"] = HTML
        yutori.actions_containers["yhchat"] = { _, _, _ -> YhChatActions(properties) }
    }
    override fun uninstall(yutori: Yutori) {
        yutori.message_builders.remove("yhchat")
        yutori.elements.remove("yhchat:markdown")
        yutori.elements.remove("yhchat:html")
        yutori.actions_containers.remove("yhchat")
    }

    override suspend fun start(yutori: Yutori) {
        service = YhChatEventService(properties, yutori)
        service!!.connect()
    }

    override fun stop(yutori: Yutori) {
        service?.disconnect()
        service = null
    }
}