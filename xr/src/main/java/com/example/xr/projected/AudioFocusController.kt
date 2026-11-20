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
    // Use ProjectedContext.createHostDeviceContext(context) to get the
    // AudioManager that participates in the phone's primary audio focus stack.
    // context can be any context: (projected, host, service, app).
    @OptIn(ExperimentalProjectedApi::class)
    val hostAudioManager: AudioManager =
        ProjectedContext.createHostDeviceContext(context)
            .getSystemService(AudioManager::class.java)

    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        // Or CONTENT_TYPE_MUSIC
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    var hasAudioFocus: Boolean = false
        private set

    private val focusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            hasAudioFocus = when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> true
                else -> false
            }
            onFocusChanged(focusChange)
        }

    private val focusRequest: AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(focusChangeListener)
            .build()

    /**
     * Request host audio focus once the projected Activity reaches Lifecycle.State.RESUMED
     * (for example, inside DefaultLifecycleObserver.onResume).
     */
    fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        val granted = hostAudioManager.requestAudioFocus(focusRequest) ==
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        hasAudioFocus = granted
        return granted
    }
    // [END androidxr_projected_audio_focus]

    fun abandonAudioFocus() {
        if (hasAudioFocus) {
            hostAudioManager.abandonAudioFocusRequest(focusRequest)
            hasAudioFocus = false
        }
    }
}
