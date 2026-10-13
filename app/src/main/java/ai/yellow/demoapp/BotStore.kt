package ai.yellow.demoapp

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException

/**
 * On-device profile storage, the equivalent of `UserDefaults` in the iOS
 * tester app. Profiles are stored under `bot-<botId>`.
 */
class BotStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("yellow_bot", Context.MODE_PRIVATE)

    fun getBotList(): List<Bot> =
        prefs.all.entries
            .filter { it.key.startsWith(BOT_PREFIX) && it.value is String }
            .mapNotNull {
                // Skip a corrupt entry rather than breaking the whole list.
                try {
                    Bot.fromJson(it.value as String)
                } catch (e: JSONException) {
                    null
                }
            }
            .sortedByDescending { it.date }

    fun getBot(botId: String): Bot? =
        prefs.getString(BOT_PREFIX + botId, null)?.let { Bot.fromJson(it) }

    fun saveBot(bot: Bot) {
        prefs.edit().putString(BOT_PREFIX + bot.id, bot.toJson()).apply()
    }

    fun deleteBot(botId: String) {
        prefs.edit().remove(BOT_PREFIX + botId).apply()
    }

    var fcmToken: String
        get() = prefs.getString(FCM_TOKEN_KEY, "") ?: ""
        set(value) = prefs.edit().putString(FCM_TOKEN_KEY, value).apply()

    private companion object {
        const val BOT_PREFIX = "bot-"
        const val FCM_TOKEN_KEY = "fcmToken"
    }
}
