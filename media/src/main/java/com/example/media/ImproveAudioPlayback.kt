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

import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioMixerAttributes
import android.media.AudioTrack
import java.util.concurrent.Executors

class ImproveAudioPlayback(
    private val mAudioManager: AudioManager,
    private val usbDevice: AudioDeviceInfo
) {
    // [START android_media_platform_improve_audio_playback]
    val EXPECTED_FORMAT: AudioFormat = AudioFormat.Builder()
        .setEncoding(AudioFormat.ENCODING_PCM_24BIT_PACKED)
        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
        .setSampleRate(44100)
        .build()

    fun startPlayback() {
        // Query all supported mixer attributes
        val mixerAttributesList: List<AudioMixerAttributes?> =
            mAudioManager.getSupportedMixerAttributes(usbDevice)

        // Find the wanted mixer attributes
        val mixerAttributes = mixerAttributesList.stream()
            .filter { mixerAttr: AudioMixerAttributes? ->
                EXPECTED_FORMAT.equals(
                    mixerAttr!!.format
                )
            }
            .findAny()
            .orElse(null)

        // Register a listener to mixer attributes changed
        val listener = MyPreferredMixerAttributesChangedListener()
        mAudioManager.addOnPreferredMixerAttributesChangedListener(
            Executors.newSingleThreadExecutor(), listener
        )

        // Currently, only media usage over USB devices will be allowed
        val attr: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA).build()
        // Set preferred mixer attributes
        mAudioManager.setPreferredMixerAttributes(
            attr, usbDevice, mixerAttributes!!
        )

        // Start playback, note the playback and the audio format must
        // match what is set when calling `setPreferredMixerAttributes`
        // API.
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(attr)
            .setAudioFormat(mixerAttributes.format)
            .build()

        // Clear all preferred mixer attributes related stuff when
        // playback task is completed
        mAudioManager.clearPreferredMixerAttributes(attr, usbDevice)
        mAudioManager.removeOnPreferredMixerAttributesChangedListener(listener)
    }

    private class MyPreferredMixerAttributesChangedListener :
        AudioManager.OnPreferredMixerAttributesChangedListener {
        override fun onPreferredMixerAttributesChanged(
            attributes: AudioAttributes,
            device: AudioDeviceInfo,
            mixerAttributes: AudioMixerAttributes?
        ) {
            // Do something when preferred mixer attributes changed
        }
    }
    // [END android_media_platform_improve_audio_playback]
}
