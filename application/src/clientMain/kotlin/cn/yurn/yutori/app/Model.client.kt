package cn.yurn.yutori.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cn.yurn.yutori.Message
import cn.yurn.yutori.RootActions
import cn.yurn.yutori.Yutori
import com.funny.data_saver.core.DataSaverInterface
import com.funny.data_saver.core.mutableDataSaverStateOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DataSaverObject {
    lateinit var dataSaver: DataSaverInterface
}

actual class ConnectScreenModel {
    actual var host by mutableDataSaverStateOf(DataSaverObject.dataSaver, "host", "")
    actual var port by mutableDataSaverStateOf(DataSaverObject.dataSaver, "port", 0)
    actual var path by mutableDataSaverStateOf(DataSaverObject.dataSaver, "path", "")
    actual var token by mutableDataSaverStateOf(DataSaverObject.dataSaver, "token", "")
    actual var requestChannels by mutableDataSaverStateOf(DataSaverObject.dataSaver, "requestChannels", true)
}

actual class MainViewModel actual constructor() : ViewModel() {
    actual var ready: Boolean = false
    actual var darkMode: Boolean by mutableDataSaverStateOf(DataSaverObject.dataSaver, "darkMode", false)
    actual var yutori: Yutori? by mutableStateOf(null)
    actual var actions: RootActions? by mutableStateOf(null)
    actual var self: Chat? by mutableStateOf(null)
    actual var chatting: Chat? by mutableStateOf(null)
    actual var platform: String by mutableStateOf("")
    actual var selfId: String by mutableStateOf("")
    actual val messages: MutableMap<String, MutableList<Message>> =
        Json.decodeFromString<MutableMap<String, MutableList<Message>>>(DataSaverObject.dataSaver.readData("messages", "{}")).apply {
            this.replaceAll { _, value -> value.toMutableStateList() }
        }
    actual val chats: MutableList<Chat> =
        Json.decodeFromString<MutableList<Chat>>(DataSaverObject.dataSaver.readData("chats", "[]")).toMutableStateList()
    actual var screen: Screen by mutableStateOf(Screen(0.dp, 0.dp))

    actual fun update() {
        DataSaverObject.dataSaver.saveData("messages", Json.encodeToString(messages))
        DataSaverObject.dataSaver.saveData("chats", Json.encodeToString(chats))
    }
}