import cn.yurn.yutori.example.bot
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        bot(System.getenv("token"))
    }
}