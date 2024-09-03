@file:Suppress("MemberVisibilityCanBePrivate", "HttpUrlsUsage")

package cn.yurn.yutori.module.satori.adapter

import cn.yurn.yutori.*
import cn.yurn.yutori.module.satori.EventSignal
import cn.yurn.yutori.module.satori.Identify
import cn.yurn.yutori.module.satori.IdentifySignal
import cn.yurn.yutori.module.satori.PingSignal
import cn.yurn.yutori.module.satori.PongSignal
import cn.yurn.yutori.module.satori.ReadySignal
import cn.yurn.yutori.module.satori.Signal
import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.*
import korlibs.io.lang.unsupported
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

/**
 * Satori 事件服务的 WebSocket 实现
 * @param properties Satori Server 配置
 * @param satori Satori 配置
 */
class WebSocketEventService(
    val properties: SatoriProperties,
    val onConnect: suspend (List<Login>, SatoriActionService, Satori) -> Unit = { _, _, _ -> },
    val onClose: suspend () -> Unit = { },
    val onError: suspend () -> Unit = { },
    val satori: Satori,
    var sequence: Number? = null
) : EventService {
    private var is_received_pong by atomic(false)
    private val service = SatoriActionService(properties, satori.name)
    private var job by atomic<Job?>(null)

    override suspend fun connect() {
        coroutineScope {
            job = launch {
                val client = HttpClient {
                    install (WebSockets) {
                        contentConverter = KotlinxWebsocketSerializationConverter(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                }
                val name = satori.name
                var connected by atomic(false)
                /*
                 这一段超时断连的代码不知为何无法正常运行
                 具体表现为当 WebSocket Server 关闭时, a 能正常打印, 而 b 无法打印
                 若将下面的 client.webSocket(...) 使用 launch 包起来后会出现新的问题:
                 a, b 都能正常打印, 但 WebSocket 连接永远不成功(具体表现为走到 client.webSocket 后发生阻塞, 触发下面的超时断连)
                 参考下面代码:
                 launch {
                    println("a")
                    delay(1000)
                    println("b")
                    if (!connected) {
                        logger.warn(name, "无法建立 WebSocket 连接: 连接超时")
                    }
                 }
                 launch {
                    println("c")
                    delay(500) // 无论 50 100 500 1000 2000 5000 最终结果都不会有任何区别
                    println("d")
                    client.webSocket(...)
                 }
                 具体情况是 a, b, c, d 都能正常打印, 但走到 client.webSocket 一行后发生阻塞, 导致上一个 launch 内触发超时断连
                 */
                /*launch {
                    println("a")
                    delay(1000)
                    println("b")
                    if (!connected) {
                        logger.warn(name, "无法建立 WebSocket 连接: 连接超时")
                    }
                }*/
                client.webSocket(
                    HttpMethod.Get, properties.host, properties.port, "${properties.path}/${properties.version}/events"
                ) {
                    try {
                        var ready by atomic(false)
                        connected = true
                        sendSerialized(IdentifySignal(Identify(properties.token, sequence)))
                        Logger.i(name) { "成功建立 WebSocket 连接, 尝试建立事件推送服务" }
                        launch {
                            delay(10000)
                            if (!ready) throw TimeoutException("无法建立事件推送服务: READY 响应超时")
                            while (isActive) {
                                sendSerialized(PingSignal())
                                Logger.d(name) { "发送 PING" }
                                delay(10000)
                            }
                            println("Done")
                        }
                        launch {
                            do {
                                is_received_pong = false
                                delay(1000 * 60)
                                if (!is_received_pong) {
                                    throw TimeoutException("WebSocket 连接断开: PONG 响应超时")
                                }
                            } while (isActive)
                        }
                        while (isActive) try {
                            when (val signal = receiveDeserialized<Signal>()) {
                                is ReadySignal -> {
                                    ready = true
                                    onConnect(signal.body.logins, service, satori)
                                    Logger.i(name) { "成功建立事件推送服务: ${signal.body.logins}" }
                                }
                                is EventSignal -> launch { onEvent(signal.body) }
                                is PongSignal -> {
                                    is_received_pong = true
                                    Logger.d(name) { "收到 PONG" }
                                }

                                else -> unsupported("Unsupported signal: $signal")
                            }
                        } catch (e: JsonConvertException) {
                            Logger.w(name, e) { "事件解析错误" }
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException && e.message == "Event service disconnect") {
                            Logger.i(name) { "WebSocket 连接断开: 主动断开连接" }
                        } else {
                            onError()
                            Logger.w(name, e) { "WebSocket 连接断开" }
                        }
                        close()
                    }
                    this@WebSocketEventService.onClose()
                }
                client.close()
            }
        }
    }

    private suspend fun onEvent(event: Event<SigningEvent>) {
        val name = satori.name
        try {
            when (event.type) {
                MessageEvents.Created -> Logger.i(name) { buildString {
                    append("${event.platform}(${event.self_id}) 接收事件(${event.type}): ")
                    append("${event.nullable_channel!!.name}(${event.nullable_channel!!.id})")
                    append("-")
                    append("${event.nick()}(${event.nullable_user!!.id})")
                    append(": ")
                    append(event.nullable_message!!.content)
                } }

                else -> Logger.i(name) { "${event.platform}(${event.self_id}) 接收事件: ${event.type}" }
            }
            Logger.d(name) { "事件详细信息: $event" }
            sequence = event.id
            satori.client.container(event, satori, service)
        } catch (e: Exception) {
            Logger.w(name, e) { "处理事件时出错: $event" }
        }
    }

    override fun disconnect() {
        job?.cancel("Event service disconnect")
    }
}

/**
 * Satori 事件服务的 WebHook 实现
 * @param properties Satori WebHook 配置
 * @param satori Satori 配置
 */
/*class WebhookEventService(
    val listen: String, val port: Int, val path: String, val properties: SatoriProperties, val satori: Satori
) : EventService {
    private var client: ApplicationEngine? = null
    private val service = SatoriActionService(properties, satori.name)

    override suspend fun connect() {
        client = embeddedServer(io.ktor.server.cio.CIO, port, listen) {
            routing {
                post(path) {
                    val authorization = call.request.headers["Authorization"]
                    if (authorization != properties.token) {
                        call.response.status(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    val body = call.receiveText()
                    try {
                        val mapper = jacksonObjectMapper().configure(
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
                        )
                        val event = mapper.readValue<Event<SigningEvent>>(body)
                        launch {
                            when (event.type) {
                                in MessageEvents.Types -> logger.info(satori.name, buildString {
                                    append("${event.platform}(${event.self_id}) 接收事件(${event.type}): ")
                                    append("\u001B[38;5;4m").append(
                                        "${event.nullable_channel!!.name}(${
                                            event.nullable_channel!!.id
                                        })"
                                    )
                                    append("\u001B[38;5;6m")
                                    append(event.nick())
                                    append("(${event.nullable_user!!.id})")
                                    append("\u001B[0m").append(": ").append(event.nullable_message!!.content)
                                })

                                else -> logger.info(
                                    satori.name, "${event.platform}(${event.self_id}) 接收事件: ${event.type}"
                                )
                            }
                            logger.debug(satori.name, "事件详细信息: $body")
                            satori.client.container(event, satori, service)
                        }
                        call.response.status(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        logger.warn(satori.name, "处理事件时出错(${body}): ${e.localizedMessage}")
                        e.printStackTrace()
                        call.response.status(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }.start()
        logger.info(satori.name, "成功启动 HTTP 服务器")
        RootActions.AdminAction(service).webhook.create(
            "http://${properties.host}:${properties.port}${properties.path}", properties.token
        )
    }

    override fun disconnect() {
        RootActions.AdminAction(service).webhook.delete(
            "http://${properties.host}:${properties.port}${properties.path}"
        )
        client?.stop()
    }
}*/
