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

package com.example.compose.snippets.components

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
// [START android_compose_expressive_text_textfield]
@Composable
fun TextFieldSample() {
    TextField(
        state = rememberTextFieldState(),
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text("Label") },
        shape = TextFieldDefaults.roundedShape,
        colors = TextFieldDefaults.tonalColors(),
    )
}
// [END android_compose_expressive_text_textfield]

@Preview
// [START android_compose_expressive_text_outlinedtextfield]
@Composable
fun OutlinedTextFieldSample() {
    OutlinedTextField(
        state = rememberTextFieldState(),
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text("Label") },
        shape = OutlinedTextFieldDefaults.roundedShape,
        colors = OutlinedTextFieldDefaults.tonalColors(),
        labelPosition = TextFieldLabelPosition.Inside(),
    )
}
// [END android_compose_expressive_text_outlinedtextfield]
