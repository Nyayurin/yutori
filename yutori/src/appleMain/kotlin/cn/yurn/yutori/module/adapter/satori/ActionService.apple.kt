package cn.yurn.yutori.module.adapter.satori

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val platformEngine: HttpClientEngineFactory<*>
    get() = Darwin