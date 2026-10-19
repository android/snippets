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

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

@Composable
private fun CaptureFocusSample() {
    // [START android_compose_touchinput_focus_capture_focus]
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    focusRequester.captureFocus()
                }
            }
    )
    // [END android_compose_touchinput_focus_capture_focus]
}

@Composable
private fun FreeFocusSample() {
    val focusRequester = remember { FocusRequester() }

    // [START android_compose_touchinput_focus_free_focus]
    IconButton(
        onClick = {
            focusRequester.freeFocus()
        }
    ) {
        Icon(Icons.Default.Close, contentDescription = "Exit focus lock")
    }
    // [END android_compose_touchinput_focus_free_focus]
}
