package cn.yurn.yutori.app

import cn.yurn.yutori.Satori
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

actual fun platformSatoriAsync(scope: CoroutineScope, satori: Satori) {
    scope.launch { satori.start() }
}