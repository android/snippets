/*
 * Copyright 2023 The Android Open Source Project
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

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonExamples() {
    Column(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Filled button:")
        FilledButtonExample(onClick = { Log.d("Filled button", "Filled button clicked.") })
        Text("Filled tonal button:")
        FilledTonalButtonExample(onClick = { Log.d("Filled tonal button", "Filled tonal button clicked.") })
        Text("Elevated button:")
        ElevatedButtonExample(onClick = { Log.d("Elevated button", "Elevated button clicked.") })
        Text("Outlined button:")
        OutlinedButtonExample(onClick = { Log.d("Outlined button", "Outlined button clicked.") })
        Text("Text button")
        TextButtonExample(onClick = { Log.d("Text button", "Text button clicked.") })
    }
}

// [START android_compose_components_filledbutton]
@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Filled")
    }
}
// [END android_compose_components_filledbutton]

// [START android_compose_components_filledtonalbutton]
@Composable
fun FilledTonalButtonExample(onClick: () -> Unit) {
    FilledTonalButton(onClick = { onClick() }) {
        Text("Tonal")
    }
}
// [END android_compose_components_filledtonalbutton]

// [START android_compose_components_elevatedbutton]
@Composable
fun ElevatedButtonExample(onClick: () -> Unit) {
    ElevatedButton(onClick = { onClick() }) {
        Text("Elevated")
    }
}
// [END android_compose_components_elevatedbutton]

// [START android_compose_components_outlinedbutton]
@Composable
fun OutlinedButtonExample(onClick: () -> Unit) {
    OutlinedButton(onClick = { onClick() }) {
        Text("Outlined")
    }
}
// [END android_compose_components_outlinedbutton]

// [START android_compose_components_textbutton]
@Composable
fun TextButtonExample(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() }
    ) {
        Text("Text Button")
    }
}
// [END android_compose_components_textbutton]
