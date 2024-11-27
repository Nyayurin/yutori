@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurin.yutori

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias ServerListener<T> = suspend ServerContext<T>.() -> Unit

abstract class ExtendedServerListenersContainer {
    abstract operator fun invoke(context: ServerContext<SigningRequest>)
}

class ServerListenersContainer(
    val any: List<ServerListener<SigningRequest>>,
    val channel: Channel,
    val guild: Guild,
    val login: Login,
    val message: Message,
    val reaction: Reaction,
    val user: User,
    val friend: Friend,
    val containers: Map<String, ExtendedServerListenersContainer>,
) {
    suspend operator fun invoke(context: ServerContext<SigningRequest>) {
        coroutineScope {
            for (listener in any) {
                launch(context.yutori.adapterConfig.exceptionHandler) {
                    listener(context)
                }
            }
        }
        channel(context)
        guild(context)
        login(context)
        message(context)
        reaction(context)
        user(context)
        friend(context)
        for (container in containers.values) container(context)
    }

    class Channel(
        val get: List<ServerListener<ChannelGetRequest>>,
        val list: List<ServerListener<ChannelListRequest>>,
        val create: List<ServerListener<ChannelCreateRequest>>,
        val update: List<ServerListener<ChannelUpdateRequest>>,
        val delete: List<ServerListener<ChannelDeleteRequest>>,
        val mute: List<ServerListener<ChannelMuteRequest>>,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            when (context.request.api) {
                ChannelRequests.GET ->
                    coroutineScope {
                        get.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelGetRequest>)
                            }
                        }
                    }

                ChannelRequests.LIST ->
                    coroutineScope {
                        list.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelListRequest>)
                            }
                        }
                    }

                ChannelRequests.CREATE ->
                    coroutineScope {
                        create.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelCreateRequest>)
                            }
                        }
                    }

                ChannelRequests.UPDATE ->
                    coroutineScope {
                        update.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelUpdateRequest>)
                            }
                        }
                    }

                ChannelRequests.DELETE ->
                    coroutineScope {
                        delete.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelDeleteRequest>)
                            }
                        }
                    }

                ChannelRequests.MUTE ->
                    coroutineScope {
                        mute.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ChannelMuteRequest>)
                            }
                        }
                    }
            }
        }
    }

    class Guild(
        val get: List<ServerListener<GuildGetRequest>>,
        val list: List<ServerListener<GuildListRequest>>,
        val approve: List<ServerListener<GuildApproveRequest>>,
        val member: Member,
        val role: Role,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            if (context.request.api !in GuildRequests.Types) {
                member(context)
                role(context)
                return
            }
            when (context.request.api) {
                GuildRequests.GET ->
                    coroutineScope {
                        get.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<GuildGetRequest>)
                            }
                        }
                    }

                GuildRequests.LIST ->
                    coroutineScope {
                        list.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<GuildListRequest>)
                            }
                        }
                    }

                GuildRequests.APPROVE ->
                    coroutineScope {
                        approve.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<GuildApproveRequest>)
                            }
                        }
                    }
            }
        }

        class Member(
            val get: List<ServerListener<GuildMemberGetRequest>>,
            val list: List<ServerListener<GuildMemberListRequest>>,
            val kick: List<ServerListener<GuildMemberKickRequest>>,
            val mute: List<ServerListener<GuildMemberMuteRequest>>,
            val approve: List<ServerListener<GuildMemberApproveRequest>>,
            val role: Role,
        ) {
            suspend operator fun invoke(context: ServerContext<SigningRequest>) {
                if (context.request.api !in GuildMemberRequests.Types) {
                    role(context)
                    return
                }
                when (context.request.api) {
                    GuildMemberRequests.GET ->
                        coroutineScope {
                            get.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildMemberGetRequest>)
                                }
                            }
                        }

                    GuildMemberRequests.LIST ->
                        coroutineScope {
                            list.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildMemberListRequest>)
                                }
                            }
                        }

                    GuildMemberRequests.KICK ->
                        coroutineScope {
                            kick.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildMemberKickRequest>)
                                }
                            }
                        }

                    GuildMemberRequests.MUTE ->
                        coroutineScope {
                            mute.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildMemberMuteRequest>)
                                }
                            }
                        }

                    GuildMemberRequests.APPROVE ->
                        coroutineScope {
                            approve.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildMemberApproveRequest>)
                                }
                            }
                        }
                }
            }

            class Role(
                val set: List<ServerListener<GuildMemberRoleSetRequest>>,
                val unset: List<ServerListener<GuildMemberRoleUnsetRequest>>,
            ) {
                suspend operator fun invoke(context: ServerContext<SigningRequest>) {
                    when (context.request.api) {
                        GuildMemberRoleRequests.SET ->
                            coroutineScope {
                                set.forEach {
                                    launch(context.yutori.adapterConfig.exceptionHandler) {
                                        it(context as ServerContext<GuildMemberRoleSetRequest>)
                                    }
                                }
                            }

                        GuildMemberRoleRequests.UNSET ->
                            coroutineScope {
                                unset.forEach {
                                    launch(context.yutori.adapterConfig.exceptionHandler) {
                                        it(context as ServerContext<GuildMemberRoleUnsetRequest>)
                                    }
                                }
                            }
                    }
                }
            }
        }

        class Role(
            val list: List<ServerListener<GuildRoleListRequest>>,
            val create: List<ServerListener<GuildRoleCreateRequest>>,
            val update: List<ServerListener<GuildRoleUpdateRequest>>,
            val delete: List<ServerListener<GuildRoleDeleteRequest>>,
        ) {
            suspend operator fun invoke(context: ServerContext<SigningRequest>) {
                when (context.request.api) {
                    GuildRoleRequests.LIST ->
                        coroutineScope {
                            list.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildRoleListRequest>)
                                }
                            }
                        }

                    GuildRoleRequests.CREATE ->
                        coroutineScope {
                            create.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildRoleCreateRequest>)
                                }
                            }
                        }

                    GuildRoleRequests.UPDATE ->
                        coroutineScope {
                            update.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildRoleUpdateRequest>)
                                }
                            }
                        }

                    GuildRoleRequests.DELETE ->
                        coroutineScope {
                            delete.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<GuildRoleDeleteRequest>)
                                }
                            }
                        }
                }
            }
        }
    }

    class Login(
        val get: List<ServerListener<LoginGetRequest>>,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            when (context.request.api) {
                LoginRequests.GET ->
                    coroutineScope {
                        get.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<LoginGetRequest>)
                            }
                        }
                    }
            }
        }
    }

    class Message(
        val create: List<ServerListener<MessageCreateRequest>>,
        val get: List<ServerListener<MessageGetRequest>>,
        val delete: List<ServerListener<MessageDeleteRequest>>,
        val update: List<ServerListener<MessageUpdateRequest>>,
        val list: List<ServerListener<MessageListRequest>>,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            when (context.request.api) {
                MessageRequests.CREATE ->
                    coroutineScope {
                        create.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<MessageCreateRequest>)
                            }
                        }
                    }

                MessageRequests.GET ->
                    coroutineScope {
                        get.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<MessageGetRequest>)
                            }
                        }
                    }

                MessageRequests.DELETE ->
                    coroutineScope {
                        delete.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<MessageDeleteRequest>)
                            }
                        }
                    }

                MessageRequests.UPDATE ->
                    coroutineScope {
                        update.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<MessageUpdateRequest>)
                            }
                        }
                    }

                MessageRequests.LIST ->
                    coroutineScope {
                        list.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<MessageListRequest>)
                            }
                        }
                    }
            }
        }
    }

    class Reaction(
        val create: List<ServerListener<ReactionCreateRequest>>,
        val delete: List<ServerListener<ReactionDeleteRequest>>,
        val clear: List<ServerListener<ReactionClearRequest>>,
        val list: List<ServerListener<ReactionListRequest>>,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            when (context.request.api) {
                ReactionRequests.CREATE ->
                    coroutineScope {
                        create.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ReactionCreateRequest>)
                            }
                        }
                    }

                ReactionRequests.DELETE ->
                    coroutineScope {
                        delete.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ReactionDeleteRequest>)
                            }
                        }
                    }

                ReactionRequests.CLEAR ->
                    coroutineScope {
                        clear.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ReactionClearRequest>)
                            }
                        }
                    }

                ReactionRequests.LIST ->
                    coroutineScope {
                        list.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<ReactionListRequest>)
                            }
                        }
                    }
            }
        }
    }

    class User(
        val get: List<ServerListener<UserGetRequest>>,
        val channel: Channel,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            if (context.request.api !in UserRequests.Types) {
                channel(context)
                return
            }
            when (context.request.api) {
                UserRequests.GET ->
                    coroutineScope {
                        get.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<UserGetRequest>)
                            }
                        }
                    }
            }
        }

        class Channel(
            val create: List<ServerListener<UserChannelCreateRequest>>,
        ) {
            suspend operator fun invoke(context: ServerContext<SigningRequest>) {
                when (context.request.api) {
                    UserChannelRequests.CREATE ->
                        coroutineScope {
                            create.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context as ServerContext<UserChannelCreateRequest>)
                                }
                            }
                        }
                }
            }
        }
    }

    class Friend(
        val list: List<ServerListener<FriendListRequest>>,
        val approve: List<ServerListener<FriendApproveRequest>>,
    ) {
        suspend operator fun invoke(context: ServerContext<SigningRequest>) {
            when (context.request.api) {
                FriendRequests.LIST ->
                    coroutineScope {
                        list.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<FriendListRequest>)
                            }
                        }
                    }

                FriendRequests.APPROVE ->
                    coroutineScope {
                        approve.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as ServerContext<FriendApproveRequest>)
                            }
                        }
                    }
            }
        }
    }
}

