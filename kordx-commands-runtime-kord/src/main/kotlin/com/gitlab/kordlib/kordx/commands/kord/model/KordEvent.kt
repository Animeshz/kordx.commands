package com.gitlab.kordlib.kordx.commands.kord.model

import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.annotation.KordUnsafe
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.behavior.reply
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.kord.model.processor.KordEventAdapter
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import com.gitlab.kordlib.rest.builder.message.MessageCreateBuilder

/**
 * Base event functionality from an [event], providing some ease of use members.
 */
interface KordEvent {

    /**
     * The event that was parsed.
     */
    val event: KordEventAdapter

    /**
     * The kord instance that spawned the [event].
     */
    val kord: Kord
        get() = event.kord

    /**
     * The message from the [event].
     */
    val message: MessageBehavior
        get() = event.message

    /**
     * The author of the [message] if present.
     */
    val author: UserBehavior?
        get() = event.author

    /**
     * The channel the [message] was created in.
     */
    val channel: MessageChannelBehavior
        get() = message.channel

    /**
     * The guild the [message] was created in, or null if it was made in a DM.
     */
    @OptIn(KordUnsafe::class, KordExperimental::class)
    val guild: GuildBehavior?
        get() = event.guild

    /**
     * Creates a message in the [KordEvent.channel].
     *
     * @param message The [MessageCreateBuilder.content] of the message.
     */
    suspend fun respond(message: String): Message = event.respond(message)

    /**
     *  Suspends until the user invoking this command enters a message in
     *  the [KordEvent.channel] that is accepted by the given [argument].
     *
     *  ```kotlin
     * command("ban")
     *     invoke {
     *         respond("Specify the user to ban")
     *         val member = read(MemberArgument, { it.message.content == "cancel" }) {
     *             if (it.id != kord.selfId) return@read true
     *
     *             respond("Can't ban myself.")
     *             false
     *         }
     *
     *         member.ban()
     *     }
     * }
     * ```
     * > This function doesn't return until a valid value is entered, which might negatively impact user experience.
     * > If this is a concern, consider using the method overload that accepts an escape filter.
     *
     *  @param filter an additional filter for generated values, ignoring all values that return false.
     *  @return an item [T] generated by the [argument]
     */
    suspend fun <T> read(
            argument: Argument<T, KordEventAdapter>,
            filter: suspend (T) -> Boolean = { true }
    ): T = event.read(argument, filter)

    /**
     *  Suspends until the user invoking this command enters a message in
     *  the [KordEvent.channel] that is accepted by the given [argument].
     *
     *  ```kotlin
     *  command("ban")
     *     invoke {
     *         respond("Specify the user to ban")
     *         val member = read(MemberArgument, { it.message.content == "cancel" }) {
     *             if (it.id != kord.selfId) return@read true
     *
     *             respond("Can't ban myself.")
     *             false
     *         }
     *
     *         member?.ban()
     *     }
     * }
     * ```
     *
     *  @param filter an additional filter for generated values, ignoring all values that return false.
     *  @param escape a filter that stops this function from taking input when returning true,
     *  making this function return `null`.
     *  @return an item [T] generated by the [argument]
     */
    suspend fun <T : Any> read(
            argument: Argument<T, KordEventAdapter>,
            escape: suspend (KordEventAdapter) -> Boolean,
            filter: suspend (T) -> Boolean = { true }
    ): T? = event.read(argument, escape, filter)

}

/**
 * Creates a message in the [KordEvent.channel] configured by the [builder].
 */
suspend inline fun KordEvent.respond(builder: MessageCreateBuilder.() -> Unit): Message {
    return channel.createMessage(builder)
}

/**
 * Creates an embed in the [KordEvent.channel] configured by the [builder].
 */
suspend inline fun KordEvent.respondEmbed(builder: EmbedBuilder.() -> Unit): Message {
    return channel.createEmbed(builder)
}

/**
 * Creates reply to the event [KordEvent.message] configured by the [builder].
 *
 * @see MessageBehavior.reply
 */
suspend inline fun KordEvent.reply(builder: MessageCreateBuilder.() -> Unit): Message {
    return message.reply(builder)
}
