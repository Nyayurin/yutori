package cn.yurn.yutori.app

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.model.ScreenModel
import cn.yurn.yutori.Message
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Satori

class MainViewModel : ViewModel() {
    var ready = false
    var darkMode by mutableStateOf(false)
    var satori: Satori? by mutableStateOf(null)
    var actions: RootActions? by mutableStateOf(null)
    var self: Chat? by mutableStateOf(null)
    var chatting: Chat? by mutableStateOf(null)
    var platform by mutableStateOf("")
    var selfId by mutableStateOf("")
    val messages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    val chats: MutableList<Chat> = mutableStateListOf()
    var screen: Screen by mutableStateOf(Screen(0.dp, 0.dp))
}

class ConnectScreenModel : ScreenModel {
    var host by mutableStateOf("")
    var port by mutableIntStateOf(5500)
    var path by mutableStateOf("")
    var token by mutableStateOf("")
    var requestChannels by mutableStateOf(true)
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