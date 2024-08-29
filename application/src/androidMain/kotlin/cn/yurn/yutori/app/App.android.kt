package cn.yurn.yutori.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.yurn.yutori.Satori
import com.funny.data_saver.core.DataSaverPreferences
import kotlinx.coroutines.CoroutineScope

class AppActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        enableEdgeToEdge()
        setContent {
            DataSaverObject.dataSaver = DataSaverPreferences(applicationContext, false)
            navController = rememberNavController()
            App(navController)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController.popBackStack(route = "connect", inclusive = false)
        navController.navigate("home")
        navController.navigate(NavDeepLinkRequest.Builder.fromUri(intent.data!!).build())
    }

    companion object {
        lateinit var context: Context
    }
}

actual fun platformSatoriAsync(scope: CoroutineScope, satori: Satori) {
    SatoriService.satori = satori
    AppActivity.context.startForegroundService(
        Intent(
            AppActivity.context,
            SatoriService::class.java
        )
    )
}