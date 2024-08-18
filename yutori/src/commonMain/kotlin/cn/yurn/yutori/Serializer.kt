package cn.yurn.yutori

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializer(Number::class)
object NumberNullableSerializer : KSerializer<Number?> {
    override fun serialize(encoder: Encoder, value: Number?) = runCatching {
        runCatching {
            encoder.encodeInt(value!!.toInt())
        }.getOrElse {
            encoder.encodeLong(value!!.toLong())
        }
    }.getOrElse {
        runCatching {
            encoder.encodeDouble(value!!.toDouble())
        }.getOrElse {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): Number? = runCatching {
        runCatching {
            decoder.decodeInt()
        }.getOrElse {
            decoder.decodeLong()
        }
    }.getOrElse {
        runCatching {
            decoder.decodeDouble()
        }.getOrNull()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(Number::class)
object NumberSerializer : KSerializer<Number> {
    override fun serialize(encoder: Encoder, value: Number) = runCatching {
        runCatching {
            encoder.encodeInt(value.toInt())
        }.getOrElse {
            encoder.encodeLong(value.toLong())
        }
    }.getOrElse {
        runCatching {
            encoder.encodeDouble(value.toDouble())
        }.getOrElse {
            throw NumberParsingException(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): Number = runCatching {
        runCatching {
            decoder.decodeInt()
        }.getOrElse {
            decoder.decodeLong()
        }
    }.getOrElse {
        runCatching {
            decoder.decodeDouble()
        }.getOrElse {
            throw NumberParsingException(decoder.decodeString())
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(Number::class)
object StatusSerializer : KSerializer<Login.Status> {
    override fun serialize(encoder: Encoder, value: Login.Status) {
        encoder.encodeInt(value.number.toInt())
    }

    override fun deserialize(decoder: Decoder): Login.Status =
        Login.Status.entries[decoder.decodeInt()]
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(Number::class)
object TypeSerializer : KSerializer<Channel.Type> {
    override fun serialize(encoder: Encoder, value: Channel.Type) {
        encoder.encodeInt(value.number.toInt())
    }

    override fun deserialize(decoder: Decoder): Channel.Type =
        Channel.Type.entries[decoder.decodeInt()]
}

object SignalSerializer : JsonContentPolymorphicSerializer<Signal>(Signal::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["op"]?.jsonPrimitive?.int) {
            Signal.EVENT -> EventSignal.serializer()
            Signal.PING -> PingSignal.serializer()
            Signal.PONG -> PongSignal.serializer()
            Signal.IDENTIFY -> IdentifySignal.serializer()
            Signal.READY -> ReadySignal.serializer()
            else -> throw RuntimeException()
        }
}

object EventSerializer : KSerializer<Event<SigningEvent>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
        element<JsonElement>("properties")
    }

    override fun serialize(encoder: Encoder, value: Event<SigningEvent>) {
        encoder.encodeCollection(descriptor, value.properties.entries) { index, entry ->

        }
    }

    override fun deserialize(decoder: Decoder): Event<SigningEvent> {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        val id = json.remove("id")!!.jsonPrimitive.long
        val type = json.remove("type")!!.jsonPrimitive.content
        val platform = json.remove("platform")!!.jsonPrimitive.content
        val self_id = json.remove("self_id")!!.jsonPrimitive.content
        val timestamp = json.remove("timestamp")!!.jsonPrimitive.long
        val argv = json.remove("argv")?.let { Json.decodeFromJsonElement<Interaction.Argv>(it) }
        val button = json.remove("button")?.let { Json.decodeFromJsonElement<Interaction.Button>(it) }
        val channel = json.remove("channel")?.let { Json.decodeFromJsonElement<Channel>(it) }
        val guild = json.remove("guild")?.let { Json.decodeFromJsonElement<Guild>(it) }
        val login = json.remove("login")?.let { Json.decodeFromJsonElement<Login>(it) }
        val member = json.remove("member")?.let { Json.decodeFromJsonElement<GuildMember>(it) }
        val message = json.remove("message")?.let { Json.decodeFromJsonElement<Message>(it) }
        val operator = json.remove("operator")?.let { Json.decodeFromJsonElement<User>(it) }
        val role = json.remove("role")?.let { Json.decodeFromJsonElement<GuildRole>(it) }
        val user = json.remove("user")?.let { Json.decodeFromJsonElement<User>(it) }

        return Event(
            id = id,
            type = type,
            platform = platform,
            self_id = self_id,
            timestamp = timestamp,
            argv = argv,
            button = button,
            channel = channel,
            guild = guild,
            login = login,
            member = member,
            message = message,
            operator = operator,
            role = role,
            user = user,
            pair = json.entries.map { (key, value) -> key to value }.toTypedArray()
        )
    }
}

@ExperimentalSerializationApi
object DynamicLookupSerializer: KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any) {
        val actualSerializer = encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}