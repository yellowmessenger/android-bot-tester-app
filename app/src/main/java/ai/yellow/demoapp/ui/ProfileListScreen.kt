package ai.yellow.demoapp.ui

import ai.yellow.demoapp.Bot
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** One row of the profile action sheet. */
data class SheetAction(val title: String, val destructive: Boolean = false, val onClick: () -> Unit)

@Composable
fun ProfileListScreen(bots: List<Bot>, onSelect: (Bot) -> Unit) {
    if (bots.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No profiles yet. Tap + to add a bot.", color = MaterialTheme.colorScheme.outline)
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(bots, key = { it.id }) { bot ->
            ListItem(
                headlineContent = { Text(bot.name.ifEmpty { "Unnamed" }) },
                supportingContent = { Text(bot.id) },
                trailingContent = { Text("›", fontSize = 24.sp) },
                modifier = Modifier.clickable { onSelect(bot) },
            )
            HorizontalDivider()
        }
    }
}

/** Replacement for the iOS app's UIAlertController action sheet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSheet(title: String, actions: List<SheetAction>, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 8.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
            )
            actions.forEach { action ->
                TextButton(
                    onClick = {
                        onDismiss()
                        action.onClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        action.title,
                        fontSize = 17.sp,
                        color = if (action.destructive) Color(0xFFDC2626) else Color.Unspecified,
                    )
                }
            }
        }
    }
}
