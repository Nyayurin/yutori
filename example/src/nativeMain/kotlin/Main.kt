import cn.yurn.yutori.example.bot
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    runBlocking {
        bot(args[0])
    }
}