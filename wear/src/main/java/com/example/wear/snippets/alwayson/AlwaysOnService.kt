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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.example.wear.R

class AlwaysOnService : LifecycleService() {

    private val notificationManager by lazy { getSystemService<NotificationManager>() }

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
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand: Service started with startId: $startId")

        // Create and start foreground notification
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        Log.d(TAG, "onStartCommand: Service is now running as foreground service")

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service destroyed")
        isRunning = false
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    description = "Always On Service notification channel"
                    setShowBadge(false)
                }

        notificationManager?.createNotificationChannel(channel)
        Log.d(TAG, "createNotificationChannel: Notification channel created")
    }

    // [START android_wear_ongoing_activity_create_notification]
    private fun createNotification(): Notification {
        val activityIntent =
            Intent(this, AlwaysOnActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                // [START_EXCLUDE]
                .setContentTitle("Always On Service")
                .setContentText("Service is running in background")
                .setSmallIcon(R.drawable.animated_walk)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // [END_EXCLUDE]
                .setOngoing(true)

        // [START_EXCLUDE]
        // Create an Ongoing Activity
        val ongoingActivityStatus = Status.Builder().addTemplate("Stopwatch running").build()
        // [END_EXCLUDE]

        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                // [START_EXCLUDE]
                .setStaticIcon(R.drawable.ic_walk)
                .setAnimatedIcon(R.drawable.animated_walk)
                .setStatus(ongoingActivityStatus)
                // [END_EXCLUDE]
                .setTouchIntent(pendingIntent)
                .build()

        ongoingActivity.apply(applicationContext)

        return notificationBuilder.build()
    }
    // [END android_wear_ongoing_activity_create_notification]
}
