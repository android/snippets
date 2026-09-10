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
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.compose.snippets.R

@RequiresApi(Build.VERSION_CODES.O)
// [START android_notifications_time_sensitive_channel]
class DACapp : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            CHANNEL_ID,
            "High priority notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
// [END android_notifications_time_sensitive_channel]

@Composable
private fun NotificationPermissionSample() {
    var hasNotificationPermission by remember { mutableStateOf(false) }
    // [START android_notifications_time_sensitive_permission]
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasNotificationPermission = it }
    )
    // [START_EXCLUDE]
    val fillerCode = Unit
    // [END_EXCLUDE]
    Button(
        onClick = {
            if (!hasNotificationPermission) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        },
    ) {
        Text(text = "Request permission")
    }
    // [END android_notifications_time_sensitive_permission]
}

private class TimeSensitiveNotificationActivity : ComponentActivity() {

    @SuppressLint("MissingPermission")
    // [START android_notifications_time_sensitive_create]
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_auto_awesome_24)
                .setContentTitle("HIGH PRIORITY")
                .setContentText("Check this dog puppy video NOW!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)

        notificationManager.notify(0, notificationBuilder.build())
    }
    // [END android_notifications_time_sensitive_create]

    @Composable
    private fun DisplayNotificationButton() {
        // [START android_notifications_time_sensitive_display]
        Button(onClick = { showNotification() }) {
            Text(text = "Show notification")
        }
        // [END android_notifications_time_sensitive_display]
    }
}

private class TimeSensitiveOngoingService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationId = 1
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).build()
        // [START android_notifications_time_sensitive_ongoing]
        // Provide a unique integer for the "notificationId" of each notification.
        startForeground(notificationId, notification)
        // [END android_notifications_time_sensitive_ongoing]
        return START_NOT_STICKY
    }
}
