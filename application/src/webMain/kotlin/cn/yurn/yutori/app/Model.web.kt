package cn.yurn.yutori.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

actual class ConnectScreenModel : ScreenModel {
    actual var host by mutableStateOf("")
    actual var port by mutableStateOf(0)
    actual var path by mutableStateOf("")
    actual var token by mutableStateOf("")
    actual var requestChannels by mutableStateOf(false)
}