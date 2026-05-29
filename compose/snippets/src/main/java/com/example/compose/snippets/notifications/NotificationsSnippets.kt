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
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.annotation.RequiresPermission
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.compose.snippets.R
import com.example.compose.snippets.notifications.ReplyReceiver.Companion.KEY_REPLY
import com.example.compose.snippets.notifications.ReplyReceiver.Companion.KEY_TEXT_REPLY
import com.example.compose.snippets.touchinput.Button
import kotlin.random.Random

val CHANNEL_ID = "channelId"

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

fun createNotification(context: Context) {
    // [START android_notification_create]
    val textTitle = "Title"
    val textContent = "Content"
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    // [END android_notification_create]
}

fun createNotificationWithStyle(context: Context) {
    // [START android_notification_set_style]
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("My notification")
        .setContentText("Much longer text that cannot fit one line...")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Much longer text that cannot fit one line..."))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    // [END android_notification_set_style]
}

// [START android_notification_create_channel]
fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
// [END android_notification_create_channel]

fun createNotificationTapAction(context: Context) {
// [START android_notification_tap_action]
    // Create an explicit intent for an Activity in your app.
    val intent = Intent(context, AlertDetails::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // Set the intent that fires when the user taps the notification.
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
// [END android_notification_tap_action]
}

fun showNotification(context: Context) {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    val notificationId = 1 // This is demonstrative and should be unique.
    // [START android_notification_show_notification]
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling ActivityCompat#requestPermissions here
            // to request the missing permissions, and then overriding
            // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            //                                        grantResults: IntArray)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return@with
        }
        // notificationId is a unique int for each notification that you must define.
        notify(notificationId, builder.build())
        // [END android_notification_show_notification]
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun addActionButton(context: Context) {
    val pendingIntent = PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    // [START android_notification_add_action]
    val ACTION_SNOOZE = "snooze"
    val snoozeIntent = Intent(context, MyBroadcastReceiver::class.java).apply {
        action = ACTION_SNOOZE
        putExtra(EXTRA_NOTIFICATION_ID, 0)
    }
    val snoozePendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.snooze, context.getString(R.string.snooze),
            snoozePendingIntent)
    // [END android_notification_add_action]

    // [START android_notification_add_reply]
    // Key for the string that's delivered in the action's intent.
    val replyLabel: String = context.resources.getString(R.string.reply_label)
    val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
        setLabel(replyLabel)
        build()
    }
    // [END android_notification_add_reply]

    val conversationId = 1 // This is demonstrative and should be unique.
    // [START android_notification_add_reply_pending_intent]
    // Build a PendingIntent for the reply action to trigger.
    val replyPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context,
            conversationId,
            getMessageReplyIntent(conversationId),
            PendingIntent.FLAG_MUTABLE)
    // [END android_notification_add_reply_pending_intent]

    // [START android_notification_add_reply_action]
    // Create the reply action and add the remote input.
    val action: NotificationCompat.Action =
        NotificationCompat.Action.Builder(R.drawable.reply,
            context.getString(R.string.reply_label), replyPendingIntent)
            .addRemoteInput(remoteInput)
            .build()
    // [END android_notification_add_reply_action]

    val notificationId = 1 // This is demonstrative and should be unique.
    // [START android_notification_add_reply_action_add_action]
    // Build the notification and add the action.
    val newMessageNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(context.getString(R.string.title))
        .setContentText(context.getString(R.string.content))
        .addAction(action)
        .build()

    // Issue the notification.
    NotificationManagerCompat.from(context).notify(notificationId, newMessageNotification)
    // [END android_notification_add_reply_action_add_action]
}

// [START android_notification_retrieve_user_input]
private fun getMessageText(intent: Intent): CharSequence? {
    return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)
}
// [END android_notification_retrieve_user_input]

fun getMessageReplyIntent(conversationId: Any): Intent {
    // This is for demonstrative purposes.
    TODO("Not yet implemented")
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun handledReply(context: Context) {
    val notificationId = 1 // This is demonstrative and should be unique.
    // [START android_notification_handled_reply]
    // Build a new notification, which informs the user that the system
    // handled their interaction with the previous notification.
    val repliedNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.message)
        .setContentText(context.getString(R.string.replied))
        .build()

    // Issue the new notification.
    NotificationManagerCompat.from(context).notify(notificationId, repliedNotification)
    // [END android_notification_handled_reply]
}

fun retrieveOtherData(context: Context) {
    // [START android_notification_remote_input_retrieve_data]
    val replyLabel: String = context.resources.getString(R.string.reply_label)
    val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
        setLabel(replyLabel)
        // Allow for image data types in the input.
        // This method can be used again to allow for other data types.
        setAllowDataType("image/*", true)
        build()
    }
    // [END android_notification_remote_input_retrieve_data]
}

