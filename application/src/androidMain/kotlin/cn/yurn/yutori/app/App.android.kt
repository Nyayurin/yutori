package cn.yurn.yutori.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewModelScope
import cn.yurn.yutori.Adapter
import cn.yurn.yutori.Satori
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.satori
import com.funny.data_saver.core.DataSaverPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        enableEdgeToEdge()
        setContent {
            DataSaverObject.dataSaver = DataSaverPreferences(applicationContext, false)
            App()
        }
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