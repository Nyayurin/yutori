package cn.yurn.yutori.module.satori

import cn.yurn.yutori.BidiPagingList
import cn.yurn.yutori.Channel
import cn.yurn.yutori.Event
import cn.yurn.yutori.Guild
import cn.yurn.yutori.GuildMember
import cn.yurn.yutori.GuildRole
import cn.yurn.yutori.Interaction
import cn.yurn.yutori.Login
import cn.yurn.yutori.Message
import cn.yurn.yutori.NumberParsingException
import cn.yurn.yutori.PagingList
import cn.yurn.yutori.SigningEvent
import cn.yurn.yutori.User
import cn.yurn.yutori.yutori
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
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

object SignalSerializer : JsonContentPolymorphicSerializer<Signal>(Signal::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (val op = element.jsonObject["op"]!!.jsonPrimitive.int) {
            Signal.EVENT -> EventSignal.serializer()
            Signal.PING -> PingSignal.serializer()
            Signal.PONG -> PongSignal.serializer()
            Signal.IDENTIFY -> IdentifySignal.serializer()
            Signal.READY -> ReadySignal.serializer()
            else -> throw RuntimeException("Unknown event op: $op")
        }
}

object EventSerializer : KSerializer<Event<SigningEvent>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = mapSerialDescriptor(String.serializer().descriptor, DynamicLookupSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Event<SigningEvent>) {
        encoder.encodeCollection(descriptor, value.properties.entries) { index, (key, value) ->
            if (value == null) return@encodeCollection
            when (value) {
                is Number -> encodeSerializableElement(descriptor, index, NumberSerializer, value)
                is String -> encodeStringElement(descriptor, index, value)
                is Interaction.Argv -> encodeSerializableElement(descriptor, index, InteractionArgvSerializer, value)
                is Interaction.Button -> encodeSerializableElement(descriptor, index, InteractionButtonSerializer, value)
                is Channel -> encodeSerializableElement(descriptor, index, ChannelSerializer, value)
                is Guild -> encodeSerializableElement(descriptor, index, GuildSerializer, value)
                is Login -> encodeSerializableElement(descriptor, index, LoginSerializer, value)
                is GuildMember -> encodeSerializableElement(descriptor, index, GuildMemberSerializer, value)
                is Message -> encodeSerializableElement(descriptor, index, MessageSerializer, value)
                is User -> encodeSerializableElement(descriptor, index, UserSerializer, value)
                is GuildRole -> encodeSerializableElement(descriptor, index, GuildRoleSerializer, value)
                else -> throw UnsupportedOperationException("Unsupported event property: $key = $value")
            }
        }
    }

    override fun deserialize(decoder: Decoder): Event<SigningEvent> {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Event(
            id = json.remove("id")!!.jsonPrimitive.long,
            type = json.remove("type")!!.jsonPrimitive.content,
            platform = json.remove("platform")!!.jsonPrimitive.content,
            self_id = json.remove("self_id")!!.jsonPrimitive.content,
            timestamp = json.remove("timestamp")!!.jsonPrimitive.long,
            argv = json.remove("argv")?.let { Json.decodeFromJsonElement(InteractionArgvSerializer, it) },
            button = json.remove("button")?.let { Json.decodeFromJsonElement(
                InteractionButtonSerializer, it) },
            channel = json.remove("channel")?.let { Json.decodeFromJsonElement(ChannelSerializer, it) },
            guild = json.remove("guild")?.let { Json.decodeFromJsonElement(GuildSerializer, it) },
            login = json.remove("login")?.let { Json.decodeFromJsonElement(LoginSerializer, it) },
            member = json.remove("member")?.let { Json.decodeFromJsonElement(GuildMemberSerializer, it) },
            message = json.remove("message")?.let { Json.decodeFromJsonElement(MessageSerializer, it) },
            operator = json.remove("operator")?.let { Json.decodeFromJsonElement(UserSerializer, it) },
            role = json.remove("role")?.let { Json.decodeFromJsonElement(GuildRoleSerializer, it) },
            user = json.remove("user")?.let { Json.decodeFromJsonElement(UserSerializer, it) },
            pair = json.entries.map { (key, value) -> key to value }.toTypedArray()
        )
    }
}

