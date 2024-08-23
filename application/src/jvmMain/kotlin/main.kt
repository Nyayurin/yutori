import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurn.yutori.app.App
import cn.yurn.yutori.app.DataSaverObject
import com.funny.data_saver.core.DataSaverProperties

fun main() = application {
    Window(
        title = "YutoriApplication",
        state = rememberWindowState(WindowPlacement.Floating),
        onCloseRequest = ::exitApplication,
    ) {
        DataSaverObject.dataSaver = DataSaverProperties("./local_storage")
        App()
    }
}