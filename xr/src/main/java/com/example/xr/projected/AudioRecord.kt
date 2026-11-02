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

/**
 * Demonstrates how to record audio from a Bluetooth HFP device.
 */
@RequiresPermission(allOf = [Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_CONNECT])
private fun recordBluetoothHfpAudio(context: Context) {
    // [START androidxr_bluetooth_audio_record]
    val audioManager = context.getSystemService(AudioManager::class.java) ?: return
    val hfpDevice = audioManager.availableCommunicationDevices
        .firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO } ?: return

    // HFP input is limited to 16 kHz, with mono or stereo channel configurations.
    val sampleRate = 16_000
    val channelMask = AudioFormat.CHANNEL_IN_MONO
    val encoding = AudioFormat.ENCODING_PCM_16BIT

    // Use a small multiple of the minimum buffer size to keep chunks short for low latency.
    val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelMask, encoding)
    if (minBufferSize <= 0) return

    val audioRecord = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        .setAudioFormat(
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(channelMask)
                .setEncoding(encoding)
                .build()
        )
        .setBufferSizeInBytes(minBufferSize * 2)
        .build()

    try {
        // Route voice communication audio through the Bluetooth device.
        if (!audioManager.setCommunicationDevice(hfpDevice)) return
        audioRecord.startRecording()
        // Read audio with audioRecord.read() on a background thread.
    } finally {
        // Stop and release when done.
        if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) audioRecord.stop()
        audioRecord.release()
        audioManager.clearCommunicationDevice()
    }
    // [END androidxr_bluetooth_audio_record]
}
