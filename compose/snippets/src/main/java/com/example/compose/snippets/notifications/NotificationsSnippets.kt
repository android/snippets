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

package com.example.compose.snippets.notifications

import android.content.Context
import android.content.Intent
import android.app.PendingIntent;
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat

@Composable
fun NotificationSnippets(context: Context) {
    // [START android_notification_authenticated_action]
    val intent = Intent(
        context, null
        /** Replace with a valid target activity */
    )
    val moreSecureNotification = NotificationCompat.Action.Builder(
        0, "Reply",
        PendingIntentCompat.getActivity(context, 0, intent, PendingIntent.FLAG_NO_CREATE, false)
    )
        // This notification always requests authentication when invoked
        // from a lock screen.
        .setAuthenticationRequired(true)
        .build()
    // [END android_notification_authenticated_action]
}