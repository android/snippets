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
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.compose.snippets.R
import com.example.compose.snippets.touchinput.Button

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

@Composable
fun NotificationSnippetRequestPostPermission() {
    // [START android_notification_request_post_permission]
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, you can now send notifications.
        } else {
            // Permission denied, handle accordingly.
        }
    }

    // In the UI ...
    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }) {
        Text("Enable Notifications")
    }
    // [END android_notification_request_post_permission]
}

fun deleteNotificationChannelId(context: Context, channelId: String) {
    // Side effects like deleting a channel should be triggered by events or wrapped in a
    // LaunchedEffect to avoid executing them on every recomposition. This is only
    // for demonstrative purposes
    // [START android_notification_delete_channel]
    val notificationManager =
        ContextCompat.getSystemService<NotificationManager>(context, NotificationManager::class.java)
    notificationManager?.deleteNotificationChannel(channelId)
    // [END android_notification_delete_channel]
}

// [START android_notification_create_group_channel]
fun createNotificationChannelGroup(context: Context, groupId: String, groupName: String) {
    val notificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)
    notificationManager?.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))
}
// [END android_notification_create_group_channel]

@OptIn(UnstableApi::class)
fun notificationStyles(context: Context) {
    val bitmapImage = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.dog
    )
    val CHANNEL_ID = "channelId"
    val someVeryLongMessage = "This is a very long message"
    // [START android_notification_big_picture_style]
    var notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.example.compose.snippets.R.drawable.ic_logo)
            .setContentTitle("Title")
            .setContentText("Content text")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmapImage)
            )
            .build()
    // [END android_notification_big_picture_style]

    // [START android_notification_big_picture_style_thumbnail]
    notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("Title")
        .setContentText("Content text")
        .setLargeIcon(Icon.createWithResource(context, R.drawable.dog))
        .setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(bitmapImage)
                .bigLargeIcon(null)
        )
        .build()
    // [END android_notification_big_picture_style_thumbnail]

    // [START android_notification_bigtext_style]
    notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("Sender name")
        .setContentText("Email subject")
        .setLargeIcon(Icon.createWithResource(context, R.drawable.dog))
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(someVeryLongMessage)
        )
        .build()
    // [END android_notification_bigtext_style]

    // [START android_notification_inbox_style]
    notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("5 New mails from Frank")
        .setContentText("Check them out")
        .setLargeIcon(bitmapImage)
        .setStyle(
            NotificationCompat.InboxStyle()
                .addLine("Re: Planning")
                .addLine("Delivery on its way")
                .addLine("Follow-up")
        )
        .build()
    // [END android_notification_inbox_style]

    // [START android_notification_messaging_style]
    val message1 = NotificationCompat.MessagingStyle.Message(
        messages[0].text,
        messages[0].time,
        messages[0].sender
    )
    val message2 = NotificationCompat.MessagingStyle.Message(
        messages[1].text,
        messages[1].time,
        messages[1].sender
    )
    notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setStyle(
            NotificationCompat.MessagingStyle(Person.Builder().setName("Me").build())
                .addMessage(message1)
                .addMessage(message2)
        )
        .build()
    // [END android_notification_messaging_style]

    val player = ExoPlayer.Builder(context).build()
    val mediaSession = MediaSession.Builder(context, player).build()
    // [START android_notification_media_style]
    notification = NotificationCompat.Builder(context, CHANNEL_ID)
        // Show controls on lock screen even when user hides sensitive content.
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(com.example.compose.snippets.R.drawable.play)
        // Add media control buttons that invoke intents in your media service
        .addAction(R.drawable.previous, "Previous", null /* Add valid intent */) // #0
        .addAction(R.drawable.pause, "Pause", null /* Add valid intent */) // #1
        .addAction(R.drawable.next, "Next", null /* Add valid intent */) // #2
        // Apply the media style template.
        .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession)
            .setShowActionsInCompactView(1 /* #1: pause button */))
        .setContentTitle("Wonderful music")
        .setContentText("My Awesome Band")
        .setLargeIcon(bitmapImage)
        .build()
    // [END android_notification_media_style]
}

data class Message(
    val text: String,
    val time: Long,
    val sender: Person
)

val messages = listOf(
    Message(
        "Check this out!",
        System.currentTimeMillis(),
        Person.Builder().setName("Frank").build()
    ),
    Message(
        "Planning for the weekend?",
        System.currentTimeMillis(),
        Person.Builder().setName("Frank").build()
    )
)
