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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
private fun FocusInTextFieldsLineLimitsSample() {
    val singleLineState = rememberTextFieldState()
    val multiLineState = rememberTextFieldState()

    // [START android_compose_touchinput_focus_text_fields_line_limits]
    // Single-line text field: Tab advances focus to the next focus target
    BasicTextField(
        state = singleLineState,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth()
    )

    // Multi-line text field: Tab inserts '\t'; Shift + Tab moves to previous target
    BasicTextField(
        state = multiLineState,
        lineLimits = TextFieldLineLimits.MultiLine(),
        modifier = Modifier.fillMaxWidth()
    )
    // [END android_compose_touchinput_focus_text_fields_line_limits]
}