object InteractionArgvSerializer : KSerializer<Interaction.Argv> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("InteractionArgv") {
        element<String>("name")
        element("arguments", listSerialDescriptor(DynamicLookupSerializer.descriptor))
        element("options", DynamicLookupSerializer.descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Interaction.Argv) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeSerializableElement(descriptor, 1, ListSerializer(DynamicLookupSerializer), value.arguments)
            encodeSerializableElement(descriptor, 2, DynamicLookupSerializer, value.options)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Interaction.Argv {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Interaction.Argv(
            name = json["name"]!!.jsonPrimitive.content,
            arguments = json["arguments"]!!.jsonArray.map { Json.decodeFromJsonElement(
                DynamicLookupSerializer, it) },
            options = Json.decodeFromJsonElement(DynamicLookupSerializer, json["options"]!!)
        )
    }
}

object InteractionButtonSerializer : KSerializer<Interaction.Button> {
    override val descriptor = buildClassSerialDescriptor("InteractionButton") {
        element<String>("id")
    }

    override fun serialize(encoder: Encoder, value: Interaction.Button) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
        }
    }

    override fun deserialize(decoder: Decoder): Interaction.Button {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Interaction.Button(
            id = json["id"]!!.jsonPrimitive.content
        )
    }
}

object ChannelSerializer : KSerializer<Channel> {
    override val descriptor = buildClassSerialDescriptor("Channel") {
        element<String>("id")
        element("type", NumberSerializer.descriptor)
        element<String?>("name")
        element<String?>("parent_id")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Channel) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeSerializableElement(descriptor, 1, NumberSerializer, value.type)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 3, String.serializer(), value.parent_id)
        }
    }

    override fun deserialize(decoder: Decoder): Channel {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Channel(
            id = json["id"]!!.jsonPrimitive.content,
            type = json["type"]!!.jsonPrimitive.int,
            name = json["name"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            parent_id = json["parent_id"]?.run{
                if (this !is JsonNull) jsonPrimitive.content else null
            }
        )
    }
}

object GuildSerializer : KSerializer<Guild> {
    override val descriptor = buildClassSerialDescriptor("Guild") {
        element<String>("id")
        element<String?>("name")
        element<String?>("avatar")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Guild) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.avatar)
        }
    }

    override fun deserialize(decoder: Decoder): Guild {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Guild(
            id = json["id"]!!.jsonPrimitive.content,
            name = json["name"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            avatar = json["avatar"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            }
        )
    }
}

object LoginSerializer : KSerializer<Login> {
    override val descriptor = buildClassSerialDescriptor("Login") {
        element("user", UserSerializer.descriptor)
        element<String?>("self_id")
        element<String?>("platform")
        element("status", NumberSerializer.descriptor)
        element<List<String>>("features")
        element<List<String>>("proxy_urls")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Login) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeNullableSerializableElement(descriptor, 0, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.self_id)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.platform)
            encodeSerializableElement(descriptor, 3, NumberSerializer, value.status)
            encodeSerializableElement(descriptor, 4, ListSerializer(String.serializer()), value.features)
            encodeSerializableElement(descriptor, 5, ListSerializer(String.serializer()), value.proxy_urls)
        }
    }

    override fun deserialize(decoder: Decoder): Login {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return Login(
            user = json["user"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                else null
            },
            self_id = json["self_id"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            platform = json["platform"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            status = json["status"]!!.jsonPrimitive.int,
            features = json["features"]!!.jsonArray.map { it.jsonPrimitive.content },
            proxy_urls = json["proxy_urls"]!!.jsonArray.map { it.jsonPrimitive.content }
        )
    }
}

object GuildMemberSerializer : KSerializer<GuildMember> {
    override val descriptor = buildClassSerialDescriptor("GuildMember") {
        element("user", UserSerializer.descriptor)
        element<String?>("nick")
        element<String?>("avatar")
        element("joined_at", NumberNullableSerializer.descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: GuildMember) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeNullableSerializableElement(descriptor, 0, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.nick)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.avatar)
            encodeNullableSerializableElement(descriptor, 3, NumberSerializer, value.joined_at)
        }
    }

    override fun deserialize(decoder: Decoder): GuildMember {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return GuildMember(
            user = json["user"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                else null
            },
            nick = json["nick"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            avatar = json["avatar"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
            joined_at = json["joined_at"]?.run {
                if (this !is JsonNull) jsonPrimitive.long else null
            },
        )
    }
}

object MessageSerializer : KSerializer<Message> {
    override val descriptor = buildClassSerialDescriptor("Message") {
        element<String>("id")
        element<String>("content")
        element("channel", ChannelSerializer.descriptor)
        element("guild", GuildSerializer.descriptor)
        element("member", GuildMemberSerializer.descriptor)
        element("user", UserSerializer.descriptor)
        element("created_at", NumberNullableSerializer.descriptor)
        element("updated_at", NumberNullableSerializer.descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Message) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeSerializableElement(descriptor, 1, String.serializer(), value.content.joinToString("") { it.serialize() })
            encodeNullableSerializableElement(descriptor, 2, ChannelSerializer, value.channel)
            encodeNullableSerializableElement(descriptor, 3, GuildSerializer, value.guild)
            encodeNullableSerializableElement(descriptor, 4, GuildMemberSerializer, value.member)
            encodeNullableSerializableElement(descriptor, 5, UserSerializer, value.user)
            encodeNullableSerializableElement(descriptor, 6, NumberSerializer, value.created_at)
            encodeNullableSerializableElement(descriptor, 7, NumberSerializer, value.updated_at)
        }
    }

    override fun deserialize(decoder: Decoder): Message {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        val contentXml = json["content"]!!.jsonPrimitive.content
        val content = contentXml.deserialize(yutori { })
        return Message(
            id = json["id"]!!.jsonPrimitive.content,
            content = content,
            channel = json["channel"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(ChannelSerializer, this)
                else null
            },
            guild = json["guild"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(GuildSerializer, this)
                else null
            },
            member = json["member"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(GuildMemberSerializer, this)
                else null
            },
            user = json["user"]?.run {
                if (this !is JsonNull) Json.decodeFromJsonElement(UserSerializer, this)
                else null
            },
            created_at = json["created_at"]?.run {
                if (this !is JsonNull) jsonPrimitive.long else null
            },
            updated_at = json["updated_at"]?.run {
                if (this !is JsonNull) jsonPrimitive.long else null
            }
        )
    }
}

