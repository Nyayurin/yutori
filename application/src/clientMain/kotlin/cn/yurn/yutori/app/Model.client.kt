package cn.yurn.yutori.app

import cafe.adriel.voyager.core.model.ScreenModel
import com.funny.data_saver.core.DataSaverInterface
import com.funny.data_saver.core.mutableDataSaverStateOf

object DataSaverObject {
    lateinit var dataSaver: DataSaverInterface
}

actual class ConnectScreenModel : ScreenModel {
    actual var host by mutableDataSaverStateOf(DataSaverObject.dataSaver, "host", "")
    actual var port by mutableDataSaverStateOf(DataSaverObject.dataSaver, "port", 0)
    actual var path by mutableDataSaverStateOf(DataSaverObject.dataSaver, "path", "")
    actual var token by mutableDataSaverStateOf(DataSaverObject.dataSaver, "token", "")
    actual var requestChannels by mutableDataSaverStateOf(DataSaverObject.dataSaver, "requestChannels", true)
}