package ai.yellow.demoapp

import org.json.JSONException
import org.json.JSONObject

enum class ActivationMode(val value: String) { CHAT("chat"), VOICE("voice") }

/**
 * A saved bot profile. Mirrors `struct Bot` in the iOS tester app
 * (BotModel.swift) and uses the same JSON keys as the React Native, Flutter
 * and Cordova apps.
 */
data class Bot(
    val id: String,
    val name: String = "",
    val ymAuthToken: String = "",
    val customBaseURL: String = "",
    val apiKey: String = "",
    val version: Int = 1, // 1, 2 or 3
    val enableSpeech: Boolean = false,
    val enableLiteVersion: Boolean = false,
    val enableNotification: Boolean = false,
    val date: Long = System.currentTimeMillis(), // sorts the list, newest first
    val payload: String = "", // raw JSON text, validated in the payload editor
    val activationMode: ActivationMode = ActivationMode.CHAT,
    val initialUserMessage: String = "",
) {
    fun toJson(): String = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("ymAuthToken", ymAuthToken)
        .put("customBaseURL", customBaseURL)
        .put("apiKey", apiKey)
        .put("version", version)
        .put("enableSpeech", enableSpeech)
        .put("enableLiteVersion", enableLiteVersion)
        .put("enableNotification", enableNotification)
        .put("date", date)
        .put("payload", payload)
        .put("activationMode", activationMode.value)
        .put("initialUserMessage", initialUserMessage)
        .toString()

    /** The payload as a map for the SDK. Empty when no payload is set. */
    fun payloadMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        if (payload.isBlank()) return map
        val json = JSONObject(payload)
        for (key in json.keys()) {
            if (!json.isNull(key)) map[key] = json.get(key)
        }
        return map
    }

    companion object {
        fun fromJson(text: String): Bot {
            val json = JSONObject(text)
            return Bot(
                id = json.getString("id"),
                name = json.optString("name"),
                ymAuthToken = json.optString("ymAuthToken"),
                customBaseURL = json.optString("customBaseURL"),
                apiKey = json.optString("apiKey"),
                version = json.optInt("version", 1),
                enableSpeech = json.optBoolean("enableSpeech"),
                enableLiteVersion = json.optBoolean("enableLiteVersion"),
                enableNotification = json.optBoolean("enableNotification"),
                date = json.optLong("date"),
                payload = json.optString("payload"),
                activationMode = if (json.optString("activationMode") == "voice") {
                    ActivationMode.VOICE
                } else {
                    ActivationMode.CHAT
                },
                initialUserMessage = json.optString("initialUserMessage"),
            )
        }
    }
}

/** A payload must be empty or a JSON object. */
fun isValidPayload(text: String): Boolean {
    if (text.isBlank()) return true
    return try {
        JSONObject(text)
        true
    } catch (e: JSONException) {
        false
    }
}
