@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurin.yutori

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias AdapterListener<T> = @BuilderMarker suspend AdapterContext<T>.() -> Unit

abstract class ExtendedAdapterListenersContainer {
    abstract operator fun invoke(context: AdapterContext<SigningEvent>)
}

class AdapterListenersContainer(
    val any: List<AdapterListener<SigningEvent>>,
    val guild: Guild,
    val interaction: Interaction,
    val login: Login,
    val message: Message,
    val reaction: Reaction,
    val friend: Friend,
    val containers: Map<String, ExtendedAdapterListenersContainer>,
) {
    suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
        coroutineScope {
            for (listener in any) {
                launch(context.yutori.adapterConfig.exceptionHandler) {
                    listener(context)
                }
            }
        }
        guild(context)
        interaction(context)
        login(context)
        message(context)
        reaction(context)
        friend(context)
        for (container in containers.values) container(context)
    }

    class Guild(
        val added: List<AdapterListener<GuildEvent>>,
        val updated: List<AdapterListener<GuildEvent>>,
        val removed: List<AdapterListener<GuildEvent>>,
        val request: List<AdapterListener<GuildEvent>>,
        val member: Member,
        val role: Role,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in GuildEvents.Types) {
                member(context)
                role(context)
                return
            }
            context as AdapterContext<GuildEvent>
            when (context.event.type) {
                GuildEvents.ADDED -> {
                    coroutineScope {
                        added.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                GuildEvents.UPDATED -> {
                    coroutineScope {
                        updated.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                GuildEvents.REMOVED -> {
                    coroutineScope {
                        removed.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                GuildEvents.REQUEST -> {
                    coroutineScope {
                        request.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }

        class Member(
            val added: List<AdapterListener<GuildMemberEvent>>,
            val updated: List<AdapterListener<GuildMemberEvent>>,
            val removed: List<AdapterListener<GuildMemberEvent>>,
            val request: List<AdapterListener<GuildMemberEvent>>,
        ) {
            suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
                if (context.event.type !in GuildMemberEvents.Types) return
                context as AdapterContext<GuildMemberEvent>
                when (context.event.type) {
                    GuildMemberEvents.ADDED -> {
                        coroutineScope {
                            added.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }

                    GuildMemberEvents.UPDATED -> {
                        coroutineScope {
                            updated.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }

                    GuildMemberEvents.REMOVED -> {
                        coroutineScope {
                            removed.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }

                    GuildMemberEvents.REQUEST -> {
                        coroutineScope {
                            request.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }
                }
            }
        }

        class Role(
            val created: List<AdapterListener<GuildRoleEvent>>,
            val updated: List<AdapterListener<GuildRoleEvent>>,
            val deleted: List<AdapterListener<GuildRoleEvent>>,
        ) {
            suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
                if (context.event.type !in GuildRoleEvents.Types) return
                context as AdapterContext<GuildRoleEvent>
                when (context.event.type) {
                    GuildRoleEvents.CREATED -> {
                        coroutineScope {
                            created.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }

                    GuildRoleEvents.UPDATED -> {
                        coroutineScope {
                            updated.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }

                    GuildRoleEvents.DELETED -> {
                        coroutineScope {
                            deleted.forEach {
                                launch(context.yutori.adapterConfig.exceptionHandler) {
                                    it(context)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class Interaction(
        val button: List<AdapterListener<InteractionButtonEvent>>,
        val command: List<AdapterListener<InteractionCommandEvent>>,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            when (context.event.type) {
                InteractionEvents.BUTTON -> {
                    coroutineScope {
                        button.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as AdapterContext<InteractionButtonEvent>)
                            }
                        }
                    }
                }

                InteractionEvents.COMMAND -> {
                    coroutineScope {
                        command.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as AdapterContext<InteractionCommandEvent>)
                            }
                        }
                    }
                }
            }
        }
    }

    class Login(
        val added: List<AdapterListener<LoginEvent>>,
        val removed: List<AdapterListener<LoginEvent>>,
        val updated: List<AdapterListener<LoginEvent>>,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in LoginEvents.Types) return
            context as AdapterContext<LoginEvent>
            when (context.event.type) {
                LoginEvents.ADDED -> {
                    coroutineScope {
                        added.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                LoginEvents.REMOVED -> {
                    coroutineScope {
                        removed.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                LoginEvents.UPDATED -> {
                    coroutineScope {
                        updated.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }
    }

    class Message(
        val created: List<AdapterListener<MessageEvent>>,
        val updated: List<AdapterListener<MessageEvent>>,
        val deleted: List<AdapterListener<MessageEvent>>,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in MessageEvents.Types) return
            context as AdapterContext<MessageEvent>
            when (context.event.type) {
                MessageEvents.CREATED -> {
                    coroutineScope {
                        created.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                MessageEvents.UPDATED -> {
                    coroutineScope {
                        updated.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                MessageEvents.DELETED -> {
                    coroutineScope {
                        deleted.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }
    }

    class Reaction(
        val added: List<AdapterListener<ReactionEvent>>,
        val removed: List<AdapterListener<ReactionEvent>>,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in ReactionEvents.Types) return
            context as AdapterContext<ReactionEvent>
            when (context.event.type) {
                ReactionEvents.ADDED -> {
                    coroutineScope {
                        added.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }

                ReactionEvents.REMOVED -> {
                    coroutineScope {
                        removed.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }
    }

    class Friend(
        val request: List<AdapterListener<UserEvent>>,
    ) {
        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            when (context.event.type) {
                UserEvents.FRIEND_REQUEST -> {
                    coroutineScope {
                        request.forEach {
                            launch(context.yutori.adapterConfig.exceptionHandler) {
                                it(context as AdapterContext<UserEvent>)
                            }
                        }
                    }
                }
            }
        }
    }
}

abstract class ExtendedAdapterListenersContainerBuilder {
    abstract fun build(): ExtendedAdapterListenersContainer
}

class AdapterListenersContainerBuilder {
    val any = mutableListOf<AdapterListener<SigningEvent>>()
    val guild = Guild()
    val interaction = Interaction()
    val login = Login()
    val message = Message()
    val reaction = Reaction()
    val friend = Friend()
    val containers = mutableMapOf<String, ExtendedAdapterListenersContainerBuilder>()

    fun any(listener: AdapterListener<SigningEvent>) = any.add(listener)

    fun build() =
        AdapterListenersContainer(
            any = any.toList(),
            guild = guild.build(),
            interaction = interaction.build(),
            login = login.build(),
            message = message.build(),
            reaction = reaction.build(),
            friend = friend.build(),
            containers = containers.mapValues { it.value.build() },
        )

    class Guild {
        val added = mutableListOf<AdapterListener<GuildEvent>>()
        val updated = mutableListOf<AdapterListener<GuildEvent>>()
        val removed = mutableListOf<AdapterListener<GuildEvent>>()
        val request = mutableListOf<AdapterListener<GuildEvent>>()
        val member = Member()
        val role = Role()

        fun added(listener: AdapterListener<GuildEvent>) = added.add(listener)

        fun updated(listener: AdapterListener<GuildEvent>) = updated.add(listener)

        fun removed(listener: AdapterListener<GuildEvent>) = removed.add(listener)

        fun request(listener: AdapterListener<GuildEvent>) = request.add(listener)

        fun build() =
            AdapterListenersContainer.Guild(
                added = added.toList(),
                updated = updated.toList(),
                removed = removed.toList(),
                request = request.toList(),
                member = member.build(),
                role = role.build(),
            )

        class Member {
            val added = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val updated = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val removed = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val request = mutableListOf<AdapterListener<GuildMemberEvent>>()

            fun added(listener: AdapterListener<GuildMemberEvent>) = added.add(listener)

            fun updated(listener: AdapterListener<GuildMemberEvent>) = updated.add(listener)

            fun removed(listener: AdapterListener<GuildMemberEvent>) = removed.add(listener)

            fun request(listener: AdapterListener<GuildMemberEvent>) = request.add(listener)

            fun build() =
                AdapterListenersContainer.Guild.Member(
                    added = added.toList(),
                    updated = updated.toList(),
                    removed = removed.toList(),
                    request = request.toList(),
                )
        }

        class Role {
            val created = mutableListOf<AdapterListener<GuildRoleEvent>>()
            val updated = mutableListOf<AdapterListener<GuildRoleEvent>>()
            val deleted = mutableListOf<AdapterListener<GuildRoleEvent>>()

            fun created(listener: AdapterListener<GuildRoleEvent>) = created.add(listener)

            fun updated(listener: AdapterListener<GuildRoleEvent>) = updated.add(listener)

            fun deleted(listener: AdapterListener<GuildRoleEvent>) = deleted.add(listener)

            fun build() =
                AdapterListenersContainer.Guild.Role(
                    created = created.toList(),
                    updated = updated.toList(),
                    deleted = deleted.toList(),
                )
        }
    }

    class Interaction {
        val button = mutableListOf<AdapterListener<InteractionButtonEvent>>()
        val command = mutableListOf<AdapterListener<InteractionCommandEvent>>()

        fun button(listener: AdapterListener<InteractionButtonEvent>) = button.add(listener)

        fun command(listener: AdapterListener<InteractionCommandEvent>) = command.add(listener)

        fun build() =
            AdapterListenersContainer.Interaction(
                button = button.toList(),
                command = command.toList(),
            )
    }

    class Login {
        val added = mutableListOf<AdapterListener<LoginEvent>>()
        val removed = mutableListOf<AdapterListener<LoginEvent>>()
        val updated = mutableListOf<AdapterListener<LoginEvent>>()

        fun added(listener: AdapterListener<LoginEvent>) = added.add(listener)

        fun removed(listener: AdapterListener<LoginEvent>) = removed.add(listener)

        fun updated(listener: AdapterListener<LoginEvent>) = updated.add(listener)

        fun build() =
            AdapterListenersContainer.Login(
                added = added.toList(),
                removed = removed.toList(),
                updated = updated.toList(),
            )
    }

    class Message {
        val created = mutableListOf<AdapterListener<MessageEvent>>()
        val updated = mutableListOf<AdapterListener<MessageEvent>>()
        val deleted = mutableListOf<AdapterListener<MessageEvent>>()

        fun created(listener: AdapterListener<MessageEvent>) = created.add(listener)

        fun updated(listener: AdapterListener<MessageEvent>) = updated.add(listener)

        fun deleted(listener: AdapterListener<MessageEvent>) = deleted.add(listener)

        fun build() =
            AdapterListenersContainer.Message(
                created = created.toList(),
                updated = updated.toList(),
                deleted = deleted.toList(),
            )
    }

    class Reaction {
        val added = mutableListOf<AdapterListener<ReactionEvent>>()
        val removed = mutableListOf<AdapterListener<ReactionEvent>>()

        fun added(listener: AdapterListener<ReactionEvent>) = added.add(listener)

        fun removed(listener: AdapterListener<ReactionEvent>) = removed.add(listener)

        fun build() =
            AdapterListenersContainer.Reaction(
                added = added.toList(),
                removed = removed.toList(),
            )
    }

    class Friend {
        val request = mutableListOf<AdapterListener<UserEvent>>()

        fun request(listener: AdapterListener<UserEvent>) = request.add(listener)

        fun build() =
            AdapterListenersContainer.Friend(
                request = request.toList(),
            )
    }
}