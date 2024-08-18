import androidx.compose.ui.window.ComposeUIViewController
import cn.yurn.yutori.app.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }