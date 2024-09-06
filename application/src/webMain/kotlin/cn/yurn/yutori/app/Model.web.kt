package cn.yurn.yutori.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cn.yurn.yutori.Message
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Yutori

actual class ConnectScreenModel {
    actual var host by mutableStateOf("")
    actual var port by mutableStateOf(0)
    actual var path by mutableStateOf("")
    actual var token by mutableStateOf("")
    actual var requestChannels by mutableStateOf(false)
}

actual class MainViewModel actual constructor() : ViewModel() {
    actual var ready: Boolean = false
    actual var darkMode: Boolean by mutableStateOf(false)
    actual var yutori: Yutori? by mutableStateOf(null)
    actual var actions: RootActions? by mutableStateOf(null)
    actual var self: Chat? by mutableStateOf(null)
    actual var chatting: Chat? by mutableStateOf(null)
    actual var platform: String by mutableStateOf("")
    actual var selfId: String by mutableStateOf("")
    actual val messages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    actual val chats: MutableList<Chat> = mutableStateListOf()
    actual var screen: Screen by mutableStateOf(Screen(0.dp, 0.dp))

    actual fun update() {}
}