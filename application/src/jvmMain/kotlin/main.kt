import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurn.yutori.app.App

fun main() = application {
    Window(
        title = "YutoriApplication",
        state = rememberWindowState(WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}