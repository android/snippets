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

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat

class AudioOutputActivity : Activity() {

    override fun onResume() {
        super.onResume()
        // [START android_media_platform_output_volume_control_stream]
        volumeControlStream = AudioManager.STREAM_MUSIC
        // [END android_media_platform_output_volume_control_stream]
    }

    // [START android_media_platform_output_becoming_noisy_receiver]
    private class BecomingNoisyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                // Pause the playback
            }
        }
    }
    // [END android_media_platform_output_becoming_noisy_receiver]

    fun setupMediaSession(context: Context) {
        // [START android_media_platform_output_register_noisy_receiver]
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()

        val callback = object : MediaSessionCompat.Callback() {

            override fun onPlay() {
                context.registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
            }

            override fun onStop() {
                context.unregisterReceiver(myNoisyAudioStreamReceiver)
            }
        }
        // [END android_media_platform_output_register_noisy_receiver]
    }
}
