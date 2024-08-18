package cn.yurn.yutori.module.adapter.satori

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual val platformEngine: HttpClientEngineFactory<*>
    get() = Js