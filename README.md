# Yellow Bot Android (Native Android Bot Tester App)

Native Android port of the iOS Bot Tester App. It tests bots through the [YMChatbot-Android](https://github.com/yellowmessenger/YMChatbot-Android) SDK directly, with no cross-platform plugin in between. Use it to try v1, v2 and v3 widgets without embedding the SDK into a real host app first.

| | Version |
|---|---|
| YMChatbot-Android | 3.5.5 (JitPack) |
| Language / UI | Kotlin, Jetpack Compose (Material 3) |
| Android Gradle plugin / Gradle | 9.1.0 / 9.3.1 (wrapper) |
| applicationId | `ai.yellow.demoapp` |
| Minimum / target SDK | 24 (Android 7.0) / 36 |

## Requirements

- JDK 17
- Android SDK with platform 36, easiest via Android Studio
- An emulator or device running Android 7.0+

## Setup and running

Open the folder in Android Studio and press ▶️ Run, or from the command line:

```bash
./gradlew installDebug        # builds and installs on the running emulator/device
./gradlew testDebugUnitTest   # unit tests for the profile model
```

If Gradle can't find the SDK, create `local.properties` with `sdk.dir=/path/to/Android/sdk`. Android Studio does this automatically.

### Firebase (push notifications only)

The app works without Firebase. It's only needed to get an FCM token for **Push Notification**, **Register Device** and **Unlink Device Token**.

Put the app's `google-services.json` (package `ai.yellow.demoapp`) in `app/`. The Google Services Gradle plugin is applied only when that file exists, and it's gitignored.

## Testing a bot

The app mirrors the iOS tester app: three tabs, same fields, same behaviour.

### Quick Start

Enter a **Bot Id** and tap **Start Chat Bot**. Launches the bot with SDK defaults (v1 widget, no auth token, no payload). Nothing is saved.

### Profiles

Saved bot configs, stored in `SharedPreferences` (the Android equivalent of `UserDefaults`).

1. Tap **+** to add a profile, or tap an existing one and choose **Edit**.
2. Fill in the fields:

   | Field | Notes |
   |---|---|
   | Bot Id | Required. Can't be changed after the profile is created. |
   | Name | Label shown in the list. |
   | Version | `v1`, `v2` or `v3` widget. |
   | Activation Mode | **v3 only.** `chat` or `voice`. |
   | Initial Message | **v3 only.** Optional first user message. |
   | Enable Speech | **v1/v2 only.** |
   | Use Lite Version | **v1/v2 only.** |
   | YMAuthToken | Optional auth token. |
   | Api Key | Needed for Register Device / Unlink Device Token. |
   | Push Notification | Passes the FCM token to the SDK as the device token. |
   | Custom Base URL | Leave empty for the default region (R0). |
   | Payload | JSON object sent with the session. Validated before saving. |

3. Tap **Save**. Tap the profile to open the action sheet:
   - **🤖 Start Bot**
   - **Register Device**: shown when Api Key, YMAuthToken and Push Notification are all set
   - **Unlink Device Token**: shown when Api Key and Push Notification are set
   - **Edit** / **Delete**

### Info

Shows the app version, the YMChatbot-Android version and the Android version.

## Where things live

| Path | What |
|---|---|
| `app/build.gradle.kts` | SDK version (`ymChatVersion`), dependencies, optional Google Services plugin |
| `app/src/main/java/ai/yellow/demoapp/MainActivity.kt` | Navigation, action sheet, permissions, SDK calls and alerts |
| `.../Chatbot.kt` | Profile → `YMConfig` (equivalent of `Bot.toYMConfig`), start / register / unlink, event listeners |
| `.../Bot.kt` | Profile model, JSON, payload validation |
| `.../BotStore.kt` | Profile persistence |
| `.../Push.kt` | FCM token (when Firebase is configured) |
| `.../ui/` | Compose screens: Quick Start, Profiles, Profile form + payload editor, Info |
| `app/src/test/` | Unit tests for the profile model |

## Notes

- Each start builds a fresh `YMConfig`, so nothing (payload included) carries over from the previous bot.
- `YMBotCloseEvent` fires twice per close. That's native SDK behaviour; this app calls the SDK directly, and the React Native, Flutter and Cordova apps see the same.
- To try another SDK version, change `ymChatVersion` in `app/build.gradle.kts`. It drives both the dependency and the Info screen.
