package cn.yurn.yutori.module.adapter.satori

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.curl.Curl

actual val platformEngine: HttpClientEngineFactory<*>
    get() = Curl