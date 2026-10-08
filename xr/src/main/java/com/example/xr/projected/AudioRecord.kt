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

import android.Manifest
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission

// Define the audio format
// the sample rate is limited to 16kHz, with support for mono or stereo channel configurations.
private val audioFormat = AudioFormat.Builder()
    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
    .setSampleRate(16000)
    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
    .build()

// Fetch the minimum required size and use it (or a small multiple)
// to ensure the "shorter chunks" recommended for low latency.
private val bufferSize = AudioRecord.getMinBufferSize(
    16000,
    AudioFormat.CHANNEL_IN_MONO,
    AudioFormat.ENCODING_PCM_16BIT
).coerceAtLeast(1024)

/**
 * Demonstrates how to record audio using Bluetooth HFP
 */
@RequiresPermission(allOf = [Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_CONNECT])
private fun startBluetoothAudioRecording(context: Context) {
    // [START androidxr_bluetooth_audio_record]
    val audioManager = context.getSystemService(AudioManager::class.java) ?: return
    val devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
    val hfpDevice = devices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }

    hfpDevice?.let { device ->
        val audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .build()

        // Route recording to the Bluetooth device
        audioRecord.setPreferredDevice(device)
        audioManager.setCommunicationDevice(device)

        audioRecord.startRecording()
        // [END androidxr_bluetooth_audio_record]

        // Stop and release when done.
        audioRecord.stop()
        audioRecord.release()
        audioManager.clearCommunicationDevice()
    }
}
