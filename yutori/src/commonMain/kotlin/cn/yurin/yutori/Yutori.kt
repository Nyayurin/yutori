@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori

import cn.yurin.yutori.message.ExtendedMessageBuilder
import cn.yurin.yutori.message.MessageBuilder
import cn.yurin.yutori.message.element.At
import cn.yurin.yutori.message.element.Audio
import cn.yurin.yutori.message.element.Author
import cn.yurin.yutori.message.element.Bold
import cn.yurin.yutori.message.element.Br
import cn.yurin.yutori.message.element.Button
import cn.yurin.yutori.message.element.Code
import cn.yurin.yutori.message.element.Delete
import cn.yurin.yutori.message.element.Em
import cn.yurin.yutori.message.element.File
import cn.yurin.yutori.message.element.Href
import cn.yurin.yutori.message.element.Idiomatic
import cn.yurin.yutori.message.element.Image
import cn.yurin.yutori.message.element.Ins
import cn.yurin.yutori.message.element.Message
import cn.yurin.yutori.message.element.MessageElementContainer
import cn.yurin.yutori.message.element.Paragraph
import cn.yurin.yutori.message.element.Quote
import cn.yurin.yutori.message.element.Sharp
import cn.yurin.yutori.message.element.Spl
import cn.yurin.yutori.message.element.Strikethrough
import cn.yurin.yutori.message.element.Strong
import cn.yurin.yutori.message.element.Sub
import cn.yurin.yutori.message.element.Sup
import cn.yurin.yutori.message.element.Text
import cn.yurin.yutori.message.element.Underline
import cn.yurin.yutori.message.element.Video
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun yutori(
    name: String = "Yutori",
    block: Yutori.() -> Unit,
) = Yutori(name).apply(block)

@BuilderMarker
class Yutori(
    val name: String,
) {
    val adapter = Adapter()
    val server = Server()
    val modules = mutableListOf<Module>()
    val elements = mutableMapOf<String, MessageElementContainer>()
    val actionsContainers =
        mutableMapOf<String, (String, String, AdapterActionService) -> ActionBranch>()
    val messageBuilders = mutableMapOf<String, (MessageBuilder) -> ExtendedMessageBuilder>()
    val actionsList = mutableListOf<ActionRoot>()
    val adapters: List<cn.yurin.yutori.Adapter>
        get() = modules.filterIsInstance<cn.yurin.yutori.Adapter>()
    val servers: List<cn.yurin.yutori.Server>
        get() = modules.filterIsInstance<cn.yurin.yutori.Server>()

    init {
        elements["text"] = Text
        elements["at"] = At
        elements["sharp"] = Sharp
        elements["href"] = Href
        elements["image"] = Image
        elements["audio"] = Audio
        elements["video"] = Video
        elements["file"] = File
        elements["bold"] = Bold
        elements["strong"] = Strong
        elements["idiomatic"] = Idiomatic
        elements["em"] = Em
        elements["underline"] = Underline
        elements["ins"] = Ins
        elements["strikethrough"] = Strikethrough
        elements["delete"] = Delete
        elements["spl"] = Spl
        elements["code"] = Code
        elements["sup"] = Sup
        elements["sub"] = Sub
        elements["br"] = Br.Container
        elements["paragraph"] = Paragraph
        elements["message"] = Message
        elements["quote"] = Quote
        elements["author"] = Author
        elements["button"] = Button
    }

    inline fun <reified T : Module> install(
        module: T,
        block: T.() -> Unit = {},
    ) {
        if (module !is Reinstallable && modules.filterIsInstance<T>().isNotEmpty()) {
            throw ModuleReinstallException(module.toString())
        }
        module.block()
        module.install(this)
        modules += module
    }

    inline fun <reified T : Module> uninstall() {
        for (module in modules.filterIsInstance<T>()) uninstall(module)
    }

    fun uninstall(alias: String? = null) {
        modules.find { it.alias == alias }?.let { uninstall(it) }
    }

    fun uninstall(module: Module) {
        module.uninstall(this)
        modules -= module
    }

    fun adapter(block: Adapter.() -> Unit) = adapter.block()

    fun server(block: Server.() -> Unit) = server.block()

    suspend fun start() =
        coroutineScope {
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

    fun stop() = modules.filterIsInstance<Startable>().forEach { adapter -> adapter.stop(this) }

    inner class Adapter {
        var exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Logger.w(name, throwable) { "监听器发生异常" }
            }
        val container = AdapterListenersContainer()

        fun listening(block: AdapterListenersContainer.() -> Unit) = container.block()
    }

    inner class Server {
        var exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Logger.w(name, throwable) { "监听器发生异常" }
            }
        val container = ServerListenersContainer()

        fun routing(block: ServerListenersContainer.() -> Unit) = container.block()
    }
}
