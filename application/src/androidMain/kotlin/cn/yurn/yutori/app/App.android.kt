package cn.yurn.yutori.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.funny.data_saver.core.DataSaverPreferences

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataSaverObject.dataSaver = DataSaverPreferences(applicationContext, false)
            App()
        }
    }
}