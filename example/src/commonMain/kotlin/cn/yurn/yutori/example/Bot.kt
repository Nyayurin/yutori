package cn.yurn.yutori.example

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.satori
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

suspend fun bot(tokenArg: String) {
    Logger.setMinSeverity(Severity.Info)
    val satori = satori {
        install(Adapter.Satori) {
            host = "127.0.0.1"
            token = tokenArg
        }
        client {
            listening {
                message.created { HelpCommand(this) }
            }
        }
    }
    satori.start()
}