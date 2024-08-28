package cn.yurn.yutori.app

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.jetpack.navigatorViewModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cn.yurn.yutori.Satori
import cn.yurn.yutori.app.ui.components.ConnectScreen
import cn.yurn.yutori.app.ui.theme.YutoriAPPTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun App() {
    ProvideNavigatorLifecycleKMPSupport {
        Navigator(ConnectScreen) { navigator ->
            YutoriAPPTheme {
                BoxWithConstraints {
                    updateViewModel(navigatorViewModel<MainViewModel>())
                    Surface(color = MaterialTheme.colorScheme.background) {
                        FadeTransition(navigator)
                    }
                }
            }
        }
    }
}

expect fun platformSatoriAsync(scope: CoroutineScope, satori: Satori)