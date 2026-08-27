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

import android.app.Activity
import android.content.ComponentName
import android.content.Context
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

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // [START android_media_playback_app_on_get_session]
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
    // [END android_media_playback_app_on_get_session]
}
// [END android_media_playback_app_playback_service]

private class PlayerActivity : Activity() {
    private lateinit var playerView: PlayerView

    // [START android_media_playback_app_connect_ui]
    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            // Call controllerFuture.get() to retrieve the MediaController.
            // MediaController implements the Player interface, so it can be
            // attached to the PlayerView UI component.
            playerView.player = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }
    // [END android_media_playback_app_connect_ui]
}
