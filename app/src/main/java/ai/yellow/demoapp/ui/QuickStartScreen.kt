package ai.yellow.demoapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickStartScreen(onStart: (String) -> Unit, showAlert: (AlertMessage) -> Unit) {
    var botId by rememberSaveable { mutableStateOf("") }
    val focus = LocalFocusManager.current

    val start = {
        val id = botId.trim()
        if (id.isEmpty()) {
            showAlert(AlertMessage("Error", "Enter a valid bot id to start"))
        } else {
            focus.clearFocus()
            onStart(id)
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            "Use Quick Start to quickly launch a bot just using bot id.\n" +
                "For advanced bot configuration and bot storage use Profiles",
            fontSize = 16.sp,
            lineHeight = 22.sp,
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = botId,
            onValueChange = { botId = it },
            placeholder = { Text("Enter Bot Id. Eg: x0123456789") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false, imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { start() }),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = start, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Start Chat Bot", fontSize = 17.sp)
        }
    }
}
