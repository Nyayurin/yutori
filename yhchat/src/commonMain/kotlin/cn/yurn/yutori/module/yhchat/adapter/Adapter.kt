@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori.module.yhchat.adapter

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.BuilderMarker
import cn.yurn.yutori.Reinstallable
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.module.yhchat.YhChatProperties
import cn.yurn.yutori.module.yhchat.message.YhChatMessageBuilder
import cn.yurn.yutori.module.yhchat.message.element.HTML
import cn.yurn.yutori.module.yhchat.message.element.Markdown
import kotlinx.atomicfu.atomic

fun Adapter.Companion.yhchat(alias: String? = null) = YhChatAdapter(alias)

@BuilderMarker
class YhChatAdapter(alias: String?) : Adapter(alias), Reinstallable {
    var host: String = "0.0.0.0"
    var port: Int = 8080
    var path: String = ""
    var token: String = ""
    var userId: String = ""
    var onStart: suspend YhChatAdapterEventService.() -> Unit = { }
    private lateinit var properties: YhChatProperties
    private var service: YhChatAdapterEventService? by atomic(null)

    fun onStart(block: suspend YhChatAdapterEventService.() -> Unit) {
        onStart = block
    }

    override fun install(yutori: Yutori) {
        properties = YhChatProperties(host, port, path, token, userId)
        yutori.elements["yhchat:markdown"] = Markdown
        yutori.elements["yhchat:html"] = HTML
        yutori.actionsContainers["yhchat"] = { _, _, _ -> YhChatActions(properties) }
        yutori.messageBuilders["yhchat"] = { YhChatMessageBuilder(it) }
    }

    override fun uninstall(yutori: Yutori) {
        yutori.elements -= "yhchat:markdown"
        yutori.elements -= "yhchat:html"
        yutori.actionsContainers -= "yhchat"
        yutori.messageBuilders -= "yhchat"
    }

    override suspend fun start(yutori: Yutori) {
        service = YhChatAdapterEventService(alias, properties, yutori)
        service!!.onStart()
        service!!.connect()
    }

    override fun stop(yutori: Yutori) {
        service?.disconnect()
        service = null
    }
}