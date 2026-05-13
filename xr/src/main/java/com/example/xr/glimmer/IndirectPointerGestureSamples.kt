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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.onIndirectPointerGesture

// [START xr_glimmer_on_indirect_pointer_gesture]
@Composable
fun OnIndirectPointerGestureSample() {
    var lastGesture by remember { mutableStateOf("None") }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .onIndirectPointerGesture(
                    enabled = true,
                    onSwipeForward = { lastGesture = "Forward" },
                    onSwipeBackward = { lastGesture = "Backward" },
                    onClick = { lastGesture = "Click" },
                )
                .focusTarget(),
        contentAlignment = Alignment.Center,
    ) {
        Text("Last Gesture: $lastGesture")
    }
}
// [END xr_glimmer_on_indirect_pointer_gesture]
