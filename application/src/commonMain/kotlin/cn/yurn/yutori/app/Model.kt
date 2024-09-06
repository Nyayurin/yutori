package cn.yurn.yutori.app

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cn.yurn.yutori.Message
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Yutori

expect class MainViewModel() : ViewModel {
    var ready: Boolean
    var darkMode: Boolean
    var yutori: Yutori?
    var actions: RootActions?
    var self: Chat?
    var chatting: Chat?
    var platform: String
    var selfId: String
    val messages: MutableMap<String, MutableList<Message>>
    val chats: MutableList<Chat>
    var screen: Screen

    fun update()
}

expect class ConnectScreenModel() {
    var host: String
    var port: Int
    var path: String
    var token: String
    var requestChannels: Boolean
}

fun BoxWithConstraintsScope.updateViewModel(viewModel: MainViewModel) {
    viewModel.screen = Screen(maxWidth, maxHeight)
}

data class Screen(val width: Dp, val height: Dp) {
    val size: Pair<ScreenSize, ScreenSize> = when {
        width < 600.dp -> ScreenSize.Compact
        width < 840.dp -> ScreenSize.Medium
        else -> ScreenSize.Expanded
    } to when {
        height < 480.dp -> ScreenSize.Compact
        height < 900.dp -> ScreenSize.Medium
        else -> ScreenSize.Expanded
    }
}

enum class ScreenSize {
    Compact, Medium, Expanded
}