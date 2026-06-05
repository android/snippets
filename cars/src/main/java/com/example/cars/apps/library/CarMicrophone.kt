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

package com.example.cars.apps.library

import android.Manifest
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.car.app.CarContext
import androidx.car.app.annotations.RequiresCarApi
import androidx.car.app.media.CarAudioRecord

@RequiresCarApi(5)
class CarMicrophone(private val carContext: CarContext) {

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun recordAudio() {
        // [START android_cars_apps_library_car_microphone_record]
        val carAudioRecord = CarAudioRecord.create(carContext)
        carAudioRecord.startRecording()

        val data = ByteArray(CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE)
        while (carAudioRecord.read(data, 0, CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE) >= 0) {
            // Use data array
            // Potentially call carAudioRecord.stopRecording() if your processing finds end of speech
        }
        carAudioRecord.stopRecording()
        // [END android_cars_apps_library_car_microphone_record]
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @RequiresApi(Build.VERSION_CODES.O)
    fun acquireAudioFocusAndRecord() {
        // [START android_cars_apps_library_car_microphone_audio_focus]
        val carAudioRecord = CarAudioRecord.create(carContext)

        // Take audio focus so that user's media is not recorded
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            // Use the most appropriate usage type for your use case
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .build()

        val audioFocusRequest =
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { state: Int ->
                    if (state == AudioManager.AUDIOFOCUS_LOSS) {
                        // Stop recording if audio focus is lost
                        carAudioRecord.stopRecording()
                    }
                }
                .build()

        val audioManager = carContext.getSystemService(AudioManager::class.java)
        if (audioManager == null || audioManager.requestAudioFocus(audioFocusRequest)
            != AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        ) {
            // Don't record if the focus isn't granted
            return
        }

        carAudioRecord.startRecording()
        // Process the audio and abandon the AudioFocusRequest when done
        // [END android_cars_apps_library_car_microphone_audio_focus]
    }
}
