package cn.yurn.yutori.example

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.module.satori.adapter.Satori
import cn.yurn.yutori.module.yhchat.adapter.YhChat
import cn.yurn.yutori.satori
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun bot(tokenArg: String) {
    Logger.setMinSeverity(Severity.Debug)
    val satori = satori {
        install(Adapter.Satori) {
            host = "127.0.0.1"
            token = tokenArg
        }
        install(Adapter.YhChat) {
            token = "token"
            selfId = "self_id"
        }
        client {
            listening {
                message.created { HelpCommand(this) }
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