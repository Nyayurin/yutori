package cn.yurn.yutori.example

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.channel
import cn.yurn.yutori.message
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.module.satori.adapter.Satori
import cn.yurn.yutori.module.yhchat.adapter.YhChat
import cn.yurn.yutori.module.yhchat.message.YhChat
import cn.yurn.yutori.yutori
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun bot(tokenArg: String) {
    Logger.setMinSeverity(Severity.Debug)
    val satori = yutori {
        install(Adapter.Satori) {
            host = "127.0.0.1"
            token = tokenArg
        }
        install(Adapter.YhChat) {
            token = "token"
            selfId = "self_id"
        }
        adapter {
            listening {
                message.created { HelpCommand(this) }
                message.created {
                    if (event.platform == "YhChat" && event.message.content.filterIsInstance<Text>().joinToString("") { it.text } == "test") {
                        actions.message.create(
                            channel_id = event.channel.id,
                            content = {
                                YhChat.markdown {
                                    text { "This is a Markdown message" }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    runBlocking {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            satori.start()
        }.join()
    }
}