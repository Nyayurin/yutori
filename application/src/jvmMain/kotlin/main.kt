import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurn.yutori.app.App
import cn.yurn.yutori.app.DataSaverObject
import com.funny.data_saver.core.DataSaverProperties
import org.jetbrains.compose.resources.painterResource
import yutori.application.generated.resources.Res
import yutori.application.generated.resources.icon

fun main() = application {
    Window(
        title = "YutoriApplication",
        icon = painterResource(Res.drawable.icon),
        state = rememberWindowState(WindowPlacement.Floating),
        onCloseRequest = ::exitApplication,
    ) {
        DataSaverObject.dataSaver = DataSaverProperties("./local_storage")
        App()
    }
}