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

// Example state variable to hold the normalized level
val audioLevel = MutableStateFlow(0f)

fun startVoiceInput(context: Context) {
    // [START androidxr_glimmer_voice_input_normalization]
    // Example state variables to hold the normalized level
    val audioLevel = MutableStateFlow(0f)
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val listener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {
            // Normalize raw dB (~0–10) to 0.0-1.0 for the indicator
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

    // Attach the listener and start recognizing speech
    speechRecognizer.setRecognitionListener(listener)

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }
    speechRecognizer.startListening(intent)
    // [END androidxr_glimmer_voice_input_normalization]
}

// [START androidxr_glimmer_voice_input_indicator]
@Composable
fun VoiceInputExample() {
    // Collect the flow as Compose State so the UI reacts to changes
    val currentLevel by audioLevel.collectAsState()

    VoiceInputIndicator(
        // The component responds to the level provided, showing a visual representation of the audio intensity
        level = { currentLevel },
        modifier = Modifier.size(64.dp)
    )
}
// [END androidxr_glimmer_voice_input_indicator]
