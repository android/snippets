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

package com.example.xr.glimmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.VoiceInputIndicator
import kotlinx.coroutines.flow.MutableStateFlow

// Example state variable to hold the normalized level (0.0 to 1.0)
// that the VoiceInputIndicator component expects.
val audioLevel = MutableStateFlow(0f)

/**
 * Demonstrates how to initialize a [SpeechRecognizer] and normalize the audio RMS dB levels
 * for use with a [VoiceInputIndicator].
 */
fun startVoiceInput(context: Context) {
    // [START androidxr_glimmer_voice_input_normalization]
    // Example state variable to hold the normalized level (0.0 to 1.0)
    // that the VoiceInputIndicator component expects.
    val audioLevel = MutableStateFlow(0f)

    // Initialize the Android SpeechRecognizer
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    // Listener to capture speech events and audio level changes
    val listener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {
            // Normalize raw dB level to a 0.0-1.0 range.
            // Android SpeechRecognizer's rmsdB typically ranges from 0 to ~10.
            audioLevel.value = ((rmsdB - 1f) / 9f).coerceIn(0f, 1f)
        }

        // ... Implement other required RecognitionListener methods ...
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onResults(results: Bundle?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
    }

    // Attach the listener to the recognizer
    speechRecognizer.setRecognitionListener(listener)

    // Create an intent to specify the recognition model and behavior
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }
    // Begin listening for audio input
    speechRecognizer.startListening(intent)
    // [END androidxr_glimmer_voice_input_normalization]
}

/**
 * A Composable that displays a [VoiceInputIndicator] reacting to the [audioLevel] state.
 */

// [START androidxr_glimmer_voice_input_indicator]
@Composable
fun VoiceInputExample() {
    // Collect the flow as Compose State so the UI reacts to changes in real-time.
    val currentLevel by audioLevel.collectAsState()

    VoiceInputIndicator(
        // The VoiceInputIndicator component provides a visual "pulse" or indicator
        // that changes based on the 'level' lambda, which returns a Float between 0.0 and 1.0.
        level = { currentLevel },
        modifier = Modifier.size(64.dp)
    )
}
// [END androidxr_glimmer_voice_input_indicator]
