package cn.yurn.yutori.app

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.yurn.yutori.Yutori
import cn.yurn.yutori.app.ui.components.ChattingScreen
import cn.yurn.yutori.app.ui.components.ConnectScreen
import cn.yurn.yutori.app.ui.components.HomeScreen
import cn.yurn.yutori.app.ui.theme.YutoriAPPTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    YutoriAPPTheme(viewModel) {
        BoxWithConstraints {
            updateViewModel(viewModel)
            Surface(color = MaterialTheme.colorScheme.background) {
                NavHost(
                    navController = navController,
                    startDestination = "connect",
                    popEnterTransition = {
                        scaleIn(
                            animationSpec = tween(
                                durationMillis = 100,
                                delayMillis = 35,
                            ),
                            initialScale = 1.1F,
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 100,
                                delayMillis = 35,
                            ),
                        )
                    },
                    popExitTransition = {
                        scaleOut(
                            targetScale = 0.9F,
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 35,
                                easing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f),
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(
                        route = "connect"
                    ) {
                        ConnectScreen(navController, viewModel)
                    }
                    composable(
                        route = "home"
                    ) {
                        HomeScreen(navController, viewModel)
                    }
                    composable(
                        route = "chatting/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { backStackEntry ->
                        ChattingScreen(navController, viewModel, backStackEntry.arguments!!.getString("id")!!)
                    }
                }
            }
        }
    }
}

expect fun platformSatoriAsync(scope: CoroutineScope, yutori: Yutori)