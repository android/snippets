/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wear.snippets.widget.skill

// [START android_wear_widget_skill_interactive_actions]
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.remote.creation.compose.action.pendingIntentAction
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.clickable
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
import androidx.wear.compose.remote.material3.RemoteButton
import androidx.wear.compose.remote.material3.RemoteText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RemoteComposable
@Composable
fun InteractiveHeaderAndRefreshSection(
    activityClass: Class<*>,
    receiverClass: Class<out BroadcastReceiver>,
) {
    val launchActivityAction = pendingIntentAction { ctx ->
        val intent = Intent(ctx, activityClass).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        PendingIntent.getActivity(
            ctx,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    val backgroundRefreshAction = pendingIntentAction { ctx ->
        val intent = Intent(ctx, receiverClass).apply {
            action = ACTION_REFRESH_WIDGET
        }
        PendingIntent.getBroadcast(
            ctx,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    RemoteColumn(modifier = RemoteModifier.fillMaxSize()) {
        RemoteText(
            text = "Open App".rs,
            modifier = RemoteModifier.clickable(launchActivityAction),
        )
        RemoteButton(onClick = backgroundRefreshAction) {
            RemoteText(text = "Refresh".rs)
        }
    }
}

const val ACTION_REFRESH_WIDGET = "com.example.wear.ACTION_REFRESH_WIDGET"

class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                MyWidget().triggerUpdateAll(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
// [END android_wear_widget_skill_interactive_actions]
