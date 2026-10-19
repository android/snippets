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

package com.example.example.snippet.views.notifications

import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import com.example.example.snippet.views.R

private const val CHANNEL_ID = "channelId"

private class CustomNotificationActivity : ComponentActivity() {

    fun showCustomNotification() {
        val context: Context = this
        // [START android_views_notifications_custom_content_view]
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Get the layouts to use in the custom notification.
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)

        // Apply the layouts to the notification.
        val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .build()

        notificationManager.notify(666, customNotification)
        // [END android_views_notifications_custom_content_view]
    }
}