object UserSerializer : KSerializer<User> {
    override val descriptor = buildClassSerialDescriptor("User") {
        element<String>("id")
        element<String?>("name")
        element<String?>("nick")
        element<String?>("avatar")
        element<Boolean?>("is_bot")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: User) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.nick)
            encodeNullableSerializableElement(descriptor, 3, String.serializer(), value.avatar)
            encodeNullableSerializableElement(descriptor, 4, Boolean.serializer(), value.is_bot)
        }
    }

    override fun deserialize(decoder: Decoder): User {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return User(
            id = json["id"]!!.jsonPrimitive.content,
            name = json["name"]?.run {
                 if (this !is JsonNull) jsonPrimitive.content else null
            },
            nick = json["nick"]?.run {
                 if (this !is JsonNull) jsonPrimitive.content else null
            },
            avatar = json["avatar"]?.run {
                 if (this !is JsonNull) jsonPrimitive.content else null
            },
            is_bot = json["is_bot"]?.run {
                 if (this !is JsonNull) jsonPrimitive.boolean else null
            },
        )
    }
}

object GuildRoleSerializer : KSerializer<GuildRole> {
    override val descriptor = buildClassSerialDescriptor("GuildRole") {
        element<String>("id")
        element<String?>("name")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: GuildRole) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.name)
        }
    }

    override fun deserialize(decoder: Decoder): GuildRole {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return GuildRole(
            id = json["id"]!!.jsonPrimitive.content,
            name = json["name"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
        )
    }
}

class PagingListSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<PagingList<T>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("PagingList") {
        element("data", listSerialDescriptor(dataSerializer.descriptor))
        element<String?>("next")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: PagingList<T>) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeSerializableElement(descriptor, 0, ListSerializer(dataSerializer), value.data)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.next)
        }
    }

    override fun deserialize(decoder: Decoder): PagingList<T> {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return PagingList(
            data = Json.decodeFromJsonElement(ListSerializer(dataSerializer), json["data"]!!),
            next = json["next"]?.run {
                if (this !is JsonNull) jsonPrimitive.content else null
            },
        )
    }
}

class BidiPagingListSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<BidiPagingList<T>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("BidiPagingList") {
        element("data", listSerialDescriptor(dataSerializer.descriptor))
        element<String?>("prev")
        element<String?>("next")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: BidiPagingList<T>) {
        encoder.encodeStructure(InteractionArgvSerializer.descriptor) {
            encodeSerializableElement(descriptor, 0, ListSerializer(dataSerializer), value.data)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.prev)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.next)
        }
    }

    override fun deserialize(decoder: Decoder): BidiPagingList<T> {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject.toMutableMap()
        return BidiPagingList(
            data = Json.decodeFromJsonElement(ListSerializer(dataSerializer), json["data"]!!),
            prev = json["prev"]?.run {
                 if (this !is JsonNull) jsonPrimitive.content else null
            },
            next = json["next"]?.run {
                 if (this !is JsonNull) jsonPrimitive.content else null
            },
        )
    }
}

@ExperimentalSerializationApi
object DynamicLookupSerializer : KSerializer<Any> {
    override val descriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any) {
        val actualSerializer =
            encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}