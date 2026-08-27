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
import android.media.AudioFormat
import android.media.AudioManager
import android.media.MediaFormat
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

private fun querySpatializer(audioManager: AudioManager) {
    // [START android_media_spatial_audio_get_spatializer]
    val spatializer = audioManager.spatializer
    // [END android_media_spatial_audio_get_spatializer]
}

@OptIn(UnstableApi::class)
private fun disableChannelConstraintsPlayer(context: Context, exoPlayer: ExoPlayer) {
    // [START android_media_spatial_audio_disable_channel_constraints_player]
    exoPlayer.trackSelectionParameters = DefaultTrackSelector.Parameters.Builder(context)
      .setConstrainAudioChannelCountToDeviceCapabilities(false)
      .build()
    // [END android_media_spatial_audio_disable_channel_constraints_player]
}

@OptIn(UnstableApi::class)
private fun disableChannelConstraintsSelector(context: Context) {
    // [START android_media_spatial_audio_disable_channel_constraints_selector]
    val trackSelector = DefaultTrackSelector(context)
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    ...
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]
    trackSelector.parameters = trackSelector.buildUponParameters()
      .setConstrainAudioChannelCountToDeviceCapabilities(false)
      .build()
    // [END android_media_spatial_audio_disable_channel_constraints_selector]
}

private fun setMaxAudioChannels(exoPlayer: ExoPlayer) {
    // [START android_media_spatial_audio_set_max_channels]
    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
      .buildUpon()
      .setMaxAudioChannelCount(2)
      .build()
    // [END android_media_spatial_audio_set_max_channels]
}

private fun setMaxOutputChannels() {
    // [START android_media_spatial_audio_max_output_channels]
    val mediaFormat = MediaFormat()
    mediaFormat.setInteger(MediaFormat.KEY_MAX_OUTPUT_CHANNEL_COUNT, 99)
    // [END android_media_spatial_audio_max_output_channels]
}

private fun buildSpatialAudioFormat() {
    // [START android_media_spatial_audio_audio_format]
    val audioFormat = AudioFormat.Builder()
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setChannelMask(AudioFormat.CHANNEL_OUT_5POINT1)
        .build()
    // [END android_media_spatial_audio_audio_format]
}
