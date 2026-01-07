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
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.foundation.text.input.rememberTextFieldState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.touchinput.Button

@Composable
fun AddAutofill() {
    val textFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }

    // [START android_compose_autofill_1_value]
    TextField(
        value = textFieldValue.value,
        onValueChange = {textFieldValue.value = it},
        modifier = Modifier.semantics { contentType = ContentType.Username }
    )
    // [END android_compose_autofill_1_value]

    // [START android_compose_autofill_1]
    TextField(
        state = rememberTextFieldState(),
        modifier = Modifier.semantics { contentType = ContentType.Username }
    )
    // [END android_compose_autofill_1]
}

@Composable
fun AddMultipleTypesOfAutofill() {
    val textFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }
    // [START android_compose_autofill_2_value]
    TextField(
        value = textFieldValue.value,
        onValueChange = { textFieldValue.value = it },
        modifier = Modifier.semantics {
            contentType = ContentType.Username + ContentType.EmailAddress
        }
    )
    // [END android_compose_autofill_2_value]

    // [START android_compose_autofill_2]
    TextField(
        state = rememberTextFieldState(),
        modifier = Modifier.semantics {
            contentType = ContentType.Username + ContentType.EmailAddress
        }
    )
    // [END android_compose_autofill_2]
}

@Composable
fun AutofillManager() {
    // [START android_compose_autofill_3]
    val autofillManager = LocalAutofillManager.current
    // [END android_compose_autofill_3]
}

@Composable
fun SaveDateWithAutofillValue() {
    val textFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }
    // [START android_compose_autofill_4_value]
    val autofillManager = LocalAutofillManager.current

    Column {
        TextField(
            value = textFieldValue.value,
            onValueChange = { textFieldValue.value = it },
            modifier = Modifier.semantics { contentType = ContentType.NewUsername }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textFieldValue.value,
            onValueChange = { textFieldValue.value = it },
            modifier = Modifier.semantics { contentType = ContentType.NewPassword }
        )
    }
    // [END android_compose_autofill_4_value]
}

@Composable
fun SaveDataWithAutofillState() {

    // [START android_compose_autofill_4]
    val autofillManager = LocalAutofillManager.current

    Column {
        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier.semantics { contentType = ContentType.NewUsername }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier.semantics { contentType = ContentType.NewPassword }
        )
    }
    // [END android_compose_autofill_4]
}


@Composable
fun SaveDataWithAutofillOnClickValue() {
    val usernameTextFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val passwordTextFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }
    // [START android_compose_autofill_5_value]
    val autofillManager = LocalAutofillManager.current

    Column {
        TextField(
            value = usernameTextFieldValue.value,
            onValueChange = { usernameTextFieldValue.value = it },
            modifier = Modifier.semantics { contentType = ContentType.NewUsername },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordTextFieldValue.value,
            onValueChange = { passwordTextFieldValue.value = it },
            modifier = Modifier.semantics { contentType = ContentType.NewPassword },
        )

        // Submit button
        Button(onClick = { autofillManager?.commit() }) { Text("Reset credentials") }
    }
    // [END android_compose_autofill_5_value]
}

@Composable
fun SaveDataWithAutofillOnClickState() {
    // [START android_compose_autofill_5]
    val autofillManager = LocalAutofillManager.current

    Column {
        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier.semantics { contentType = ContentType.NewUsername },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier.semantics { contentType = ContentType.NewPassword },
        )

        // Submit button
        Button(onClick = { autofillManager?.commit() }) { Text("Reset credentials") }
    }
    // [END android_compose_autofill_5]
}

@Composable
fun CustomAutofillHighlightValue() {
    val textFieldValue = remember {
        mutableStateOf(TextFieldValue(""))
    }

    // [START android_compose_autofill_6_value]
    val customHighlightColor = Color.Red

    CompositionLocalProvider(LocalAutofillHighlightColor provides customHighlightColor) {
        TextField(
            value = textFieldValue.value,
            onValueChange = { textFieldValue.value = it },
            modifier = Modifier.semantics { contentType = ContentType.Username }
        )
    }
    // [END android_compose_autofill_6_value]
}

@Composable
fun CustomAutofillHighlightState(customHighlightColor: Color = Color.Red) {
    // [START android_compose_autofill_6]
    val customHighlightColor = Color.Red

    CompositionLocalProvider(LocalAutofillHighlightColor provides customHighlightColor) {
        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier.semantics { contentType = ContentType.Username }
        )
    }
    // [END android_compose_autofill_6]
}
