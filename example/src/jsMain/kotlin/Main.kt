import cn.yurn.yutori.example.bot
import korlibs.platform.process
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    GlobalScope.launch {
        bot(process.env.token as String)
    }
}