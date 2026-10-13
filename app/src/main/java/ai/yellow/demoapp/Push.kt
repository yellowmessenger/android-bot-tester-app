package ai.yellow.demoapp

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Equivalent of the Firebase setup in the iOS tester app's AppDelegate: store
 * the FCM token so profiles with "Push Notification" enabled pass it to the
 * SDK. Notification permission is requested by MainActivity.
 *
 * Firebase is configured from app/google-services.json. Without it there's no
 * Firebase app; the bot still works and only push testing is affected.
 */
object Push {
    fun init(context: Context, store: BotStore) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            Log.i(TAG, "Push notifications not set up (optional): no google-services.json")
            return
        }
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { store.fcmToken = it }
            .addOnFailureListener { Log.i(TAG, "Push notifications not set up (optional): $it") }
    }

    private const val TAG = "push"
}
