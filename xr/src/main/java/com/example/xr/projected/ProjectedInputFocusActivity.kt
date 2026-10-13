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

package com.example.xr.projected

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedActivityCompat
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.launch

// [START androidxr_projected_input_receiver_activity]
@OptIn(ExperimentalProjectedApi::class)
class ProjectedInputFocusActivity : ComponentActivity() {

    private lateinit var textToSpeech: TextToSpeech

    // [START androidxr_projected_input_receiver_pending_intent]
    // Build an explicit Intent pointing to this projected activity and wrap it in a
    // PendingIntent so the system can relaunch it and route physical input to it.
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(this, ProjectedInputFocusActivity::class.java)

        PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    // [END androidxr_projected_input_receiver_pending_intent]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the readout once the TTS engine is ready.
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                startTurnByTurnReadout()
            }
        }
    }

    // [START androidxr_projected_input_receiver_register]
    private fun startTurnByTurnReadout() {
        lifecycleScope.launch {
            // Create or obtain the ProjectedActivityCompat instance
            val projectedActivity = ProjectedActivityCompat.create(this@ProjectedInputFocusActivity)

            // Register the PendingIntent as the designated input receiver
            projectedActivity.setActivityAsInputReceiver(pendingIntent)
        }

        // Trigger perceptible audio
        textToSpeech.speak("In 500 feet, turn left", TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID")
    }
    // [END androidxr_projected_input_receiver_register]

    // [START androidxr_projected_input_receiver_clear]
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            val projectedActivity = ProjectedActivityCompat.create(this@ProjectedInputFocusActivity)
            projectedActivity.clearActivityAsInputReceiver()
        }
        // [START_EXCLUDE]
        textToSpeech.shutdown()
        // [END_EXCLUDE]
    }
    // [END androidxr_projected_input_receiver_clear]
}
// [END androidxr_projected_input_receiver_activity]
