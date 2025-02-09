package cn.yurin.yutori

import co.touchlab.kermit.Logger

@Suppress("unused")
abstract class BaseLog(
	private val tag: String,
	private val logger: Logger = Logger
) {
	fun v(throwable: Throwable? = null, message: () -> String) = logger.v(throwable, tag, message)
	fun d(throwable: Throwable? = null, message: () -> String) = logger.d(throwable, tag, message)
	fun i(throwable: Throwable? = null, message: () -> String) = logger.i(throwable, tag, message)
	fun w(throwable: Throwable? = null, message: () -> String) = logger.w(throwable, tag, message)
	fun e(throwable: Throwable? = null, message: () -> String) = logger.e(throwable, tag, message)
	fun a(throwable: Throwable? = null, message: () -> String) = logger.a(throwable, tag, message)
}

internal object Log : BaseLog("yutori")