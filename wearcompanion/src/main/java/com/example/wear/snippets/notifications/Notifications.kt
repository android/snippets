package com.example.wear.snippets.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.wear.snippets.R

private const val channelId = "1"
private const val chatId = "123123"
private const val wearRequestCode = 123

fun bridgedActions(context: Context) {
    // [START android_wearcompanion_notification_bridged_actions]
    // This intent will be fired as a result of the user clicking the "Open on watch" action.
    // However, it executes on the phone, not on the watch. Typically, the Activity should then use
    // RemoteActivityHelper to then launch the correct activity on the watch.
    val intent = Intent(context, LaunchOnWearActivity::class.java)
    val wearPendingIntent = PendingIntent.getActivity(
        context,
        wearRequestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val openOnWatchAction = NotificationCompat.Action.Builder(
        R.drawable.watch,
        "Open on watch",
        wearPendingIntent
    )
        .build()

    val wearableExtender = NotificationCompat.WearableExtender()
        // This action will only be shown on the watch, not on the phone.
        // Actions added to the Notification builder directly will not be shown on the watch,
        // because one or more actions are defined in the WearableExtender.
        .addAction(openOnWatchAction)
        // This synchronizes dismissals between watch and phone.
        .setDismissalId(chatId)

    val notification = NotificationCompat.Builder(context, channelId)
        // ... set other fields ...
        .extend(wearableExtender)
        .build()
    // [END android_wearcompanion_notification_bridged_actions]
}