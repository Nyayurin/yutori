@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurn.yutori

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias Listener<T> = suspend (context: Context<T>) -> Unit

abstract class ExtendedListenersContainer {
    abstract operator fun invoke(context: Context<SigningEvent>)
}

@BuilderMarker
class ListenersContainer {
    val any = mutableListOf<Listener<SigningEvent>>()
    val guild = Guild()
    val interaction = Interaction()
    val login = Login()
    val message = Message()
    val reaction = Reaction()
    val friend = Friend()
    val containers = mutableMapOf<String, ExtendedListenersContainer>()

    fun any(listener: suspend Context<SigningEvent>.() -> Unit) = any.add { it.listener() }

    suspend operator fun invoke(context: Context<SigningEvent>) {
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
        val added = mutableListOf<Listener<GuildEvent>>()
        val updated = mutableListOf<Listener<GuildEvent>>()
        val removed = mutableListOf<Listener<GuildEvent>>()
        val request = mutableListOf<Listener<GuildEvent>>()
        val member = Member()
        val role = Role()

        fun added(listener: suspend Context<GuildEvent>.() -> Unit) = added.add { it.listener() }
        fun updated(listener: suspend Context<GuildEvent>.() -> Unit) = updated.add { it.listener() }
        fun removed(listener: suspend Context<GuildEvent>.() -> Unit) = removed.add { it.listener() }
        fun request(listener: suspend Context<GuildEvent>.() -> Unit) = request.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            if (context.event.type !in GuildEvents.Types) {
                member(context)
                role(context)
                return
            }
            context as Context<GuildEvent>
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
            val added = mutableListOf<Listener<GuildMemberEvent>>()
            val updated = mutableListOf<Listener<GuildMemberEvent>>()
            val removed = mutableListOf<Listener<GuildMemberEvent>>()
            val request = mutableListOf<Listener<GuildMemberEvent>>()

            fun added(listener: suspend Context<GuildMemberEvent>.() -> Unit) = added.add { it.listener() }
            fun updated(listener: suspend Context<GuildMemberEvent>.() -> Unit) = updated.add { it.listener() }
            fun removed(listener: suspend Context<GuildMemberEvent>.() -> Unit) = removed.add { it.listener() }
            fun request(listener: suspend Context<GuildMemberEvent>.() -> Unit) = request.add { it.listener() }

            suspend operator fun invoke(context: Context<SigningEvent>) {
                if (context.event.type !in GuildMemberEvents.Types) return
                context as Context<GuildMemberEvent>
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
            val created = mutableListOf<Listener<GuildRoleEvent>>()
            val updated = mutableListOf<Listener<GuildRoleEvent>>()
            val deleted = mutableListOf<Listener<GuildRoleEvent>>()

            fun created(listener: suspend Context<GuildRoleEvent>.() -> Unit) = created.add { it.listener() }
            fun updated(listener: suspend Context<GuildRoleEvent>.() -> Unit) = updated.add { it.listener() }
            fun deleted(listener: suspend Context<GuildRoleEvent>.() -> Unit) = deleted.add { it.listener() }

            suspend operator fun invoke(context: Context<SigningEvent>) {
                if (context.event.type !in GuildRoleEvents.Types) return
                context as Context<GuildRoleEvent>
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
        val button = mutableListOf<Listener<InteractionButtonEvent>>()
        val command = mutableListOf<Listener<InteractionCommandEvent>>()

        fun button(listener: suspend Context<InteractionButtonEvent>.() -> Unit) = button.add { it.listener() }
        fun command(listener: suspend Context<InteractionCommandEvent>.() -> Unit) = command.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            when (context.event.type) {
                InteractionEvents.BUTTON -> coroutineScope {
                    button.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as Context<InteractionButtonEvent>)
                        }
                    }
                }

                InteractionEvents.COMMAND -> coroutineScope {
                    command.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as Context<InteractionCommandEvent>)
                        }
                    }
                }
            }
        }
    }

    @BuilderMarker
    class Login {
        val added = mutableListOf<Listener<LoginEvent>>()
        val removed = mutableListOf<Listener<LoginEvent>>()
        val updated = mutableListOf<Listener<LoginEvent>>()

        fun added(listener: suspend Context<LoginEvent>.() -> Unit) = added.add { it.listener() }
        fun removed(listener: suspend Context<LoginEvent>.() -> Unit) = removed.add { it.listener() }
        fun updated(listener: suspend Context<LoginEvent>.() -> Unit) = updated.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            if (context.event.type !in LoginEvents.Types) return
            context as Context<LoginEvent>
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
        val created = mutableListOf<Listener<MessageEvent>>()
        val updated = mutableListOf<Listener<MessageEvent>>()
        val deleted = mutableListOf<Listener<MessageEvent>>()

        fun created(listener: suspend Context<MessageEvent>.() -> Unit) = created.add { it.listener() }
        fun updated(listener: suspend Context<MessageEvent>.() -> Unit) = updated.add { it.listener() }
        fun deleted(listener: suspend Context<MessageEvent>.() -> Unit) = deleted.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            if (context.event.type !in MessageEvents.Types) return
            context as Context<MessageEvent>
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
        val added = mutableListOf<Listener<ReactionEvent>>()
        val removed = mutableListOf<Listener<ReactionEvent>>()

        fun added(listener: suspend Context<ReactionEvent>.() -> Unit) = added.add { it.listener() }
        fun removed(listener: suspend Context<ReactionEvent>.() -> Unit) = removed.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            if (context.event.type !in ReactionEvents.Types) return
            context as Context<ReactionEvent>
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
        val request = mutableListOf<Listener<UserEvent>>()

        fun request(listener: suspend Context<UserEvent>.() -> Unit) = request.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            when (context.event.type) {
                UserEvents.FRIEND_REQUEST -> coroutineScope {
                    request.forEach {
                        launch(context.yutori.adapter.exceptionHandler) {
                            it(context as Context<UserEvent>)
                        }
                    }
                }
            }
        }
    }
}