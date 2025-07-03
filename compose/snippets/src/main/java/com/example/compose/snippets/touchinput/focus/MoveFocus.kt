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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
internal fun MoveFocusExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("Move focus", modifier = modifier) {
        FocusAdvancing()
        SubSection("Clear focus with ESC") {
            ClearFocusWithEscKey(
                onClick = onClick,
                modifier = Modifier.focusGroup(),
            )
        }
    }
}

@Composable
fun ClearFocusWithEscKey(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_clear_focus_esc]
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier.onPreviewKeyEvent {
            if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                focusManager.clearFocus()
                true
            } else {
                false
            }
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var inputText by rememberSaveable { mutableStateOf("") }
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
        )
        Button(onClick = onClick) {
            Text("Send")
        }
    }
    // [END android_compose_touchinput_clear_focus_esc]
}
