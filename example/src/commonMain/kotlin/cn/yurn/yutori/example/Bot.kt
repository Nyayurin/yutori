package cn.yurn.yutori.example

import cn.yurn.yutori.Adapter
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.module.adapter.yhchat.YhChat
import cn.yurn.yutori.satori
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

suspend fun bot(tokenArg: String) {
    Logger.setMinSeverity(Severity.Info)
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
    satori.start()
}