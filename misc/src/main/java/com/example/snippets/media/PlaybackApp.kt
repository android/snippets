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

package com.example.snippets.media

import android.content.ComponentName
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.MoreExecutors

private fun createExoPlayer(context: Context) {
    // [START android_media_playback_app_create_exoplayer]
    val player = ExoPlayer.Builder(context).build()
    // [END android_media_playback_app_create_exoplayer]
}

private fun createMediaSession(context: Context) {
    // [START android_media_playback_app_create_media_session]
    val player = ExoPlayer.Builder(context).build()
    val mediaSession = MediaSession.Builder(context, player).build()
    // [END android_media_playback_app_create_media_session]
}

// [START android_media_playback_app_playback_service]
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    // Create your Player and MediaSession in the onCreate lifecycle event
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
    // [START_EXCLUDE silent]
    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession
    // [END_EXCLUDE]
}
// [END android_media_playback_app_playback_service]

private class SessionCallbackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    // [START android_media_playback_app_on_get_session]
    // This example always accepts the connection request
    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession
    // [END android_media_playback_app_on_get_session]
}

private class PlayerActivity : ComponentActivity() {
    private lateinit var playerView: PlayerView

    // [START android_media_playback_app_connect_ui]
    override fun onStart() {
        // [START_EXCLUDE silent]
        super.onStart()
        // [END_EXCLUDE]
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                playerView.setPlayer(controllerFuture.get())
            },
            MoreExecutors.directExecutor()
        )
    }
    // [END android_media_playback_app_connect_ui]
}