abstract class ExtendedServerListenersContainerBuilder {
    abstract fun build(): ExtendedServerListenersContainer
}

@BuilderMarker
class ServerListenersContainerBuilder {
    val any = mutableListOf<ServerListener<SigningRequest>>()
    val channel = Channel()
    val guild = Guild()
    val login = Login()
    val message = Message()
    val reaction = Reaction()
    val user = User()
    val friend = Friend()
    val containers = mutableMapOf<String, ExtendedServerListenersContainerBuilder>()

    fun any(listener: ServerListener<SigningRequest>) = any.add(listener)

    fun build() =
        ServerListenersContainer(
            any = any.toList(),
            channel = channel.build(),
            guild = guild.build(),
            login = login.build(),
            message = message.build(),
            reaction = reaction.build(),
            user = user.build(),
            friend = friend.build(),
            containers = containers.mapValues { it.value.build() },
        )

    @BuilderMarker
    class Channel {
        val get = mutableListOf<ServerListener<ChannelGetRequest>>()
        val list = mutableListOf<ServerListener<ChannelListRequest>>()
        val create = mutableListOf<ServerListener<ChannelCreateRequest>>()
        val update = mutableListOf<ServerListener<ChannelUpdateRequest>>()
        val delete = mutableListOf<ServerListener<ChannelDeleteRequest>>()
        val mute = mutableListOf<ServerListener<ChannelMuteRequest>>()

