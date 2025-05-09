/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.wear.snippets.voiceinput

/*
 * Copyright 2021 The Android Open Source Project
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

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults.behavior
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.wear.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Chip

/**
 * Shows voice input option
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun VoiceInputScreen() {
    AppScaffold {
        // [START android_wear_voice_input]
        var textForVoiceInput by remember { mutableStateOf("") }

        val voiceLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { activityResult ->
                // This is where you process the intent and extract the speech text from the intent.
                activityResult.data?.let { data ->
                    val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textForVoiceInput = results?.get(0) ?: "None"
                }
            }

        val scrollState = rememberScrollState()

        ScreenScaffold(scrollState = scrollState) {
            // rest of implementation here
            // [START_EXCLUDE]
            val padding = ScalingLazyColumnDefaults.padding(
                first = ItemType.Text,
                last = ItemType.Chip
            )()
            val focusRequester = rememberActiveFocusRequester()
            // [END_EXCLUDE]
            Column(
                // rest of implementation here
                // [START_EXCLUDE]
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .rotaryScrollable(
                        behavior = behavior(scrollableState = scrollState),
                        focusRequester = focusRequester,
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                // [END_EXCLUDE]

                // Create an intent that can start the Speech Recognizer activity
                val voiceIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_PROMPT,
                        stringResource(R.string.voice_text_entry_label)
                    )
                }
                // Invoke the process from a chip
                Chip(
                    onClick = {
                        voiceLauncher.launch(voiceIntent)
                    },
                    label = stringResource(R.string.voice_input_label),
                    secondaryLabel = textForVoiceInput
                )
            }
        }
        // [END android_wear_voice_input]
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun VoiceInputScreenPreview() {
    VoiceInputScreen()
}