fun fullScreenIntent(context: Context) {
    // [START android_notification_full_screen_intent]
    val fullScreenIntent = Intent(context, ImportantActivity::class.java)
    val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
        fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setFullScreenIntent(fullScreenPendingIntent, true)
    // [END android_notification_full_screen_intent]
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
                .bigLargeIcon(null as Bitmap?)
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
        .setSmallIcon(R.drawable.mail)
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

    // [START android_notification_progress_style]
    val progressStyleNotification =
        NotificationCompat.ProgressStyle()
            .setStyledByProgress(false)
            .setProgress(456)
            .setProgressTrackerIcon(IconCompat.createWithResource(context, R.drawable.car))
            .setProgressSegments(
                listOf(
                    NotificationCompat.ProgressStyle.Segment(41).setColor(Color.BLACK),
                    NotificationCompat.ProgressStyle.Segment(552).setColor(Color.YELLOW),
                    NotificationCompat.ProgressStyle.Segment(253).setColor(Color.WHITE),
                    NotificationCompat.ProgressStyle.Segment(94).setColor(Color.BLUE)
                )
            )
            .setProgressPoints(
                listOf(
                    NotificationCompat.ProgressStyle.Point(60).setColor(Color.RED),
                    NotificationCompat.ProgressStyle.Point(560).setColor(Color.GREEN)
                )
            )
    // [END android_notification_progress_style]

    // [START android_notification_call_style_caller]
    // Create a new call, setting incoming caller.
    val incomingCaller = Person.Builder()
        .setName("Jane Doe")
        .setImportant(true)
        .build()
    // [END android_notification_call_style_caller]

    val caller= Person.Builder()
        .setName("Your name")
        .setImportant(true)
        .build()

    val callerAtOtherEnd = Person.Builder()
        .setName("John Doe")
        .setImportant(true)
        .build()

    // [START android_notification_call_style]
    // For demonstrative purposes only, should use a well-defined Intents.
    val contentIntent =
        PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    val declineIntent =
        PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    val hangupIntent = PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    val answerIntent = PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    // Create a call style notification for an incoming call.
    val builderForIncomingCall = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_logo)
        .setStyle(
            NotificationCompat.CallStyle.forIncomingCall(incomingCaller, declineIntent, answerIntent))
        .addPerson(incomingCaller)
    // [END android_notification_call_style]

    // [START android_notification_call_style_ongoing]
    // Create a call style notification for an ongoing call.
    val builderForOngoingCall = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_logo)
        .setStyle(
            NotificationCompat.CallStyle.forOngoingCall(callerAtOtherEnd, hangupIntent))
        .addPerson(caller)
    // [END android_notification_call_style_ongoing]

    // [START android_notification_call_style_screening]
    // Create a call style notification for screening a call.
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_logo)
        .setStyle(
            NotificationCompat.CallStyle.forScreeningCall(caller, hangupIntent, answerIntent))
        .addPerson(caller)
    // [END android_notification_call_style_screening]
}

@Composable
fun notificationPermissionRequest() {
    // [START android_notification_permission_request]
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

    // ... in your UI ...
    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }) {
        Text("Enable Notifications")
    }
    // [END android_notification_permission_request]
}

@Composable
fun notificationChannels() {
    val context = LocalContext.current
    val name = stringResource(R.string.channel_name)
    val descriptionText = stringResource(R.string.channel_description)

    // [START android_notification_channel_create]
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(mChannel)
        }
    }
    // [END android_notification_channel_create]

    // [START android_notification_channel_setting_intent]
    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID)
    }
    Button(onClick = {
        context.startActivity(intent)
    }) {
        Text("Open Channel Settings")
    }
    // [END android_notification_channel_setting_intent]
}

fun notificationChatBubble(context: Context) {
    // For demonstrative purposes only, this should use a well defined Intent.
    val contentIntent =
        PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    // [START android_notification_chat_bubble]
    // Create a bubble intent.
    val target = Intent(context, BubbleActivity::class.java)
    val bubbleIntent =
        PendingIntent.getActivity(context, 0, target, PendingIntent.FLAG_IMMUTABLE /* flags */)
    val category = "com.example.category.IMG_SHARE_TARGET"

    val chatPartner = Person.Builder()
        .setName("Chat partner")
        .setImportant(true)
        .build()

    // Create a sharing shortcut.
    val shortcutId = generateShortcutId()
    val shortcut =
        ShortcutInfoCompat.Builder(context, shortcutId)
            .setCategories(setOf(category))
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel("Chat partner name")
            .build()
    // Publish the shortcut, otherwise the bubble metadata will not apply.
    ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

    // Create a bubble metadata.
    val bubbleData = NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
        IconCompat.createWithResource(context, R.drawable.ic_logo))
        .setDesiredHeight(600)
        .build()

    // Create a notification, referencing the sharing shortcut.
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.chat)
        .setBubbleMetadata(bubbleData)
        .setShortcutId(shortcutId)
        .addPerson(chatPartner)
    // [END android_notification_chat_bubble]

    // [START android_notification_chat_bubble_expanded]
    val bubbleMetadata = NotificationCompat.BubbleMetadata.Builder()
        .setDesiredHeight(600)
        .setIntent(bubbleIntent)
        .setAutoExpandBubble(true)
        .setSuppressNotification(true)
        .build()
    // [END android_notification_chat_bubble_expanded]
}

// For demonstrative purposes only, should not be used in production.
fun generateShortcutId() : String {
    return Random.nextInt().toString()
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

// For demonstrative purposes only when used for a full screen intent.
class ImportantActivity : ComponentActivity() {
}

// For demonstrative purposes only for launching from a tap action.
class AlertDetails : ComponentActivity() {
}

// For demonstrative purposes only for the Bubble Activity.
class BubbleActivity : ComponentActivity() {
}

// For demonstrative purposes only for handling a snooze action.
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }
}