        fun get(listener: ServerListener<ChannelGetRequest>) = get.add(listener)

        fun list(listener: ServerListener<ChannelListRequest>) = list.add(listener)

        fun create(listener: ServerListener<ChannelCreateRequest>) = create.add(listener)

        fun update(listener: ServerListener<ChannelUpdateRequest>) = update.add(listener)

        fun delete(listener: ServerListener<ChannelDeleteRequest>) = delete.add(listener)

        fun mute(listener: ServerListener<ChannelMuteRequest>) = mute.add(listener)

        fun build() =
            ServerListenersContainer.Channel(
                get = get.toList(),
                list = list.toList(),
                create = create.toList(),
                update = update.toList(),
                delete = delete.toList(),
                mute = mute.toList(),
            )
    }

    @BuilderMarker
    class Guild {
        val get = mutableListOf<ServerListener<GuildGetRequest>>()
        val list = mutableListOf<ServerListener<GuildListRequest>>()
        val approve = mutableListOf<ServerListener<GuildApproveRequest>>()
        val member = Member()
        val role = Role()

        fun get(listener: ServerListener<GuildGetRequest>) = get.add(listener)

        fun list(listener: ServerListener<GuildListRequest>) = list.add(listener)

        fun approve(listener: ServerListener<GuildApproveRequest>) = approve.add(listener)

        fun build() =
            ServerListenersContainer.Guild(
                get = get.toList(),
                list = list.toList(),
                approve = approve.toList(),
                member = member.build(),
                role = role.build(),
            )

        @BuilderMarker
        class Member {
            val get = mutableListOf<ServerListener<GuildMemberGetRequest>>()
            val list = mutableListOf<ServerListener<GuildMemberListRequest>>()
            val kick = mutableListOf<ServerListener<GuildMemberKickRequest>>()
            val mute = mutableListOf<ServerListener<GuildMemberMuteRequest>>()
            val approve = mutableListOf<ServerListener<GuildMemberApproveRequest>>()
            val role = Role()

            fun get(listener: ServerListener<GuildMemberGetRequest>) = get.add(listener)

            fun list(listener: ServerListener<GuildMemberListRequest>) = list.add(listener)

            fun kick(listener: ServerListener<GuildMemberKickRequest>) = kick.add(listener)

            fun mute(listener: ServerListener<GuildMemberMuteRequest>) = mute.add(listener)

            fun approve(listener: ServerListener<GuildMemberApproveRequest>) = approve.add(listener)

            fun build() =
                ServerListenersContainer.Guild.Member(
                    get = get.toList(),
                    list = list.toList(),
                    kick = kick.toList(),
                    mute = mute.toList(),
                    approve = approve.toList(),
                    role = role.build(),
                )

            @BuilderMarker
            class Role {
                val set = mutableListOf<ServerListener<GuildMemberRoleSetRequest>>()
                val unset = mutableListOf<ServerListener<GuildMemberRoleUnsetRequest>>()

