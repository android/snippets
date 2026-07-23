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

package com.example.tv.playback

import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioProfile
import android.media.AudioTrack
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi

abstract class AudioPlayer {
    abstract val audioTrack: AudioTrack
    fun addAudioTrackWriteErrorListener(listener: (Int) -> Unit) {}
}

class AudioCapabilities(
    private val audioManager: AudioManager,
    private val audioPlayer: AudioPlayer,
    private val handler: Handler
) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkDirectPlaybackSupport() {
// [START android_tv_playback_audio_capabilities_direct_playback_support]
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_E_AC3)
            .setChannelMask(AudioFormat.CHANNEL_OUT_5POINT1)
            .setSampleRate(48000)
            .build()
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        if (AudioManager.getDirectPlaybackSupport(format, attributes) !=
            AudioManager.DIRECT_PLAYBACK_NOT_SUPPORTED
        ) {
            // The format and attributes are supported for direct playback
            // on the currently active routed audio path
        } else {
            // The format and attributes are NOT supported for direct playback
            // on the currently active routed audio path
        }
// [END android_tv_playback_audio_capabilities_direct_playback_support]
    }

// [START android_tv_playback_audio_capabilities_find_best_format]
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun findBestAudioFormat(audioAttributes: AudioAttributes): AudioFormat {
        val preferredFormats = listOf(
            AudioFormat.ENCODING_E_AC3,
            AudioFormat.ENCODING_AC3,
            AudioFormat.ENCODING_PCM_16BIT,
            AudioFormat.ENCODING_DEFAULT
        )
        val audioProfiles = audioManager.getDirectProfilesForAttributes(audioAttributes)
        val bestAudioProfile = preferredFormats.firstNotNullOf { format ->
            audioProfiles.firstOrNull { it.format == format }
        }
        val sampleRate = findBestSampleRate(bestAudioProfile)
        val channelMask = findBestChannelMask(bestAudioProfile)
        return AudioFormat.Builder()
            .setEncoding(bestAudioProfile.format)
            .setSampleRate(sampleRate)
            .setChannelMask(channelMask)
            .build()
    }
// [END android_tv_playback_audio_capabilities_find_best_format]

    private fun findBestSampleRate(audioProfile: AudioProfile): Int {
        return audioProfile.sampleRates.maxOrNull() ?: 48000
    }

    private fun findBestChannelMask(audioProfile: AudioProfile): Int {
        return audioProfile.channelMasks.firstOrNull() ?: AudioFormat.CHANNEL_OUT_STEREO
    }

    private fun findDefaultAudioDeviceInfo(): AudioDeviceInfo? = null
    private fun restartAudioTrack(audioDeviceInfo: AudioDeviceInfo?) {}
    private fun needsAudioFormatChange(audioDeviceInfo: AudioDeviceInfo): Boolean = false

    fun registerRoutingListener() {
// [START android_tv_playback_audio_capabilities_routing_listener]
        // audioPlayer is a wrapper around an AudioTrack
        // which calls a callback for an AudioTrack write error
        audioPlayer.addAudioTrackWriteErrorListener {
            // error code can be checked here,
            // in case of write error try to recreate the audio track
            restartAudioTrack(findDefaultAudioDeviceInfo())
        }

        audioPlayer.audioTrack.addOnRoutingChangedListener({ audioRouting ->
            audioRouting?.routedDevice?.let { audioDeviceInfo ->
                // use the updated audio routed device to determine
                // what audio format should be used
                if (needsAudioFormatChange(audioDeviceInfo)) {
                    restartAudioTrack(audioDeviceInfo)
                }
            }
        }, handler)
// [END android_tv_playback_audio_capabilities_routing_listener]
    }
}
