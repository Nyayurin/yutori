package cn.yurn.yutori.module.satori

import cn.yurn.yutori.Event
import cn.yurn.yutori.Login
import cn.yurn.yutori.SigningEvent
import kotlinx.serialization.Serializable

@Serializable(SignalSerializer::class)
sealed class Signal {
    @Serializable(NumberSerializer::class)
    abstract val op: Number

    companion object {
        const val EVENT = 0
        const val PING = 1
        const val PONG = 2
        const val IDENTIFY = 3
        const val READY = 4
    }
}

@Serializable
data class EventSignal(@Serializable(EventSerializer::class) val body: Event<SigningEvent>) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = EVENT
    }
}

@Serializable
class PingSignal : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = PING
    }
}

@Serializable
class PongSignal : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = PONG
    }
}

@Serializable
data class IdentifySignal(val body: Identify) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = IDENTIFY
    }
}

@Serializable
data class ReadySignal(val body: Ready) : Signal() {
    @Serializable(NumberSerializer::class)
    override val op: Number

    init {
        op = READY
    }
}

@Serializable
data class Identify(
    val token: String? = null,
    @Serializable(NumberNullableSerializer::class)
    val sequence: Number? = null
)

@Serializable
data class Ready(val logins: List<@Serializable(LoginSerializer::class) Login>)