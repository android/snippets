/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.wear.snippets.alwayson

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class AlwaysOnService : LifecycleService() {

    companion object {
        private const val TAG = "AlwaysOnService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "always_on_service_channel"
        private const val CHANNEL_NAME = "Always On Service"
        @Volatile
        var isRunning = false
            private set

        fun startService(context: Context) {
            Log.d(TAG, "Starting AlwaysOnService")
            val intent = Intent(context, AlwaysOnService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            Log.d(TAG, "Stopping AlwaysOnService")
            context.stopService(Intent(context, AlwaysOnService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service created")
        isRunning = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Service started with startId: $startId")

        // Create and start foreground notification
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        Log.d(TAG, "onStartCommand: Service is now running as foreground service")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service destroyed")
        isRunning = false
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
                    .apply {
                        description = "Always On Service notification channel"
                        setShowBadge(false)
                    }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "createNotificationChannel: Notification channel created")
        }
    }

    private fun createNotification(): Notification {
        val intent =
            Intent(this, AlwaysOnActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Always On Service")
            .setContentText("Service is running in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
