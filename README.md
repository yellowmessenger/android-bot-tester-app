# Yellow.ai Chatbot SDK: Android Demo App

A sample Android app that shows how to add a Yellow.ai chatbot to a native Android app with the [YMChatbot-Android](https://github.com/yellowmessenger/YMChatbot-Android) SDK.

Use it to:

- **See your bot running in an Android app in a few minutes**, before you write any integration code.
- **Try different settings** (v1, v2 or v3 widget, auth token, payload, region) without rebuilding.
- **Copy working integration code**: starting the bot, listening to bot events, and registering for push notifications.

| | Version |
|---|---|
| YMChatbot-Android | 3.5.5 |
| Language / UI | Kotlin, Jetpack Compose |
| Minimum / target Android | 7.0 (API 24) / API 36 |

For the full SDK reference, see the [Android SDK docs](https://docs.yellow.ai/docs/platform_concepts/mobile/chatbot/android).

## Requirements

- Android Studio (recommended), or JDK 17 and the Android SDK with platform 36
- An emulator or device running Android 7.0 or later
- Your bot's **Bot ID**. Open your bot on the [Yellow.ai platform](https://cloud.yellow.ai); the Bot ID is in the URL and starts with `x` (for example `x1587041004122`).

## Run the app

1. Clone this repo.
2. Open the folder in Android Studio and press **Run**.

Or from the command line, with an emulator running or a device connected:

```bash
./gradlew installDebug
```

If Gradle can't find the Android SDK, create `local.properties` with `sdk.dir=/path/to/Android/sdk`. Android Studio does this for you.

## Try your bot

### Quick Start

Enter your **Bot ID** and tap **Start Chat Bot**. The bot opens with default settings: v1 widget, no auth token, no payload.

### Profiles

Save a bot with specific settings and reuse it. Profiles are stored on the device.

1. Open the **Profiles** tab and tap **+**.
2. Fill in the fields you need:

   | Field | What it does |
   |---|---|
   | Bot ID | Your bot's ID. Required. To change it later, delete the profile and add it again. |
   | Name | A label for the profile list. |
   | Version | Which chat widget to load: `v1`, `v2` or `v3`. |
   | Activation Mode | **v3 only.** Open the widget in `chat` or `voice` mode. |
   | Initial Message | **v3 only.** A message to start the conversation with. |
   | Enable Speech | **v1/v2 only.** Adds voice input to the widget. |
   | Use Lite Version | **v1/v2 only.** Loads the lite widget. |
   | YMAuthToken | A token that identifies the user, for bots that need an authenticated session. |
   | Api Key | Your Yellow.ai API key. Needed only for **Register Device** and **Unlink Device Token**. |
   | Push Notification | Sends this device's push token to the bot. Needs Firebase (see below). |
   | Custom Base URL | Leave empty if your bot is in the default region. For other regions use `https://rx.cloud.yellow.ai` (for example `https://r5.cloud.yellow.ai`), or your on-premise URL. |
   | Payload | Any JSON object you want to send to the bot when the chat starts, such as user details. Must be valid JSON. |

3. Tap **Save**, then tap the profile:
   - **Start Bot** opens the chat with this profile's settings.
   - **Register Device** links this device to the user for push notifications. Shown when Api Key, YMAuthToken and Push Notification are set.
   - **Unlink Device Token** stops push notifications to this device, for example on logout. Shown when Api Key and Push Notification are set.
   - **Edit** / **Delete**

### Info

Shows the app version, the YMChatbot-Android SDK version and the Android version.

## Push notifications (optional)

The app runs without Firebase. To test push notifications from your bot:

1. In your own [Firebase project](https://console.firebase.google.com), add an Android app with the package name `ai.yellow.demoapp`.
2. Download its `google-services.json` and put it in the `app/` folder.
3. Map your Firebase service account key to your Bot ID, as described in [Push notifications for mobile SDKs](https://docs.yellow.ai/docs/platform_concepts/mobile/chatbot/sdk-faq-worflows/push-notifications-sdks).
4. Rebuild, turn on **Push Notification** in a profile, and use **Register Device**.

## Integration code to reuse

All SDK calls are in a few files under `app/src/main/java/ai/yellow/demoapp/`:

| File | What to look at |
|---|---|
| `Chatbot.kt` | Building a `YMConfig` from your settings, starting the bot, registering and unlinking the device, and listening to bot events |
| `Push.kt` | Getting the Firebase push token |
| `Bot.kt` | The settings the app passes to the SDK, and payload validation |
| `app/build.gradle.kts` | Adding the SDK dependency (`ymChatVersion`) |

## Tips for your own integration

- Create a fresh `YMConfig` each time you start the bot so no settings (payload included) carry over from a previous session.
- `YMBotCloseEvent` currently fires twice when the bot closes. Make your close handler safe to run twice.
- To try another SDK version, change `ymChatVersion` in `app/build.gradle.kts`.

## Help

- [Android SDK docs](https://docs.yellow.ai/docs/platform_concepts/mobile/chatbot/android)
- [Mobile SDK FAQ](https://docs.yellow.ai/docs/platform_concepts/mobile/chatbot/sdk-faq-worflows/mobile-sdk-faq)
- Email [apps@yellow.ai](mailto:apps@yellow.ai)
