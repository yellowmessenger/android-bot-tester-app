package ai.yellow.demoapp

import android.content.Context
import android.util.Log
import com.yellowmessenger.ymchat.YMChat
import com.yellowmessenger.ymchat.YMConfig
import com.yellowmessenger.ymchat.models.YMActivationMode
import com.yellowmessenger.ymchat.models.YellowCallback

/** Bridges saved profiles to the YMChatbot-Android SDK. */
object Chatbot {
    private val ymChat: YMChat get() = YMChat.getInstance()

    /** Equivalent of `Bot.toYMConfig` in the iOS tester app. */
    private fun configFor(bot: Bot, fcmToken: String): YMConfig =
        YMConfig(bot.id).apply {
            version = bot.version
            showCloseButton = true
            if (bot.version == 3) {
                activationMode = if (bot.activationMode == ActivationMode.VOICE) {
                    YMActivationMode.VOICE
                } else {
                    YMActivationMode.CHAT
                }
                if (bot.initialUserMessage.isNotEmpty()) {
                    initialUserMessage = bot.initialUserMessage
                }
            } else {
                enableSpeech = bot.enableSpeech
                useLiteVersion = bot.enableLiteVersion
            }
            if (bot.ymAuthToken.isNotEmpty()) ymAuthenticationToken = bot.ymAuthToken
            if (bot.enableNotification) deviceToken = fcmToken
            // An empty Custom Base URL keeps the SDK's default region.
            if (bot.customBaseURL.isNotEmpty()) customBaseUrl = bot.customBaseURL
            payload = bot.payloadMap()
        }

    fun startBot(context: Context, bot: Bot, fcmToken: String) {
        ymChat.config = configFor(bot, fcmToken)
        ymChat.startChatbot(context)
    }

    /** Quick Start: bot id only, everything else at SDK defaults. */
    fun quickStart(context: Context, botId: String) {
        ymChat.config = YMConfig(botId)
        ymChat.startChatbot(context)
    }

    fun registerDevice(bot: Bot, fcmToken: String, callback: YellowCallback) {
        val config = configFor(bot, fcmToken)
        ymChat.config = config
        ymChat.registerDevice(bot.apiKey, config, callback)
    }

    fun unlinkDeviceToken(bot: Bot, fcmToken: String, callback: YellowCallback) {
        val config = configFor(bot, fcmToken)
        ymChat.config = config
        ymChat.unlinkDeviceToken(bot.apiKey, config, callback)
    }

    /** Native SDK events: logged, and load failures surfaced in the UI. */
    fun listen(onLoadFailed: () -> Unit) {
        ymChat.onEventFromBot { event -> Log.d(TAG, "event ${event.code} ${event.data}") }
        ymChat.onBotClose { Log.d(TAG, "bot closed") }
        ymChat.onBotLoadFailed { onLoadFailed() }
    }

    private const val TAG = "YMChat"
}
