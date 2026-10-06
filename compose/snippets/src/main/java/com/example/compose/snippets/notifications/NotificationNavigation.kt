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

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder

private const val CHANNEL_ID = "channelId"
private const val NOTIFICATION_ID = 1

private class NotificationNavigationActivity : ComponentActivity() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startRegularActivityFromNotification() {
        // [START android_notification_navigation_regular_pending_intent]
        // Create an Intent for the activity you want to start.
        val resultIntent = Intent(this, ResultActivity::class.java)
        // Create the TaskStackBuilder.
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack.
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack.
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        // [END android_notification_navigation_regular_pending_intent]

        // [START android_notification_navigation_regular_notify]
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentIntent(resultPendingIntent)
            // ...
        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
        // [END android_notification_navigation_regular_notify]
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startSpecialActivityFromNotification() {
        // [START android_notification_navigation_special_pending_intent]
        val notifyIntent = Intent(this, ResultActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // [END android_notification_navigation_special_pending_intent]

        // [START android_notification_navigation_special_notify]
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentIntent(notifyPendingIntent)
            // ...
        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
        // [END android_notification_navigation_special_notify]
    }
}

private class ResultActivity : ComponentActivity()
