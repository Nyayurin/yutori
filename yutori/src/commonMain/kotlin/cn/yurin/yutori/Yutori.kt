@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yurin.yutori

import cn.yurin.yutori.message.ExtendedMessageBuilder
import cn.yurin.yutori.message.MessageBuilder
import cn.yurin.yutori.message.element.*
import cn.yurin.yutori.message.element.Message
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun yutori(
    name: String = "Yutori",
    block: @BuilderMarker YutoriBuilder.() -> Unit,
) = YutoriBuilder(name).apply(block).build()

class Yutori(
    val name: String,
    val adapterConfig: AdapterConfig,
    val serverConfig: ServerConfig,
    val modules: List<Module>,
    val elements: Map<String, MessageElementContainer>,
    val actionsContainers: Map<String, (String, String, AdapterActionService) -> ActionBranch>,
    val messageBuilders: Map<String, (MessageBuilder) -> ExtendedMessageBuilder>,
    val actionsList: MutableList<ActionRoot>,
) {
    val adapters: List<Adapter>
        get() = modules.filterIsInstance<Adapter>()
    val servers: List<Server>
        get() = modules.filterIsInstance<Server>()

    suspend fun start() =
        coroutineScope {
            modules.filterIsInstance<Startable>().forEach { module ->
                launch {
                    try {
                        module.start(this@Yutori)
                    } catch (e: Exception) {
                        Log.w(e) { "Module start failed" }
                    }
                }
            }
        }

    fun stop() = modules.filterIsInstance<Stopable>().forEach { adapter -> adapter.stop(this) }

    class AdapterConfig(
        val exceptionHandler: CoroutineExceptionHandler,
        val container: AdapterListenersContainer,
    )

    class ServerConfig(
        val exceptionHandler: CoroutineExceptionHandler,
        val container: ServerListenersContainer,
    )
}

class YutoriBuilder(
    private val name: String,
) {
    var adapter = Adapter(name)
    var server = Server(name)
    val modules = mutableListOf<Module>()
    val elements =
        mutableMapOf(
            "text" to Text,
            "at" to At,
            "sharp" to Sharp,
            "href" to Href,
            "image" to Image,
            "audio" to Audio,
            "video" to Video,
            "file" to File,
            "bold" to Bold,
            "strong" to Strong,
            "idiomatic" to Idiomatic,
            "em" to Em,
            "underline" to Underline,
            "ins" to Ins,
            "strikethrough" to Strikethrough,
            "delete" to Delete,
            "spl" to Spl,
            "code" to Code,
            "sup" to Sup,
            "sub" to Sub,
            "br" to Br.Container,
            "paragraph" to Paragraph,
            "message" to Message,
            "quote" to Quote,
            "author" to Author,
            "button" to Button,
        )
    val actionsContainers = mutableMapOf<String, (String, String, AdapterActionService) -> ActionBranch>()
    val messageBuilders = mutableMapOf<String, (MessageBuilder) -> ExtendedMessageBuilder>()
    val actionsList = mutableListOf<ActionRoot>()

    inline fun <reified T : Module> install(module: T) {
        if (module !is Reinstallable && modules.any { it::class == T::class }) {
            throw ModuleReinstallException(module.toString())
        }
        if (module.alias != null && modules.any { it.alias == module.alias }) {
            throw ModuleAliasDuplicateException(module.alias)
        }
        module.install(this)
        modules += module
    }

    inline fun <reified T : Module> uninstall() {
        for (module in modules.filterIsInstance<T>()) {
            uninstall(module)
        }
    }

    fun uninstall(alias: String? = null) {
        modules.find { it.alias == alias }?.let { uninstall(it) }
    }

    fun uninstall(module: Module) {
        module.uninstall(this)
        modules -= module
    }

    fun adapter(block: @BuilderMarker Adapter.() -> Unit) {
        adapter = Adapter(name).apply(block)
    }

    fun server(block: @BuilderMarker Server.() -> Unit) {
        server = Server(name).apply(block)
    }

    fun build() =
        Yutori(
            name = name,
            adapterConfig = adapter.build(),
            serverConfig = server.build(),
            modules = modules.toList(),
            elements = elements.toMap(),
            actionsContainers = actionsContainers.toMap(),
            messageBuilders = messageBuilders.toMap(),
            actionsList = actionsList,
        )

    class Adapter(
        name: String,
    ) {
        var exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.w(throwable) { "Exception in listener: $name" }
            }
        var container = AdapterListenersContainerBuilder()

        fun listening(block: @BuilderMarker AdapterListenersContainerBuilder.() -> Unit) {
            container = AdapterListenersContainerBuilder().apply(block)
        }

        fun build() = Yutori.AdapterConfig(exceptionHandler, container.build())
    }

    class Server(
        name: String,
    ) {
        var exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.w(throwable) { "Exception in listener: $name" }
            }
        var container = ServerListenersContainerBuilder()

        fun routing(block: ServerListenersContainerBuilder.() -> Unit) {
            container = ServerListenersContainerBuilder().apply(block)
        }

        fun build() = Yutori.ServerConfig(exceptionHandler, container.build())
    }
}