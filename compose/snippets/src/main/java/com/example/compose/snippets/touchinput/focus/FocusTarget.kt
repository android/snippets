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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
private fun FocusTargetInteractiveDefaultSample() {
    // [START android_compose_touchinput_focus_target]
    Card(onClick = { /* First card action */ }) {
        Text("Card 1 (Interactive)")
    }
    Card {
        Text("Card 2 (Informational - not focusable)")
    }
    Card(onClick = { /* Third card action */ }) {
        Text("Card 3 (Interactive)")
    }
    // [END android_compose_touchinput_focus_target]
}

@Composable
private fun FocusTargetMakeInteractiveSample() {
    // [START android_compose_touchinput_focus_target_interactive_ui_element]
    Card(onClick = { /* Second card action */ }) {
        Text("Card 2 (Now interactive and focusable)")
    }
    // [END android_compose_touchinput_focus_target_interactive_ui_element]
}

@Composable
private fun FocusTargetClickableModifierSample() {
    // [START android_compose_touchinput_focus_target_clickable_modifier]
    Box(
        modifier = Modifier
            .clickable { /* Handle click */ }
    ) {
        Text("Custom interactive element")
    }
    // [END android_compose_touchinput_focus_target_clickable_modifier]
}

@Composable
private fun FocusTargetFocusableModifierSample() {
    // [START android_compose_touchinput_focus_focusable]
    Box(
        modifier = Modifier
            .focusable()
    ) {
        Text("Non-clickable focus target")
    }
    // [END android_compose_touchinput_focus_focusable]
}