                fun set(listener: ServerListener<GuildMemberRoleSetRequest>) = set.add(listener)

                fun unset(listener: ServerListener<GuildMemberRoleUnsetRequest>) = unset.add(listener)

                fun build() =
                    ServerListenersContainer.Guild.Member.Role(
                        set = set.toList(),
                        unset = unset.toList(),
                    )
            }
        }

        @BuilderMarker
        class Role {
            val list = mutableListOf<ServerListener<GuildRoleListRequest>>()
            val create = mutableListOf<ServerListener<GuildRoleCreateRequest>>()
            val update = mutableListOf<ServerListener<GuildRoleUpdateRequest>>()
            val delete = mutableListOf<ServerListener<GuildRoleDeleteRequest>>()

            fun list(listener: ServerListener<GuildRoleListRequest>) = list.add(listener)

            fun create(listener: ServerListener<GuildRoleCreateRequest>) = create.add(listener)

            fun update(listener: ServerListener<GuildRoleUpdateRequest>) = update.add(listener)

            fun delete(listener: ServerListener<GuildRoleDeleteRequest>) = delete.add(listener)

            fun build() =
                ServerListenersContainer.Guild.Role(
                    list = list.toList(),
                    create = create.toList(),
                    update = update.toList(),
                    delete = delete.toList(),
                )
        }
    }

    @BuilderMarker
    class Login {
        val get = mutableListOf<ServerListener<LoginGetRequest>>()

        fun get(listener: ServerListener<LoginGetRequest>) = get.add(listener)

        fun build() =
            ServerListenersContainer.Login(
                get = get.toList(),
            )
    }

    @BuilderMarker
    class Message {
        val create = mutableListOf<ServerListener<MessageCreateRequest>>()
        val get = mutableListOf<ServerListener<MessageGetRequest>>()
        val delete = mutableListOf<ServerListener<MessageDeleteRequest>>()
        val update = mutableListOf<ServerListener<MessageUpdateRequest>>()
        val list = mutableListOf<ServerListener<MessageListRequest>>()

        fun create(listener: ServerListener<MessageCreateRequest>) = create.add(listener)

        fun get(listener: ServerListener<MessageGetRequest>) = get.add(listener)

        fun delete(listener: ServerListener<MessageDeleteRequest>) = delete.add(listener)

        fun update(listener: ServerListener<MessageUpdateRequest>) = update.add(listener)

        fun list(listener: ServerListener<MessageListRequest>) = list.add(listener)

        fun build() =
            ServerListenersContainer.Message(
                create = create.toList(),
                get = get.toList(),
                delete = delete.toList(),
                update = update.toList(),
                list = list.toList(),
            )
    }

    @BuilderMarker
    class Reaction {
        val create = mutableListOf<ServerListener<ReactionCreateRequest>>()
        val delete = mutableListOf<ServerListener<ReactionDeleteRequest>>()
        val clear = mutableListOf<ServerListener<ReactionClearRequest>>()
        val list = mutableListOf<ServerListener<ReactionListRequest>>()

        fun create(listener: ServerListener<ReactionCreateRequest>) = create.add(listener)

        fun delete(listener: ServerListener<ReactionDeleteRequest>) = delete.add(listener)

        fun clear(listener: ServerListener<ReactionClearRequest>) = clear.add(listener)

        fun list(listener: ServerListener<ReactionListRequest>) = list.add(listener)

        fun build() =
            ServerListenersContainer.Reaction(
                create = create.toList(),
                delete = delete.toList(),
                clear = clear.toList(),
                list = list.toList(),
            )
    }

    @BuilderMarker
    class User {
        val get = mutableListOf<ServerListener<UserGetRequest>>()
        val channel = Channel()

        fun get(listener: ServerListener<UserGetRequest>) = get.add(listener)

        fun build() =
            ServerListenersContainer.User(
                get = get.toList(),
                channel = channel.build(),
            )

        @BuilderMarker
        class Channel {
            val create = mutableListOf<ServerListener<UserChannelCreateRequest>>()

            fun create(listener: ServerListener<UserChannelCreateRequest>) = create.add(listener)

            fun build() =
                ServerListenersContainer.User.Channel(
                    create = create.toList(),
                )
        }
    }

    @BuilderMarker
    class Friend {
        val list = mutableListOf<ServerListener<FriendListRequest>>()
        val approve = mutableListOf<ServerListener<FriendApproveRequest>>()

        fun list(listener: ServerListener<FriendListRequest>) = list.add(listener)

        fun approve(listener: ServerListener<FriendApproveRequest>) = approve.add(listener)

        fun build() =
            ServerListenersContainer.Friend(
                list = list.toList(),
                approve = approve.toList(),
            )
    }
}