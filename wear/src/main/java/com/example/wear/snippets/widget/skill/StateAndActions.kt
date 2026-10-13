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

@file:SuppressLint("RestrictedApi")

package com.example.wear.snippets.widget.skill

// [START android_wear_widget_skill_state_adjuster]
// [START_EXCLUDE silent]
import android.annotation.SuppressLint
// [END_EXCLUDE]
import androidx.compose.remote.creation.compose.action.valueChange
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteRow
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rememberMutableRemoteInt
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
import androidx.wear.compose.remote.material3.RemoteButton
import androidx.wear.compose.remote.material3.RemoteText

@RemoteComposable
@Composable
fun StatefulAdjusterSection() {
    val value = rememberMutableRemoteInt(0)

    RemoteColumn(
        modifier = RemoteModifier.fillMaxSize(),
        verticalArrangement = RemoteArrangement.Center,
        horizontalAlignment = RemoteAlignment.CenterHorizontally,
    ) {
        // Call value.toRemoteString() as a member method (no extension import)
        RemoteText(text = "Value: ".rs + value.toRemoteString())
        RemoteRow(horizontalArrangement = RemoteArrangement.spacedBy(8.rdp)) {
            RemoteButton(onClick = valueChange(value, value - 1)) {
                RemoteText("-".rs)
            }
            RemoteButton(onClick = valueChange(value, value + 1)) {
                RemoteText("+".rs)
            }
        }
    }
}
// [END android_wear_widget_skill_state_adjuster]
