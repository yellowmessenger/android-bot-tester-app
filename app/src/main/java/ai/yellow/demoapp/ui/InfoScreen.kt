package ai.yellow.demoapp.ui

import ai.yellow.demoapp.BuildConfig
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun InfoScreen() {
    val rows = listOf(
        "App Version" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        "YMChatbot-Android" to BuildConfig.YMCHAT_VERSION,
        "Android" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
    )
    Column {
        rows.forEach { (label, value) ->
            ListItem(headlineContent = { Text(label) }, trailingContent = { Text(value) })
            HorizontalDivider()
        }
    }
}
