package ai.yellow.demoapp.ui

import ai.yellow.demoapp.ActivationMode
import ai.yellow.demoapp.Bot
import ai.yellow.demoapp.BotStore
import ai.yellow.demoapp.isValidPayload
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Editable form state; Save builds a [Bot] from it. */
class ProfileFormState(bot: Bot) {
    var id by mutableStateOf(bot.id)
    var name by mutableStateOf(bot.name)
    var version by mutableStateOf(bot.version)
    var activationMode by mutableStateOf(bot.activationMode)
    var initialUserMessage by mutableStateOf(bot.initialUserMessage)
    var enableSpeech by mutableStateOf(bot.enableSpeech)
    var enableLiteVersion by mutableStateOf(bot.enableLiteVersion)
    var ymAuthToken by mutableStateOf(bot.ymAuthToken)
    var apiKey by mutableStateOf(bot.apiKey)
    var enableNotification by mutableStateOf(bot.enableNotification)
    var customBaseURL by mutableStateOf(bot.customBaseURL)
    var payload by mutableStateOf(bot.payload)

    fun toBot() = Bot(
        id = id.trim(),
        name = name.trim(),
        ymAuthToken = ymAuthToken.trim(),
        customBaseURL = customBaseURL.trim(),
        apiKey = apiKey.trim(),
        version = version,
        enableSpeech = enableSpeech,
        enableLiteVersion = enableLiteVersion,
        enableNotification = enableNotification,
        date = System.currentTimeMillis(),
        payload = payload,
        activationMode = activationMode,
        initialUserMessage = initialUserMessage.trim(),
    )
}

/**
 * Add / Edit Profile. v3-only and v1/v2-only fields show based on Version,
 * like the iOS tester app's profile form.
 */
@Composable
fun ProfileScreen(form: ProfileFormState, isEditing: Boolean, onOpenPayload: () -> Unit) {
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(bottom = 32.dp),
    ) {
        FieldRow("Bot Id") {
            TextInput(form.id, { form.id = it }, "x0123456789", enabled = !isEditing)
        }
        FieldRow("Name") { TextInput(form.name, { form.name = it }, "Label for this profile") }
        FieldRow("Version") {
            Segmented(listOf("v1" to 1, "v2" to 2, "v3" to 3), form.version) { form.version = it }
        }
        if (form.version == 3) {
            FieldRow("Activation Mode") {
                Segmented(
                    listOf("chat" to ActivationMode.CHAT, "voice" to ActivationMode.VOICE),
                    form.activationMode,
                ) { form.activationMode = it }
            }
            FieldRow("Initial Message") {
                TextInput(form.initialUserMessage, { form.initialUserMessage = it }, "Optional")
            }
        } else {
            SwitchRow("Enable Speech", form.enableSpeech) { form.enableSpeech = it }
            SwitchRow("Use Lite Version", form.enableLiteVersion) { form.enableLiteVersion = it }
        }
        FieldRow("YMAuthToken") { TextInput(form.ymAuthToken, { form.ymAuthToken = it }, "Optional") }
        FieldRow("Api Key") {
            TextInput(form.apiKey, { form.apiKey = it }, "Needed for Register / Unlink device")
        }
        SwitchRow("Push Notification", form.enableNotification) { form.enableNotification = it }
        FieldRow("Custom Base URL") {
            TextInput(
                form.customBaseURL,
                { form.customBaseURL = it },
                "Leave empty for the default region",
                keyboardType = KeyboardType.Uri,
            )
        }
        ListItem(
            headlineContent = { Text("Payload ›") },
            supportingContent = {
                Text(
                    form.payload.ifEmpty { "Tap to add a JSON payload" },
                    fontFamily = FontFamily.Monospace,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = Modifier.clickable(onClick = onOpenPayload),
        )
    }
}

/** Validates and saves a payload; returns an error message or null. */
fun savePayload(store: BotStore, form: ProfileFormState, text: String): String? {
    if (!isValidPayload(text)) return "Payload not compatible with JSON format"
    // Like the iOS app, persist straight away if the profile already exists.
    store.getBot(form.id.trim())?.let { store.saveBot(it.copy(payload = text)) }
    form.payload = text
    return null
}

/** JSON payload editor. */
@Composable
fun PayloadScreen(text: String, onTextChange: (String) -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().imePadding().padding(16.dp)) {
        Text("JSON object sent to the bot as the session payload.")
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("{\n  \"key\": \"value\"\n}") },
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
        )
    }
}
