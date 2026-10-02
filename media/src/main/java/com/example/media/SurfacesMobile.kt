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

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

private const val ACTION_FAVORITES = "ACTION_FAVORITES"

// Wrapper avoids a collision with PlaybackService in PlaybackApp.kt.
private object CustomControlsSnippet {
    @OptIn(UnstableApi::class)
    // [START android_media_surfaces_mobile_custom_command_buttons]
    class PlaybackService : MediaSessionService() {
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
                    // Do custom logic here.
                    saveToFavorites(session.player.currentMediaItem)
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                return super.onCustomCommand(session, controller, customCommand, args)
            }
        }
        // [START_EXCLUDE silent]

        override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
            return mediaSession
        }
        // [END_EXCLUDE]
    }
    // [END android_media_surfaces_mobile_custom_command_buttons]
}

private fun saveToFavorites(item: MediaItem?) {}
