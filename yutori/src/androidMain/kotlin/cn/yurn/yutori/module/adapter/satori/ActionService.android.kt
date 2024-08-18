package cn.yurn.yutori.module.adapter.satori

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual val platformEngine: HttpClientEngineFactory<*>
    get() = OkHttp