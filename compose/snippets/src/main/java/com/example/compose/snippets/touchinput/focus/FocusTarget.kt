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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp

@Composable
internal fun FocusTargetExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("Focus target", modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            FocusTarget(
                onClick = onClick,
                modifier = Modifier.focusGroup()
            )
            InteractiveUiElementIsFocusTargets(
                onClick = onClick,
                modifier = Modifier.focusGroup()
            )
            FocusTargetWithClickableModifier(
                onClick = onClick,
                modifier = Modifier.focusGroup()
            )
        }
        SubSection("Modifier precedence") {
            ModifierPrecedence(
                onClick = onClick,
                modifier = Modifier.focusGroup()
            )
        }
    }
}

@Composable
fun FocusTarget(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_target]
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Card(onClick = onClick) {
            Text(text = "1st card", modifier = Modifier.padding(32.dp))
        }
        Card {
            Text(text = "2nd card", modifier = Modifier.padding(32.dp))
        }
        Card(onClick = onClick) {
            Text(text = "3rd card", modifier = Modifier.padding(32.dp))
        }
    }
    // [END android_compose_touchinput_focus_target]
}

@Composable
fun InteractiveUiElementIsFocusTargets(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_target_interactive_ui_element]
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Card(onClick = onClick) {
            Text(text = "1st card", modifier = Modifier.padding(32.dp))
        }
        Card(onClick = onClick) {
            Text(text = "2nd card", modifier = Modifier.padding(32.dp))
        }
        Card(onClick = onClick) {
            Text(text = "3rd card", modifier = Modifier.padding(32.dp))
        }
    }
    // [END android_compose_touchinput_focus_target_interactive_ui_element]
}

@Composable
fun FocusTargetWithClickableModifier(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Card(onClick = onClick) {
            Text(text = "1st card", modifier = Modifier.padding(32.dp))
        }
        // [START android_compose_touchinput_focus_target_interactive_ui_element]
        Box(modifier = Modifier.clickable(onClick = onClick)) {
            Text(text = "1st box", modifier = Modifier.padding(32.dp))
        }
        // [END android_compose_touchinput_focus_target_interactive_ui_element]
        Card(onClick = onClick) {
            Text(text = "3rd card", modifier = Modifier.padding(32.dp))
        }
    }
}

@Composable
fun ModifierPrecedence(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        // [START android_compose_touchinput_focus_modifier_precedence]
        Card(
            onClick = onClick,
            modifier = Modifier
                .focusProperties {
                    canFocus = false
                }
                .focusProperties {
                    canFocus = true
                }
        ) {
            Text(text = "1st card", modifier = Modifier.padding(32.dp))
        }
        // [END android_compose_touchinput_focus_modifier_precedence]
    }
}
