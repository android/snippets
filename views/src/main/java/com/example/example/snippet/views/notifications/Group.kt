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

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.example.snippet.views.R

private const val CHANNEL_ID = "channelId"

private class MainActivity : ComponentActivity() {
    private val emailObject = EmailObject()
    private val emailObject1 = EmailObject()
    private val emailObject2 = EmailObject()
    private val emailNotificationId1 = 1
    private val emailNotificationId2 = 2

    fun createGroupNotification() {
        // [START android_views_notifications_group_notification]
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        val newMessageNotification = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.new_mail)
                .setContentTitle(emailObject.getSenderName())
                .setContentText(emailObject.getSubject())
                .setLargeIcon(emailObject.getSenderAvatar())
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .build()
        // [END android_views_notifications_group_notification]
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun createGroupSummary() {
        // [START android_views_notifications_group_summary]
        // Use constant ID for notifications used as group summary.
        val SUMMARY_ID = 0
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        val newMessageNotification1 = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_email_status)
                .setContentTitle(emailObject1.getSummary())
                .setContentText("You will not believe...")
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .build()

        val newMessageNotification2 = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_email_status)
                .setContentTitle(emailObject2.getSummary())
                .setContentText("Please join us to celebrate the...")
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .build()

        val summaryNotification = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setContentTitle(emailObject.getSummary())
                // Set content text to support devices running API level < 24.
                .setContentText("Two new messages")
                .setSmallIcon(R.drawable.ic_notify_summary_status)
                // Build summary info into InboxStyle template.
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine("Alex Faarborg Check this out")
                        .addLine("Jeff Chang Launch Party")
                        .setBigContentTitle("2 new messages")
                        .setSummaryText("janedoe@example.com"))
                // Specify which group this notification belongs to.
                .setGroup(GROUP_KEY_WORK_EMAIL)
                // Set this notification as the summary for the group.
                .setGroupSummary(true)
                .build()

        NotificationManagerCompat.from(this).apply {
            notify(emailNotificationId1, newMessageNotification1)
            notify(emailNotificationId2, newMessageNotification2)
            notify(SUMMARY_ID, summaryNotification)
        }
        // [END android_views_notifications_group_summary]
    }
}

private class EmailObject(
    private val senderName: String = "",
    private val subject: String = "",
    private val summary: String = "",
    private val senderAvatar: Bitmap? = null,
) {
    fun getSenderName(): String = senderName
    fun getSubject(): String = subject
    fun getSummary(): String = summary
    fun getSenderAvatar(): Bitmap? = senderAvatar
}
