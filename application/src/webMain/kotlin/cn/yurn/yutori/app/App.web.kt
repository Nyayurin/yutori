package cn.yurn.yutori.app

import cn.yurn.yutori.Yutori
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

actual fun platformSatoriAsync(scope: CoroutineScope, yutori: Yutori) {
    scope.launch { yutori.start() }
}