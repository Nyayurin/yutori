package cn.yurn.yutori.example

import cn.yurn.yutori.Context
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.channel
import cn.yurn.yutori.message

abstract class Command(regex: String) {
    private val pattern = regex.toRegex()
    suspend operator fun invoke(context: Context<MessageEvent>) {
        if (qqHelperFilter(context.event)) return
        if (pattern.matches(context.event.message.content)) onExecute(context)
    }

    abstract suspend fun onExecute(context: Context<MessageEvent>)
}

object HelpCommand : Command("^/[Hh]elp( [\\w\\W]*)?$") {
    override suspend fun onExecute(context: Context<MessageEvent>) {
        val (actions, event) = context
        actions.message.create(
            channel_id = event.channel.id,
            content = {
                quote { id = event.message.id }
                text {
                    """
                    命令前缀:
                        /(左斜线, 同MC的指令前缀)
                        !(英文感叹号)
                    命令列表:
                        help
                    """.trimIndent()
                }
            }
        )
    }
}