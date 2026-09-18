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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester

@Composable
private fun FocusTraversalOneDimensionalSample() {
    // [START android_compose_touchinput_focus_one_dimensional_traversal]
    Column {
        Row {
            Button(onClick = { /* ... */ }) { Text("1st") }
            Button(onClick = { /* ... */ }) { Text("2nd") }
        }
        Row {
            Button(onClick = { /* ... */ }) { Text("3rd") }
            Button(onClick = { /* ... */ }) { Text("4th") }
        }
    }
    // [END android_compose_touchinput_focus_one_dimensional_traversal]
}

@Composable
private fun FocusTraversalCustomizeOneDimensionalSample() {
    // [START android_compose_touchinput_focus_customize_one_dimensional_traversal]
    val (first, second, third) = remember { FocusRequester.createRefs() }

    Column {
        Button(
            onClick = { /* ... */ },
            modifier = Modifier
                .focusRequester(first)
                .focusProperties { next = third }
        ) {
            Text("First (Tab jumps to Third)")
        }
        Button(
            onClick = { /* ... */ },
            modifier = Modifier.focusRequester(second)
        ) {
            Text("Second")
        }
        Button(
            onClick = { /* ... */ },
            modifier = Modifier.focusRequester(third)
        ) {
            Text("Third")
        }
    }
    // [END android_compose_touchinput_focus_customize_one_dimensional_traversal]
}

@Composable
private fun FocusTraversalCustomizeTwoDimensionalSample() {
    // [START android_compose_touchinput_focus_customize_two_dimensional_traversal]
    val (topButton, bottomButton) = remember { FocusRequester.createRefs() }

    Button(
        onClick = { /* ... */ },
        modifier = Modifier
            .focusRequester(topButton)
            .focusProperties {
                down = bottomButton
                right = bottomButton
            }
    ) {
        Text("Top button")
    }
    Button(
        onClick = { /* ... */ },
        modifier = Modifier.focusRequester(bottomButton)
    ) {
        Text("Bottom button")
    }
    // [END android_compose_touchinput_focus_customize_two_dimensional_traversal]
}
