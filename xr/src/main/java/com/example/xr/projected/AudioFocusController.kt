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

package com.example.xr.projected

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi

class AudioFocusController(
    context: Context,
    private val onFocusChanged: (Int) -> Unit,
) {
    // [START androidxr_projected_audio_focus]
    // Use ProjectedContext.createHostDeviceContext() to get the
    // AudioManager that participates in the phone's primary audio focus stack.
    // 'context' can be any context: (projected, host, service, app).
    // It will be converted to the correct version.
    @OptIn(ExperimentalProjectedApi::class)
    private val hostAudioManager: AudioManager =
        ProjectedContext.createHostDeviceContext(context)
            .getSystemService(AudioManager::class.java)

    private val focusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            onFocusChanged(focusChange)
        }

    private var activeFocusRequest: AudioFocusRequest? = null

    fun requestAudioFocus(): Boolean {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH) // Or CONTENT_TYPE_MUSIC
            .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(focusChangeListener)
            .build()

        activeFocusRequest = request
        return hostAudioManager.requestAudioFocus(request) ==
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    // [END androidxr_projected_audio_focus]

    fun abandonAudioFocus() {
        activeFocusRequest?.let { request ->
            hostAudioManager.abandonAudioFocusRequest(request)
            activeFocusRequest = null
        }
    }
}
