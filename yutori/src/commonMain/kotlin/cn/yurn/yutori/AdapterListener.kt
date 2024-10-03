@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurn.yutori

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias AdapterListener<T> = suspend AdapterContext<T>.() -> Unit

abstract class ExtendedAdapterListenersContainer {
    abstract operator fun invoke(context: AdapterContext<SigningEvent>)
}

@BuilderMarker
class AdapterListenersContainer {
    val any = mutableListOf<AdapterListener<SigningEvent>>()
    val guild = Guild()
    val interaction = Interaction()
    val login = Login()
    val message = Message()
    val reaction = Reaction()
    val friend = Friend()
    val containers = mutableMapOf<String, ExtendedAdapterListenersContainer>()

    fun any(listener: suspend AdapterContext<SigningEvent>.() -> Unit) = any.add { listener() }

    suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
        coroutineScope {
            for (listener in any) {
                launch(context.yutori.adapter.exceptionHandler) {
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

    @BuilderMarker
    class Guild {
        val added = mutableListOf<AdapterListener<GuildEvent>>()
        val updated = mutableListOf<AdapterListener<GuildEvent>>()
        val removed = mutableListOf<AdapterListener<GuildEvent>>()
        val request = mutableListOf<AdapterListener<GuildEvent>>()
        val member = Member()
        val role = Role()

        fun added(listener: suspend AdapterContext<GuildEvent>.() -> Unit) = added.add { listener() }
        fun updated(listener: suspend AdapterContext<GuildEvent>.() -> Unit) = updated.add { listener() }
        fun removed(listener: suspend AdapterContext<GuildEvent>.() -> Unit) = removed.add { listener() }
        fun request(listener: suspend AdapterContext<GuildEvent>.() -> Unit) = request.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in GuildEvents.Types) {
                member(context)
                role(context)
                return
            }
            context as AdapterContext<GuildEvent>
            when (context.event.type) {
                GuildEvents.ADDED -> coroutineScope {
                    added.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                GuildEvents.UPDATED -> coroutineScope {
                    updated.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                GuildEvents.REMOVED -> coroutineScope {
                    removed.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                GuildEvents.REQUEST -> coroutineScope {
                    request.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
            }
        }

        @BuilderMarker
        class Member {
            val added = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val updated = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val removed = mutableListOf<AdapterListener<GuildMemberEvent>>()
            val request = mutableListOf<AdapterListener<GuildMemberEvent>>()

            fun added(listener: suspend AdapterContext<GuildMemberEvent>.() -> Unit) = added.add { listener() }
            fun updated(listener: suspend AdapterContext<GuildMemberEvent>.() -> Unit) = updated.add { listener() }
            fun removed(listener: suspend AdapterContext<GuildMemberEvent>.() -> Unit) = removed.add { listener() }
            fun request(listener: suspend AdapterContext<GuildMemberEvent>.() -> Unit) = request.add { listener() }

            suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
                if (context.event.type !in GuildMemberEvents.Types) return
                context as AdapterContext<GuildMemberEvent>
                when (context.event.type) {
                    GuildMemberEvents.ADDED -> coroutineScope {
                        added.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                    GuildMemberEvents.UPDATED -> coroutineScope {
                        updated.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                    GuildMemberEvents.REMOVED -> coroutineScope {
                        removed.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                    GuildMemberEvents.REQUEST -> coroutineScope {
                        request.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }

        @BuilderMarker
        class Role {
            val created = mutableListOf<AdapterListener<GuildRoleEvent>>()
            val updated = mutableListOf<AdapterListener<GuildRoleEvent>>()
            val deleted = mutableListOf<AdapterListener<GuildRoleEvent>>()

            fun created(listener: suspend AdapterContext<GuildRoleEvent>.() -> Unit) = created.add { listener() }
            fun updated(listener: suspend AdapterContext<GuildRoleEvent>.() -> Unit) = updated.add { listener() }
            fun deleted(listener: suspend AdapterContext<GuildRoleEvent>.() -> Unit) = deleted.add { listener() }

            suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
                if (context.event.type !in GuildRoleEvents.Types) return
                context as AdapterContext<GuildRoleEvent>
                when (context.event.type) {
                    GuildRoleEvents.CREATED -> coroutineScope {
                        created.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                    GuildRoleEvents.UPDATED -> coroutineScope {
                        updated.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                    GuildRoleEvents.DELETED -> coroutineScope {
                        deleted.forEach {
                            launch(context.yutori.adapter.exceptionHandler) {
                                it(context)
                            }
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Interaction {
        val button = mutableListOf<AdapterListener<InteractionButtonEvent>>()
        val command = mutableListOf<AdapterListener<InteractionCommandEvent>>()

        fun button(listener: suspend AdapterContext<InteractionButtonEvent>.() -> Unit) = button.add { listener() }
        fun command(listener: suspend AdapterContext<InteractionCommandEvent>.() -> Unit) = command.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            when (context.event.type) {
                InteractionEvents.BUTTON -> coroutineScope {
                    button.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as AdapterContext<InteractionButtonEvent>)
                        }
                    }
                }

                InteractionEvents.COMMAND -> coroutineScope {
                    command.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as AdapterContext<InteractionCommandEvent>)
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Login {
        val added = mutableListOf<AdapterListener<LoginEvent>>()
        val removed = mutableListOf<AdapterListener<LoginEvent>>()
        val updated = mutableListOf<AdapterListener<LoginEvent>>()

        fun added(listener: suspend AdapterContext<LoginEvent>.() -> Unit) = added.add { listener() }
        fun removed(listener: suspend AdapterContext<LoginEvent>.() -> Unit) = removed.add { listener() }
        fun updated(listener: suspend AdapterContext<LoginEvent>.() -> Unit) = updated.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in LoginEvents.Types) return
            context as AdapterContext<LoginEvent>
            when (context.event.type) {
                LoginEvents.ADDED -> coroutineScope {
                    added.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                LoginEvents.REMOVED -> coroutineScope {
                    removed.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                LoginEvents.UPDATED -> coroutineScope {
                    updated.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Message {
        val created = mutableListOf<AdapterListener<MessageEvent>>()
        val updated = mutableListOf<AdapterListener<MessageEvent>>()
        val deleted = mutableListOf<AdapterListener<MessageEvent>>()

        fun created(listener: suspend AdapterContext<MessageEvent>.() -> Unit) = created.add { listener() }
        fun updated(listener: suspend AdapterContext<MessageEvent>.() -> Unit) = updated.add { listener() }
        fun deleted(listener: suspend AdapterContext<MessageEvent>.() -> Unit) = deleted.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in MessageEvents.Types) return
            context as AdapterContext<MessageEvent>
            when (context.event.type) {
                MessageEvents.CREATED -> coroutineScope {
                    created.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                MessageEvents.UPDATED -> coroutineScope {
                    updated.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                MessageEvents.DELETED -> coroutineScope {
                    deleted.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Reaction {
        val added = mutableListOf<AdapterListener<ReactionEvent>>()
        val removed = mutableListOf<AdapterListener<ReactionEvent>>()

        fun added(listener: suspend AdapterContext<ReactionEvent>.() -> Unit) = added.add { listener() }
        fun removed(listener: suspend AdapterContext<ReactionEvent>.() -> Unit) = removed.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            if (context.event.type !in ReactionEvents.Types) return
            context as AdapterContext<ReactionEvent>
            when (context.event.type) {
                ReactionEvents.ADDED -> coroutineScope {
                    added.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
                ReactionEvents.REMOVED -> coroutineScope {
                    removed.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context)
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Friend {
        val request = mutableListOf<AdapterListener<UserEvent>>()

        fun request(listener: suspend AdapterContext<UserEvent>.() -> Unit) = request.add { listener() }

        suspend operator fun invoke(context: AdapterContext<SigningEvent>) {
            when (context.event.type) {
                UserEvents.FRIEND_REQUEST -> coroutineScope {
                    request.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as AdapterContext<UserEvent>)
                        }
                    }
                }
            }
        }
    }
}