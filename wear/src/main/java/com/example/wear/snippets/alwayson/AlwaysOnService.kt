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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.example.wear.R

abstract class AlwaysOnServiceBase : LifecycleService() {

    private val notificationManager by lazy { getSystemService<NotificationManager>() }

    companion object {
        private const val TAG = "AlwaysOnService"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "always_on_service_channel"
        private const val CHANNEL_NAME = "Always On Service"

        @Volatile
        var isRunning = false
            private set
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

        createNotification()

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

    abstract fun createNotification()
}

class AlwaysOnService1 : AlwaysOnServiceBase() {
    override fun createNotification() {
        // Creates an ongoing activity that demonstrates how to link the touch intent to the always-on activity.
        // [START android_wear_ongoing_activity_create_notification]
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
                // ...
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
        val ongoingActivityStatus = Status.Builder().addTemplate("Stopwatch running").build()
        // [END_EXCLUDE]

        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                // ...
                // [START_EXCLUDE]
                .setStaticIcon(R.drawable.ic_walk)
                .setAnimatedIcon(R.drawable.animated_walk)
                .setStatus(ongoingActivityStatus)
                // [END_EXCLUDE]
                .setTouchIntent(pendingIntent)
                .build()

        ongoingActivity.apply(applicationContext)

        val notification = notificationBuilder.build()
        // [END android_wear_ongoing_activity_create_notification]

        startForeground(NOTIFICATION_ID, notification)
    }
}

class AlwaysOnService2 : AlwaysOnServiceBase() {
    override fun createNotification() {
        // Creates an ongoing activity with a static status text

        // [START android_wear_ongoing_activity_notification_builder]
        // Create a PendingIntent to pass to the notification builder
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, AlwaysOnActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Always On Service")
            .setContentText("Service is running in background")
            .setSmallIcon(R.drawable.animated_walk)
            // Category helps the system prioritize the ongoing activity
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true) // Important!
        // [END android_wear_ongoing_activity_notification_builder]

        // [START android_wear_ongoing_activity_builder]
        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                // Sets the icon that appears on the watch face in active mode.
                .setAnimatedIcon(R.drawable.animated_walk)
                // Sets the icon that appears on the watch face in ambient mode.
                .setStaticIcon(R.drawable.ic_walk)
                // Sets the tap target to bring the user back to the app.
                .setTouchIntent(pendingIntent)
                .build()
        // [END android_wear_ongoing_activity_builder]

        // [START android_wear_ongoing_activity_post_notification]
        // This call modifies notificationBuilder to include the ongoing activity data.
        ongoingActivity.apply(applicationContext)

        // Post the notification.
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        // [END android_wear_ongoing_activity_post_notification]
    }
}

class AlwaysOnService3 : AlwaysOnServiceBase() {
    override fun createNotification() {
        // Creates an ongoing activity that demonstrates dynamic status text (a timer)
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

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Always On Service")
            .setContentText("Service is running in background")
            .setSmallIcon(R.drawable.animated_walk)
            // Category helps the system prioritize the ongoing activity
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true) // Important!

        // [START android_wear_ongoing_activity_create_status]
        // Define a template with placeholders for the activity type and the timer.
        val statusTemplate = "#type# for #time#"

        // Set the start time for a stopwatch.
        // Use SystemClock.elapsedRealtime() for time-based parts.
        val runStartTime = SystemClock.elapsedRealtime()

        val ongoingActivityStatus = Status.Builder()
            // Sets the template string.
            .addTemplate(statusTemplate)
            // Fills the #type# placeholder with a static text part.
            .addPart("type", Status.TextPart("Run"))
            // Fills the #time# placeholder with a stopwatch part.
            .addPart("time", Status.StopwatchPart(runStartTime))
            .build()
        // [END android_wear_ongoing_activity_create_status]

        // [START android_wear_ongoing_activity_set_status]
        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                // [START_EXCLUDE]
                .setAnimatedIcon(R.drawable.animated_walk)
                .setStaticIcon(R.drawable.ic_walk)
                .setTouchIntent(pendingIntent)
                // [END_EXCLUDE]
                // Add the status to the OngoingActivity.
                .setStatus(ongoingActivityStatus)
                .build()
        // [END android_wear_ongoing_activity_set_status]

        ongoingActivity.apply(applicationContext)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }
}
