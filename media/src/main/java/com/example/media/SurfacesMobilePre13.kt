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

package com.example.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.OptIn
// [START android_media_surfaces_mobile_pre_android_13_notification]
import androidx.core.app.NotificationCompat
// [END android_media_surfaces_mobile_pre_android_13_notification]
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
// [START android_media_surfaces_mobile_pre_android_13_notification]
import androidx.media3.session.MediaStyleNotificationHelper

// [END android_media_surfaces_mobile_pre_android_13_notification]

private const val CHANNEL_ID = "playback_channel"

@OptIn(UnstableApi::class)
private fun preAndroid13Notification(
    context: Context,
    mediaSession: MediaSession,
    prevPendingIntent: PendingIntent,
    pausePendingIntent: PendingIntent,
    nextPendingIntent: PendingIntent,
    albumArtBitmap: Bitmap
) {
    // [START android_media_surfaces_mobile_pre_android_13_notification]
    var notification = NotificationCompat.Builder(context, CHANNEL_ID)
        // Show controls on lock screen even when user hides sensitive content.
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        // [START_EXCLUDE silent]
        .setSmallIcon(android.R.drawable.ic_media_play)
        .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
        .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent) // #1
        .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent) // #2
        /*
        // [END_EXCLUDE]
        .setSmallIcon(R.drawable.ic_stat_player)
        // Add media control buttons that invoke intents in your media service
        .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent) // #0
        .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent) // #1
        .addAction(R.drawable.ic_next, "Next", nextPendingIntent) // #2
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        // Apply the media style template
        .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession)
            .setShowActionsInCompactView(1 /* #1: pause button */))
        .setContentTitle("Wonderful music")
        .setContentText("My Awesome Band")
        .setLargeIcon(albumArtBitmap)
        .build()
    // [END android_media_surfaces_mobile_pre_android_13_notification]
}
