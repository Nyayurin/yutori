@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.module.yhchat.YhChatProperties
import cn.yurn.yutori.module.yhchat.message.YhChatMessageBuilder
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
    var onStart: suspend YhChatEventService.() -> Unit = { }
    private lateinit var properties: YhChatProperties
    private var service: YhChatEventService? by atomic(null)

    fun onStart(block: suspend YhChatEventService.() -> Unit) {
        onStart = block
    }

    override fun install(yutori: Yutori) {
        properties = YhChatProperties(host, port, path, token, selfId)
        yutori.messageBuilders["yhchat"] = { YhChatMessageBuilder(it) }
        yutori.actionsContainers["yhchat"] = { _, _, _ -> YhChatActions(properties) }
    }

    override fun uninstall(yutori: Yutori) {
        yutori.messageBuilders.remove("yhchat")
        yutori.actionsContainers.remove("yhchat")
    }

    override suspend fun start(yutori: Yutori) {
        service = YhChatEventService(properties, yutori)
        service!!.onStart()
        service!!.connect()
    }

    override fun stop(yutori: Yutori) {
        service?.disconnect()
        service = null
    }
}