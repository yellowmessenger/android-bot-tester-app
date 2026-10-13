package ai.yellow.demoapp

import ai.yellow.demoapp.ui.ActionSheet
import ai.yellow.demoapp.ui.Alert
import ai.yellow.demoapp.ui.AlertMessage
import ai.yellow.demoapp.ui.InfoScreen
import ai.yellow.demoapp.ui.PayloadScreen
import ai.yellow.demoapp.ui.ProfileListScreen
import ai.yellow.demoapp.ui.ProfileScreen
import ai.yellow.demoapp.ui.QuickStartScreen
import ai.yellow.demoapp.ui.SheetAction
import ai.yellow.demoapp.ui.YellowBotTheme
import ai.yellow.demoapp.ui.ProfileFormState
import ai.yellow.demoapp.ui.savePayload
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.yellowmessenger.ymchat.models.YellowCallback

class MainActivity : ComponentActivity() {
    private lateinit var store: BotStore
    private var alert by mutableStateOf<AlertMessage?>(null)

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = BotStore(this)

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        Push.init(this, store)
        Chatbot.listen(onLoadFailed = {
            showAlert(AlertMessage("Error", "Bot failed to load. Check the bot id and network."))
        })

        setContent {
            YellowBotTheme {
                YellowBotApp(store, ::startBot, ::quickStart, ::registerDevice, ::unlinkDeviceToken, ::showAlert)
                alert?.let { Alert(it) { alert = null } }
            }
        }
    }

    private fun showAlert(message: AlertMessage) {
        // SDK callbacks arrive on background threads.
        Handler(Looper.getMainLooper()).post { alert = message }
    }

    private fun run(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            showAlert(AlertMessage("Error", e.message ?: e.toString()))
        }
    }

    private fun startBot(bot: Bot) = run { Chatbot.startBot(this, bot, store.fcmToken) }

    private fun quickStart(botId: String) = run { Chatbot.quickStart(this, botId) }

    private fun registerDevice(bot: Bot) = run {
        Chatbot.registerDevice(bot, store.fcmToken, callback("Device Registered Successfully"))
    }

    private fun unlinkDeviceToken(bot: Bot) = run {
        Chatbot.unlinkDeviceToken(bot, store.fcmToken, callback("Device Unlinked Successfully"))
    }

    private fun callback(successMessage: String) = object : YellowCallback {
        override fun success() = showAlert(AlertMessage(null, successMessage))
        override fun failure(message: String) = showAlert(AlertMessage("Error", message))
    }
}

private enum class Tab(val title: String, val icon: String) {
    QUICK_START("Quick Start", "⚡"),
    PROFILES("Profiles", "🗂️"),
    INFO("Info", "ℹ️"),
}

/** Screens pushed over the Profiles tab. */
private sealed interface Pushed {
    /** [openedAt] makes every opening a fresh form, so a discarded draft isn't reused. */
    data class Form(val editBotId: String?, val openedAt: Long = System.nanoTime()) : Pushed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YellowBotApp(
    store: BotStore,
    startBot: (Bot) -> Unit,
    quickStart: (String) -> Unit,
    registerDevice: (Bot) -> Unit,
    unlinkDeviceToken: (Bot) -> Unit,
    showAlert: (AlertMessage) -> Unit,
) {
    var tab by remember { mutableStateOf(Tab.QUICK_START) }
    var pushed by remember { mutableStateOf<Pushed?>(null) }
    var showPayload by remember { mutableStateOf(false) }
    var payloadDraft by remember { mutableStateOf("") }
    var sheetFor by remember { mutableStateOf<Bot?>(null) }
    var listVersion by remember { mutableIntStateOf(0) } // bump to reload the list
    val bots = remember(listVersion, pushed) { store.getBotList() }

    val form = (pushed as? Pushed.Form)
    val formBot = remember(form) {
        form?.editBotId?.let { store.getBot(it) } ?: Bot(id = "")
    }
    val formState = remember(form) { ProfileFormState(formBot) }

    BackHandler(enabled = pushed != null && !showPayload) { pushed = null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            showPayload -> "Payload"
                            form != null -> if (form.editBotId != null) "Edit Profile" else "Add Profile"
                            else -> tab.title
                        },
                    )
                },
                navigationIcon = {
                    if (pushed != null) {
                        IconButton(onClick = { if (showPayload) showPayload = false else pushed = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    when {
                        showPayload -> TextButton(onClick = {
                            val error = savePayload(store, formState, payloadDraft)
                            if (error != null) showAlert(AlertMessage("Error", error)) else showPayload = false
                        }) { Text("Save") }

                        form != null -> TextButton(onClick = {
                            val bot = formState.toBot()
                            if (bot.id.isEmpty()) {
                                showAlert(AlertMessage("Error", "Bot ID cannot be empty"))
                            } else {
                                store.saveBot(bot)
                                pushed = null
                            }
                        }) { Text("Save") }

                        tab == Tab.PROFILES -> IconButton(onClick = { pushed = Pushed.Form(null) }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add profile")
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (pushed == null) {
                NavigationBar {
                    Tab.entries.forEach {
                        NavigationBarItem(
                            selected = tab == it,
                            onClick = { tab = it },
                            icon = { Text(it.icon, fontSize = 20.sp) },
                            label = { Text(it.title) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        androidx.compose.foundation.layout.Box(modifier) {
            when {
                showPayload -> PayloadScreen(payloadDraft, { payloadDraft = it }) { showPayload = false }
                form != null -> ProfileScreen(formState, form.editBotId != null) {
                    if (formState.id.isBlank()) {
                        showAlert(AlertMessage(null, "Enter the bot id first"))
                    } else {
                        payloadDraft = formState.payload
                        showPayload = true
                    }
                }
                tab == Tab.QUICK_START -> QuickStartScreen(quickStart, showAlert)
                tab == Tab.PROFILES -> ProfileListScreen(bots) { sheetFor = it }
                tab == Tab.INFO -> InfoScreen()
            }
        }
    }

    sheetFor?.let { bot ->
        val actions = buildList {
            add(SheetAction("🤖 Start Bot") { startBot(bot) })
            // Same visibility rules as the iOS tester app.
            if (bot.apiKey.isNotEmpty() && bot.ymAuthToken.isNotEmpty() && bot.enableNotification) {
                add(SheetAction("Register Device") { registerDevice(bot) })
            }
            if (bot.apiKey.isNotEmpty() && bot.enableNotification) {
                add(SheetAction("Unlink Device Token") { unlinkDeviceToken(bot) })
            }
            add(SheetAction("Edit") { pushed = Pushed.Form(bot.id) })
            add(SheetAction("Delete", destructive = true) {
                store.deleteBot(bot.id)
                listVersion++
            })
        }
        ActionSheet(bot.id, actions) { sheetFor = null }
    }
}
