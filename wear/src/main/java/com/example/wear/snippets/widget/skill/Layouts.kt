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

package com.example.wear.snippets.widget.skill

// [START android_wear_widget_skill_adaptive_layout]
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.remote.material3.RemoteText

@RemoteComposable
@Composable
fun AdaptiveCardContent(
    isLarge: Boolean,
    title: String,
    subtitle: String,
) {
    RemoteColumn(
        modifier = RemoteModifier.fillMaxSize()
            .padding(if (isLarge) 12.rdp else 6.rdp),
        verticalArrangement = RemoteArrangement.Center,
        horizontalAlignment = RemoteAlignment.CenterHorizontally,
    ) {
        RemoteText(
            text = title.rs,
            color = Color(0xFFE6E1E5).rc,
        )
        if (isLarge) {
            RemoteText(
                text = subtitle.rs,
                color = Color(0xFFD0BCFF).rc,
                modifier = RemoteModifier.padding(top = 4.rdp),
            )
        }
    }
}
// [END android_wear_widget_skill_adaptive_layout]
