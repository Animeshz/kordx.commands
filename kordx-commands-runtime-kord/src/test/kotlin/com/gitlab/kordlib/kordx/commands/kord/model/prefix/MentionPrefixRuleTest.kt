package com.gitlab.kordlib.kordx.commands.kord.model.prefix

import com.gitlab.kordlib.kordx.commands.kord.model.processor.KordEventAdapter
import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

internal class MentionPrefixRuleTest {

    @Test
    fun `mention prefix accepts a bot's username mention`() = runBlockingTest {
        val rule = prefix { mention() }

        val event = mockEvent()

        val result = rule.consume("<@${mentionId.value}> test", KordEventAdapter(event))
        val accepted = result as PrefixRule.Result.Accepted

        assertEquals("<@${mentionId.value}> ", accepted.prefix)
    }

    @Test
    fun `mention prefix accepts a bot's nickname mention`() = runBlockingTest {
        val rule = prefix { mention() }

        val event = mockEvent()

        val result = rule.consume("<@!${mentionId.value}> test", KordEventAdapter(event))
        val accepted = result as PrefixRule.Result.Accepted

        assertEquals("<@!${mentionId.value}> ", accepted.prefix)
    }

    @Test
    fun `mention prefix does not accept another mention`() = runBlockingTest {
        val rule = prefix { mention() }

        val event = mockEvent()

        val result = rule.consume("<@!${123456}> test", KordEventAdapter(event))

        assertEquals(PrefixRule.Result.Denied, result)
    }

}
