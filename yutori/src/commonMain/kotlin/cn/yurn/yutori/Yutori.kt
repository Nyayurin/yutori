@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurn.yutori

import cn.yurn.yutori.message.ExtendedMessageBuilder
import cn.yurn.yutori.message.MessageBuilder
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.Message
import cn.yurn.yutori.message.element.MessageElementContainer
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun yutori(name: String = "Yutori", block: Yutori.() -> Unit) = Yutori(name).apply(block)

@BuilderMarker
class Yutori(val name: String) {
    val adapter = Adapter()
    val server = Server()
    val modules = mutableListOf<Module>()
    val elements = mutableMapOf<String, MessageElementContainer>()
    val actionsContainers = mutableMapOf<String, (String, String, AdapterActionService) -> Actions>()
    val messageBuilders = mutableMapOf<String, (MessageBuilder) -> ExtendedMessageBuilder>()
    val actionsList = mutableListOf<RootActions>()
    val adapters: List<cn.yurn.yutori.Adapter>
        get() = modules.filterIsInstance<cn.yurn.yutori.Adapter>()
    val servers: List<cn.yurn.yutori.Server>
        get() = modules.filterIsInstance<cn.yurn.yutori.Server>()

    init {
        elements["at"] = At
        elements["sharp"] = Sharp
        elements["a"] = Href
        elements["img"] = Image
        elements["audio"] = Audio
        elements["video"] = Video
        elements["file"] = File
        elements["b"] = Bold
        elements["strong"] = Strong
        elements["i"] = Idiomatic
        elements["em"] = Em
        elements["u"] = Underline
        elements["ins"] = Ins
        elements["s"] = Strikethrough
        elements["del"] = Delete
        elements["spl"] = Spl
        elements["code"] = Code
        elements["sup"] = Sup
        elements["sub"] = Sub
        elements["br"] = Br
        elements["p"] = Paragraph
        elements["message"] = Message
        elements["quote"] = Quote
        elements["author"] = Author
        elements["button"] = Button
    }

    inline fun <reified T : Module> install(module: T, block: T.() -> Unit = {}) {
        if (module !is Reinstallable && modules.filterIsInstance<T>().isNotEmpty()) {
            throw ModuleReinstallException(module.toString())
        }
        module.block()
        module.install(this)
        modules += module
    }

    inline fun <reified T : Module> uninstall() {
        for (module in modules.filterIsInstance<T>()) {
            uninstall(module)
        }
    }

    fun uninstall(module: Module) {
        module.uninstall(this)
        modules -= module
    }

    fun adapter(block: Adapter.() -> Unit) = adapter.block()
    fun server(block: Server.() -> Unit) = server.block()
    suspend fun start() = coroutineScope {
        modules.filterIsInstance<Startable>().forEach { module ->
            launch {
                try {
                    module.start(this@Yutori)
                } catch (e: Exception) {
                    Logger.w(e) { "Module start failed" }
                }
            }
        }
    }

    fun stop() = modules.filterIsInstance<cn.yurn.yutori.Adapter>().forEach { adapter -> adapter.stop(this) }

    inner class Adapter {
        var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Logger.w(name, throwable) { "监听器发生异常" }
        }
        val container = AdapterListenersContainer()
        fun listening(block: AdapterListenersContainer.() -> Unit) = container.block()
    }

    inner class Server {
        var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Logger.w(name, throwable) { "监听器发生异常" }
        }
        val container = ServerListenersContainer()
        fun routing(block: ServerListenersContainer.() -> Unit) = container.block()
    }
}