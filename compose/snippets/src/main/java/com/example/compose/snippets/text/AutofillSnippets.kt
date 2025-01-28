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

package com.example.compose.snippets.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.touchinput.Button

@Composable
fun AddAutofill() {
    // [START android_compose_autofill_1]
    BasicTextField(
        state = remember { TextFieldState("Enter your username.") },
        modifier = Modifier.semantics { contentType = ContentType.Username }
    )
    // [END android_compose_autofill_1]
}

@Composable
fun AddMultipleTypesOfAutofill() {
    // [START android_compose_autofill_2]
    Column {
        BasicTextField(
            state = remember { TextFieldState() },
            modifier =
            Modifier.semantics {
                contentType = ContentType.Username + ContentType.EmailAddress
            },
        )
    }
    // [END android_compose_autofill_2]
}

@Composable
fun SaveDataWithAutofill() {
    // [START android_compose_autofill_3]
    // [START android_compose_autofill_4]
    val autofillManager = LocalAutofillManager.current
    // [END android_compose_autofill_3]

    Column {
        BasicTextField(
            state = remember { TextFieldState() },
            modifier =
            Modifier.semantics {
                contentType = ContentType.NewUsername
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            state = remember { TextFieldState() },
            modifier =
            Modifier.semantics {
                contentType = ContentType.NewPassword
            },
        )
    }
    // [END android_compose_autofill_4]
}

@Composable
fun SaveDataWithAutofillOnClick() {
    // [START android_compose_autofill_5]
    val autofillManager = LocalAutofillManager.current
    Column {
        BasicTextField(
            state = remember { TextFieldState() },
            modifier =
            Modifier.semantics {
                contentType = ContentType.NewUsername
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            state = remember { TextFieldState() },
            modifier =
            Modifier.semantics {
                contentType = ContentType.NewPassword
            },
        )

        // Submit button
        Button(onClick = { autofillManager?.commit() }) { Text("Reset credentials") }
    }
    // [END android_compose_autofill_5]
}

// [START android_compose_autofill_6]
@Composable
fun customizeAutofillHighlight() {
    val customHighlightColor = Color.Red
    val usernameState = remember { TextFieldState() }

    CompositionLocalProvider(LocalAutofillHighlightColor provides customHighlightColor) {
        Column {
            BasicTextField(
                state = usernameState,
                modifier = Modifier.semantics {
                    contentType = ContentType.Username
                }
            )
        }
    }
}
// [END android_compose_autofill_6]
