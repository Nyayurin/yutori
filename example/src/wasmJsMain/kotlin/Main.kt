import cn.yurn.yutori.example.bot
import korlibs.io.lang.Environment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    GlobalScope.launch {
        bot(Environment["token"]!!)
    }
}