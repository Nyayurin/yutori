package cn.yurn.yutori.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cn.yurn.yutori.app.ui.components.ConnectScreen
import cn.yurn.yutori.app.ui.theme.YutoriAPPTheme

@Composable
fun App() {
    YutoriAPPTheme {
        BoxWithConstraints(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            updateViewModel(viewModel<MainViewModel>())
            Navigator(ConnectScreen) { navigator ->
                FadeTransition(navigator)
            }
        }
    }
}