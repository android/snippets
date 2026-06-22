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

package com.example.tv.ui

import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.activity.ComponentActivity

private class MediaData(
    val displayTitle: String,
    val displaySubtitle: String,
    val artUri: String,
    val title: String,
    val artist: String,
    val artBitmap: Bitmap
)

private class MediaSessionCallback : MediaSession.Callback()

class MediaSessionActivity : ComponentActivity() {
    private lateinit var session: MediaSession
    private var mState = PlaybackState.STATE_NONE
    private var mediaPlayer: MediaPlayer? = null
    private var playingQueue: List<Any>? = null
    private var currentIndexOnQueue = 0

    fun createMediaSession() {
        // [START android_tv_media_session_create]
        session = MediaSession(this, "MusicService").apply {
            setCallback(MediaSessionCallback())
            setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
        }
        // [END android_tv_media_session_create]
    }

    private fun tryToGetAudioFocus() {}

    // [START android_tv_media_session_start]
    fun handlePlayRequest() {
        tryToGetAudioFocus()

        if (!session.isActive) {
            session.isActive = true
        }
        // ...
    }
    // [END android_tv_media_session_start]

    // [START android_tv_media_session_update_state]
    private fun updatePlaybackState() {
        val position: Long =
            mediaPlayer
                ?.takeIf { it.isPlaying }
                ?.currentPosition?.toLong()
                ?: PlaybackState.PLAYBACK_POSITION_UNKNOWN

        val stateBuilder = PlaybackState.Builder()
            .setActions(getAvailableActions()).apply {
                setState(mState, position, 1.0f)
            }
        session.setPlaybackState(stateBuilder.build())
    }

    private fun getAvailableActions(): Long {
        var actions = (
            PlaybackState.ACTION_PLAY_PAUSE
                or PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackState.ACTION_PLAY_FROM_SEARCH
            )

        playingQueue?.takeIf { it.isNotEmpty() }?.apply {
            actions = if (mState == PlaybackState.STATE_PLAYING) {
                actions or PlaybackState.ACTION_PAUSE
            } else {
                actions or PlaybackState.ACTION_PLAY
            }
            if (currentIndexOnQueue > 0) {
                actions = actions or PlaybackState.ACTION_SKIP_TO_PREVIOUS
            }
            if (currentIndexOnQueue < size - 1) {
                actions = actions or PlaybackState.ACTION_SKIP_TO_NEXT
            }
        }
        return actions
    }
    // [END android_tv_media_session_update_state]

    // [START android_tv_media_session_update_metadata]
    private fun updateMetadata(myData: MediaData) {
        val metadataBuilder = MediaMetadata.Builder().apply {
            // To provide most control over how an item is displayed set the
            // display fields in the metadata
            putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, myData.displayTitle)
            putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, myData.displaySubtitle)
            putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, myData.artUri)
            // And at minimum the title and artist for legacy support
            putString(MediaMetadata.METADATA_KEY_TITLE, myData.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, myData.artist)
            // A small bitmap for the artwork is also recommended
            putBitmap(MediaMetadata.METADATA_KEY_ART, myData.artBitmap)
            // Add any other fields you have for your data as well
        }
        session.setMetadata(metadataBuilder.build())
    }
    // [END android_tv_media_session_update_metadata]
}
