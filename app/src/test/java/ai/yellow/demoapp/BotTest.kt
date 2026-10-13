package ai.yellow.demoapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BotTest {
    @Test
    fun isValidPayload_acceptsEmptyAndJsonObjects() {
        assertTrue(isValidPayload(""))
        assertTrue(isValidPayload("  "))
        assertTrue(isValidPayload("""{"name": "Asha", "age": 30}"""))
    }

    @Test
    fun isValidPayload_rejectsArraysScalarsAndBrokenJson() {
        assertFalse(isValidPayload("[1, 2]"))
        assertFalse(isValidPayload("\"text\""))
        assertFalse(isValidPayload("""{"name": }"""))
    }

    private val bot = Bot(
        id = "x123",
        name = "Test",
        version = 3,
        activationMode = ActivationMode.VOICE,
        date = 1000,
        payload = """{"city": "Pune", "skip": null}""",
    )

    @Test
    fun bot_roundTripsThroughJson() {
        assertEquals(bot, Bot.fromJson(bot.toJson()))
    }

    @Test
    fun payloadMap_dropsNullsAndIsEmptyWithoutPayload() {
        assertEquals(mapOf("city" to "Pune"), bot.payloadMap())
        assertTrue(bot.copy(payload = "").payloadMap().isEmpty())
    }
}
