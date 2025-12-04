/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.wear.snippets.audio

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.WearUnsuitableOutputPlaybackSuppressionResolverListener

class AudioActivity : ComponentActivity() {

    // [START android_wear_audio_detect_devices]
    private val audioManager: AudioManager by lazy {
        getSystemService(AUDIO_SERVICE) as AudioManager
    }

    fun audioOutputAvailable(type: Int): Boolean {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false
        }
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { it.type == type }
    }
    // [END android_wear_audio_detect_devices]

    @OptIn(UnstableApi::class)
    fun buildExoPlayer(context: Context): ExoPlayer {
        // [START android_wear_exoplayer_audio_output_suppression]
        val exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setSuppressPlaybackOnUnsuitableOutput(true)
            .build()
        // [END android_wear_exoplayer_audio_output_suppression]
        // [START android_wear_exoplayer_audio_output_suppression_listener]
        exoPlayer.addListener(WearUnsuitableOutputPlaybackSuppressionResolverListener(context))
        // [END android_wear_exoplayer_audio_output_suppression_listener]
        return exoPlayer
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // [START android_wear_audio_detect_devices_sample]
        val hasSpeaker = audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
        val hasBluetoothHeadset = audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)
        val hasBLEBroadcast = audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_BROADCAST)
        val hasBLEHeadset = audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_HEADSET)
        val hasBLESpeaker = audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_SPEAKER)
        // [END android_wear_audio_detect_devices_sample]
        println("Has speaker: $hasSpeaker")
        println("Has Bluetooth headset: $hasBluetoothHeadset")
        println("Has BLE broadcast: $hasBLEBroadcast")
        println("Has BLE headset: $hasBLEHeadset")
        println("Has BLE speaker: $hasBLESpeaker")

        // [START android_wear_audio_register_callback]
        val audioDeviceCallback =
            object : AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                    super.onAudioDevicesAdded(addedDevices)
                    if (audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) ||
                        audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_BROADCAST) ||
                        audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_HEADSET) ||
                        audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_SPEAKER)
                    ) {
                        // A Bluetooth or BLE device is connected and available for playback.
                    }
                }
                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                    super.onAudioDevicesRemoved(removedDevices)
                    if (!(audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) &&
                        !(audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_BROADCAST)) &&
                        !(audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_HEADSET)) &&
                        !(audioOutputAvailable(AudioDeviceInfo.TYPE_BLE_SPEAKER))
                    ) {
                        // No Bluetooth or BLE devices are connected anymore.
                    }
                }
            }

        audioManager.registerAudioDeviceCallback(audioDeviceCallback, /*handler=*/ null)
        // [END android_wear_audio_register_callback]
    }
}
