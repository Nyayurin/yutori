package cn.yurn.yutori.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.yurn.yutori.Yutori
import com.funny.data_saver.core.DataSaverPreferences
import kotlinx.coroutines.CoroutineScope

lateinit var context: Context
lateinit var viewModel: MainViewModel

class AppActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        enableEdgeToEdge()
        setContent {
            DataSaverObject.dataSaver = DataSaverPreferences(applicationContext, false)
            navController = rememberNavController()
            viewModel = MainViewModel()
            App(navController, viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val channelId = intent.getStringExtra("channel_id")!!
        navController.popBackStack(route = "connect", inclusive = false)
        navController.navigate("home")
        navController.navigate("chatting/$channelId")
        val chat = viewModel.chats.find { it.id == channelId }
        if (chat != null) {
            viewModel.chats[viewModel.chats.indexOf(chat)] = chat.copy(unread = false)
        }
    }
}

actual fun platformSatoriAsync(scope: CoroutineScope, yutori: Yutori) {
    SatoriService.yutori = yutori
    context.startForegroundService(Intent(context, SatoriService::class.java))
}