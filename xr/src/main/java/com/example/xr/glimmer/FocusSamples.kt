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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Text

// [START xr_glimmer_focus_activity]

// [START xr_glimmer_focus_activity]
class GlassesActivityExample : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        @OptIn(ExperimentalComposeUiApi::class)
        ComposeUiFlags.isInitialFocusOnFocusableAvailable = true
        super.onCreate(savedInstanceState)
    }
}
// [END xr_glimmer_focus_activity]


// [START xr_glimmer_focus_group]
@Composable
fun FocusSample(initialFocus: FocusRequester) {
    Box(
        modifier =
            Modifier.focusProperties {
                    onEnter = {
                        initialFocus.requestFocus()
                        cancelFocusChange()
                    }
                }
                .focusGroup()
    ) {
        Button(onClick = {}) { Text("First Button") }
    }
}
// [END xr_glimmer_focus_group]
