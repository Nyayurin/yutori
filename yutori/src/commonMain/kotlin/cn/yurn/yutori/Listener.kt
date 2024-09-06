@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package cn.yurn.yutori

import co.touchlab.kermit.Logger

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

    suspend operator fun invoke(event: Event<SigningEvent>, yutori: Yutori, service: ActionService) {
        try {
            val context = Context(RootActions(event.platform, event.self_id, service, yutori), event, yutori)
            for (listener in this.any) listener(context)
            guild(context)
            interaction(context)
            login(context)
            message(context)
            reaction(context)
            friend(context)
            for (container in containers.values) container(context)
        } catch (e: EventParsingException) {
            Logger.e(yutori.name, e) { "event: $event" }
        }
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
                GuildEvents.Added -> added.forEach { it(context) }
                GuildEvents.Updated -> updated.forEach { it(context) }
                GuildEvents.Removed -> removed.forEach { it(context) }
                GuildEvents.Request -> request.forEach { it(context) }
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
                    GuildMemberEvents.Added -> added.forEach { it(context) }
                    GuildMemberEvents.Updated -> updated.forEach { it(context) }
                    GuildMemberEvents.Removed -> removed.forEach { it(context) }
                    GuildMemberEvents.Request -> request.forEach { it(context) }
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
                    GuildRoleEvents.Created -> created.forEach { it(context) }
                    GuildRoleEvents.Updated -> updated.forEach { it(context) }
                    GuildRoleEvents.Deleted -> deleted.forEach { it(context) }
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
                InteractionEvents.Button -> button.forEach {
                    it(context as Context<InteractionButtonEvent>)
                }

                InteractionEvents.Command -> command.forEach {
                    it(context as Context<InteractionCommandEvent>)
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
                LoginEvents.Added -> added.forEach { it(context) }
                LoginEvents.Removed -> removed.forEach { it(context) }
                LoginEvents.Updated -> updated.forEach { it(context) }
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
                MessageEvents.Created -> created.forEach { it(context) }
                MessageEvents.Updated -> updated.forEach { it(context) }
                MessageEvents.Deleted -> deleted.forEach { it(context) }
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
                ReactionEvents.Added -> added.forEach { it(context) }
                ReactionEvents.Removed -> removed.forEach { it(context) }
            }
        }
    }

    @BuilderMarker
    class Friend {
        val request = mutableListOf<Listener<UserEvent>>()

        fun request(listener: suspend Context<UserEvent>.() -> Unit) = request.add { it.listener() }

        suspend operator fun invoke(context: Context<SigningEvent>) {
            when (context.event.type) {
                UserEvents.Friend_Request -> request.forEach {
                    it(context as Context<UserEvent>)
                }
            }
        }
    }
}