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

import android.content.Context
import android.media.MediaDrm
import android.media.MediaPlayer
import android.net.Uri

class MediaPlayerDrm(private val context: Context) {

    fun syncDrmFlow(mediaPlayer: MediaPlayer?, uri: Uri) {
        // [START android_media_platform_mediaplayer_drm_sync]
        mediaPlayer?.apply {
            setDataSource(context, uri)
            setOnDrmConfigHelper { mp ->
                // optional, for custom configuration
            }
            prepare()
            drmInfo?.also { info ->
                val uuid = info.supportedSchemes[0]
                prepareDrm(uuid)
                val keyRequest = getKeyRequest(null, null, null, MediaDrm.KEY_TYPE_STREAMING, null)
                val keyResponse = byteArrayOf()
                provideKeyResponse(null, keyResponse)
            }

            // MediaPlayer is now ready to use
            start()
            // ...play/pause/resume...
            stop()
            releaseDrm()
        }
        // [END android_media_platform_mediaplayer_drm_sync]
    }
}

// [START android_media_platform_mediaplayer_drm_async]
class AsyncDrmPlayer(
    private val context: Context,
    private val uri: Uri
) : MediaPlayer.OnPreparedListener, MediaPlayer.OnDrmInfoListener {

    private val mediaPlayer = MediaPlayer()

    fun setupAsyncDrm() {
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnDrmInfoListener(this)
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.prepareAsync()
    }

    // If the data source content is protected you receive a call to the onDrmInfo() callback.
    override fun onDrmInfo(mp: MediaPlayer, drmInfo: MediaPlayer.DrmInfo?) {
        drmInfo?.let { info ->
            val uuid = info.supportedSchemes[0]
            mp.prepareDrm(uuid)
            val keyRequest = mp.getKeyRequest(null, null, null, MediaDrm.KEY_TYPE_STREAMING, null)
            val keyResponse = byteArrayOf()
            mp.provideKeyResponse(null, keyResponse)
        }
    }

    // When prepareAsync() finishes, you receive a call to the onPrepared() callback.
    // If there is a DRM, onDrmInfo() sets it up before executing this callback,
    // so you can start the player.
    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }
}
// [END android_media_platform_mediaplayer_drm_async]
