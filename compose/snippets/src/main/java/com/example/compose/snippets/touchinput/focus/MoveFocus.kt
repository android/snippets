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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager

@Composable
private fun MoveFocusDirectionSample() {
    // [START android_compose_touchinput_focus_move_direction]
    val focusManager = LocalFocusManager.current

    Button(
        onClick = {
            focusManager.moveFocus(FocusDirection.Next)
        }
    ) {
        Text("Next Field")
    }
    // [END android_compose_touchinput_focus_move_direction]
}

@Composable
private fun ClearFocusSample() {
    // [START android_compose_touchinput_focus_clear_focus]
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.onPreviewKeyEvent { keyEvent ->
            if (keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyUp) {
                focusManager.clearFocus()
                true
            } else {
                false
            }
        }
    )
    // [END android_compose_touchinput_focus_clear_focus]
}
