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

package com.example.wear.snippets.alwayson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.AmbientMode
import androidx.wear.compose.foundation.AmbientTickEffect
import androidx.wear.compose.foundation.LocalAmbientModeManager
import androidx.wear.compose.foundation.rememberAmbientModeManager
import androidx.wear.compose.material3.Text

@Composable
fun AmbientModeBasicSample() {
    // [START android_wear_ongoing_activity_ambient_compose]
    // In a production application, the AmbientModeManager should be instantiated and provided at
    // the highest level of the Compose hierarchy (typically in the host Activity's setContent
    // block) using a CompositionLocalProvider. This ensures proper lifecycle management and
    // broad accessibility.

    // For this self-contained demo, AmbientModeManager is created and provided locally:
    val activityAmbientModeManager = rememberAmbientModeManager()
    CompositionLocalProvider(LocalAmbientModeManager provides activityAmbientModeManager) {
        val ambientModeManager = LocalAmbientModeManager.current
        val ambientMode = ambientModeManager?.currentAmbientMode

        ambientModeManager?.AmbientTickEffect {
            // While device is in ambient mode, update properties every minute or so
            // ...
        }

        // [START_EXCLUDE]
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            // [END_EXCLUDE]
            val ambientModeName =
                when (ambientMode) {
                    is AmbientMode.Interactive -> "Interactive"
                    is AmbientMode.Ambient -> "Ambient"
                    else -> "Unknown"
                }

            Text(text = "$ambientModeName Mode")
            // [START_EXCLUDE]
        }
        // [END_EXCLUDE]
    }
    // [END android_wear_ongoing_activity_ambient_compose]
}
