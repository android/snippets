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
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.utils.MediaConstants.SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_NEXT
import androidx.media.utils.MediaConstants.SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_PREV
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

private const val ACTION_FAVORITES = "ACTION_FAVORITES"
private const val CUSTOM_ACTION_1 = "CUSTOM_ACTION_1"
private const val CUSTOM_ACTION_2 = "CUSTOM_ACTION_2"
private const val TAG = "SurfacesMobile"
private const val MY_RECENTS_ROOT_ID = "MY_RECENTS_ROOT_ID"
private const val MY_MEDIA_ROOT_ID = "MY_MEDIA_ROOT_ID"
private const val MY_EMPTY_ROOT_ID = "MY_EMPTY_ROOT_ID"
private const val CHANNEL_ID = "playback_channel"

@OptIn(UnstableApi::class)
// [START android_media_surfaces_mobile_custom_command_buttons]
class CustomControlsPlaybackService : MediaSessionService() {
  private val customCommandFavorites = SessionCommand(ACTION_FAVORITES, Bundle.EMPTY)
  private var mediaSession: MediaSession? = null

  override fun onCreate() {
    super.onCreate()
    val favoriteButton =
      CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
        .setDisplayName("Save to favorites")
        .setSessionCommand(customCommandFavorites)
        .build()
    val player = ExoPlayer.Builder(this).build()
    // Build the session with a custom layout.
    mediaSession =
      MediaSession.Builder(this, player)
        .setCallback(MyCallback())
        .setMediaButtonPreferences(ImmutableList.of(favoriteButton))
        .build()
  }

  private inner class MyCallback : MediaSession.Callback {
    override fun onConnect(
      session: MediaSession,
      controller: MediaSession.ControllerInfo
    ): ConnectionResult {
    // Set available player and session commands.
    return AcceptedResultBuilder(session)
      .setAvailableSessionCommands(
        ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
          .add(customCommandFavorites)
          .build()
      )
      .build()
    }

    override fun onCustomCommand(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      customCommand: SessionCommand,
      args: Bundle
    ): ListenableFuture<SessionResult> {
      if (customCommand.customAction == ACTION_FAVORITES) {
        // Do custom logic here
        saveToFavorites(session.player.currentMediaItem)
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
      }
      return super.onCustomCommand(session, controller, customCommand, args)
    }
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
    return mediaSession
  }

  private fun saveToFavorites(item: MediaItem?) {}
}
// [END android_media_surfaces_mobile_custom_command_buttons]

private fun addStandardActions(context: Context, notificationBuilder: NotificationCompat.Builder) {
    // [START android_media_surfaces_mobile_add_standard_actions]
    val session = MediaSessionCompat(context, TAG)
    val playbackStateBuilder = PlaybackStateCompat.Builder()
    val style = androidx.media.app.NotificationCompat.MediaStyle()

    // For this example, the media is currently paused:
    val state = PlaybackStateCompat.STATE_PAUSED
    val position = 0L
    val playbackSpeed = 1f
    playbackStateBuilder.setState(state, position, playbackSpeed)

    // And the user can play, skip to next or previous, and seek
    val stateActions = (PlaybackStateCompat.ACTION_PLAY
        or PlaybackStateCompat.ACTION_PLAY_PAUSE
        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        or PlaybackStateCompat.ACTION_SEEK_TO) // adding the seek action enables seeking with the seekbar
    playbackStateBuilder.setActions(stateActions)

    // ... do more setup here ...

    session.setPlaybackState(playbackStateBuilder.build())
    style.setMediaSession(session.sessionToken)
    notificationBuilder.setStyle(style)
    // [END android_media_surfaces_mobile_add_standard_actions]
}

private fun slotReservationExtras(session: MediaSessionCompat) {
    // [START android_media_surfaces_mobile_slot_reservation_extras]
    session.setExtras(Bundle().apply {
        putBoolean(SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_PREV, true)
        putBoolean(SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_NEXT, true)
    })
    // [END android_media_surfaces_mobile_slot_reservation_extras]
}

private fun addCustomActions(playbackStateBuilder: PlaybackStateCompat.Builder) {
    // [START android_media_surfaces_mobile_add_custom_actions]
    val customAction = PlaybackStateCompat.CustomAction.Builder(
        "com.example.MY_CUSTOM_ACTION", // action ID
        "Custom Action", // title - used as content description for the button
        android.R.drawable.ic_media_play
    ).build()

    playbackStateBuilder.addCustomAction(customAction)
    // [END android_media_surfaces_mobile_add_custom_actions]
}

private fun playbackStateCallback(session: MediaSessionCompat) {
    // [START android_media_surfaces_mobile_playback_state_callback]
    val callback = object: MediaSessionCompat.Callback() {
        override fun onPlay() {
            // start playback
        }

        override fun onPause() {
            // pause playback
        }

        override fun onSkipToPrevious() {
            // skip to previous
        }

        override fun onSkipToNext() {
            // skip to next
        }

        override fun onSeekTo(pos: Long) {
            // jump to position in track
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            when (action) {
                CUSTOM_ACTION_1 -> doCustomAction1(extras)
                CUSTOM_ACTION_2 -> doCustomAction2(extras)
                else -> {
                    Log.w(TAG, "Unknown custom action $action")
                }
            }
        }

    }

    session.setCallback(callback)
    // [END android_media_surfaces_mobile_playback_state_callback]
}

private fun doCustomAction1(extras: Bundle?) {}
private fun doCustomAction2(extras: Bundle?) {}

private class MobileResumptionBrowserService : MediaBrowserServiceCompat() {
    // [START android_media_surfaces_mobile_on_get_root]
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        ...
        // [START_EXCLUDE silent]
        */
        // [END_EXCLUDE]
        // Verify that the specified package is SystemUI. You'll need to write your 
        // own logic to do this.
        if (isSystem(clientPackageName, clientUid)) {
            rootHints?.let {
                if (it.getBoolean(BrowserRoot.EXTRA_RECENT)) {
                    // Return a tree with a single playable media item for resumption.
                    val extras = Bundle().apply {
                        putBoolean(BrowserRoot.EXTRA_RECENT, true)
                    }
                    return BrowserRoot(MY_RECENTS_ROOT_ID, extras)
                }
            }
            // You can return your normal tree if the EXTRA_RECENT flag is not present.
            return BrowserRoot(MY_MEDIA_ROOT_ID, null)
        }
        // Return an empty tree to disallow browsing.
        return BrowserRoot(MY_EMPTY_ROOT_ID, null)
    }
    // [END android_media_surfaces_mobile_on_get_root]

    private fun isSystem(clientPackageName: String, clientUid: Int): Boolean = true

    override fun onLoadChildren(
        parentId: String,
        result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }
}

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
    .setSmallIcon(android.R.drawable.ic_media_play)
    // Add media control buttons that invoke intents in your media service
    .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
    .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent) // #1
    .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent) // #2
    // Apply the media style template
    .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession)
    .setShowActionsInCompactView(1 /* #1: pause button */))
    .setContentTitle("Wonderful music")
    .setContentText("My Awesome Band")
    .setLargeIcon(albumArtBitmap)
    .build()
    // [END android_media_surfaces_mobile_pre_android_13_notification]
}
