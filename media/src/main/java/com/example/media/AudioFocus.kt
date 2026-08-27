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
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaControllerCompat
import java.util.concurrent.TimeUnit

private class AudioFocusManager(private val context: Context, private val mediaController: MediaControllerCompat) {
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest
    private val handler = Handler(Looper.getMainLooper())
    private var delayedStopRunnable = Runnable {
        mediaController.transportControls.stop()
    }

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        onAudioFocusChange(focusChange)
    }

    private fun requestFocusPostOreo() {
        // [START android_media_audio_focus_request]
        // initializing variables for audio focus and playback management
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(afChangeListener, handler)
            build()
        }
        val focusLock = Any()

        var playbackDelayed = false
        var playbackNowAuthorized = false

        // requesting audio focus and processing the response
        val res = audioManager.requestAudioFocus(focusRequest)
        synchronized(focusLock) {
            playbackNowAuthorized = when (res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> false
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    playbackNow()
                    true
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                    playbackDelayed = true
                    false
                }
                else -> false
            }
        }
        // [END android_media_audio_focus_request]
    }

    private var playbackDelayed = false
    private var resumeOnFocusGain = false
    private val focusLock = Any()

    private fun onAudioFocusChange(focusChange: Int) {
        // [START android_media_audio_focus_request_listener_impl]
        // implementing OnAudioFocusChangeListener to react to focus changes
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->
                if (playbackDelayed || resumeOnFocusGain) {
                    synchronized(focusLock) {
                        playbackDelayed = false
                        resumeOnFocusGain = false
                    }
                    playbackNow()
                }
            AudioManager.AUDIOFOCUS_LOSS -> {
                synchronized(focusLock) {
                    resumeOnFocusGain = false
                    playbackDelayed = false
                }
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                synchronized(focusLock) {
                    // only resume if playback is being interrupted
                    resumeOnFocusGain = isPlaying()
                    playbackDelayed = false
                }
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // ... pausing or ducking depends on your app
            }
        }
        // [END android_media_audio_focus_request_listener_impl]
    }

    @Suppress("DEPRECATION")
    private fun requestFocusPreOreo() {
        // [START android_media_audio_focus_request_pre_o]
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        lateinit var afChangeListener: AudioManager.OnAudioFocusChangeListener

        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        ...
        // [START_EXCLUDE silent]
        */
        // [END_EXCLUDE]
        // Request audio focus for playback
        val result: Int = audioManager.requestAudioFocus(
                afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback
        }
        // [END android_media_audio_focus_request_pre_o]
    }

    @Suppress("DEPRECATION")
    private fun abandonFocusExample(afChangeListener: AudioManager.OnAudioFocusChangeListener) {
        // [START android_media_audio_focus_abandon]
        audioManager.abandonAudioFocus(afChangeListener)
        // [END android_media_audio_focus_abandon]
    }

    private fun setupHandlerChangeListener() {
        // [START android_media_audio_focus_change_listener]
        val handler = Handler()
        val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Permanent loss of audio focus
                    // Pause playback immediately
                    mediaController.transportControls.pause()
                    // Wait 30 seconds before stopping playback
                    handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30))
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // Pause playback
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // Lower the volume, keep playing
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    // Your app has been granted audio focus again
                    // Raise volume to normal, restart playback if necessary
                }
            }
        }
        // [END android_media_audio_focus_change_listener]

        // [START android_media_audio_focus_delayed_stop_runnable]
        var delayedStopRunnable = Runnable {
            mediaController.transportControls.stop()
        }
        // [END android_media_audio_focus_delayed_stop_runnable]
    }

    private fun playbackNow() {}
    private fun pausePlayback() {}
    private fun isPlaying(): Boolean = true
}
