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

import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.VolumeShaper

class VolumeShaperSnippets(
    private val myMediaPlayer: MediaPlayer,
    private val myAudioTrack: AudioTrack
) {

    fun createConfig(): VolumeShaper.Configuration {
        // [START android_media_platform_volumeshaper_create_config]
        val config: VolumeShaper.Configuration = VolumeShaper.Configuration.Builder()
            .setDuration(3000)
            .setCurve(floatArrayOf(0f, 1f), floatArrayOf(0f, 1f))
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
            .build()
        // [END android_media_platform_volumeshaper_create_config]
        return config
    }

    fun createShaper(config: VolumeShaper.Configuration) {
        // [START android_media_platform_volumeshaper_create_shaper]
        var volumeShaper = myMediaPlayer.createVolumeShaper(config)
        volumeShaper = myAudioTrack.createVolumeShaper(config)
        // [END android_media_platform_volumeshaper_create_shaper]

        // [START android_media_platform_volumeshaper_apply_play]
        volumeShaper.apply(VolumeShaper.Operation.PLAY)
        // [END android_media_platform_volumeshaper_apply_play]

        // [START android_media_platform_volumeshaper_replace_config]
        val newConfig = VolumeShaper.Configuration.Builder()
            .setDuration(1000)
            .setCurve(floatArrayOf(0f, 0.5f), floatArrayOf(0f, 1f))
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
            .build()
        val join = true
        volumeShaper.replace(newConfig, VolumeShaper.Operation.PLAY, join)
        // [END android_media_platform_volumeshaper_replace_config]
    }
}
