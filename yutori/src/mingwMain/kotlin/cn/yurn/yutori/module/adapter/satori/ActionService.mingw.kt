package cn.yurn.yutori.module.adapter.satori

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.winhttp.WinHttp

actual val platformEngine: HttpClientEngineFactory<*>
    get() = WinHttp