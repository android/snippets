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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun FocusedStateExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("Focus state", modifier = modifier) {
        ApplyRipple(
            modifier = Modifier.focusGroup(),
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyRipple(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        // [START android_compose_touchinput_focus_ripple]
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier.clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = ripple()
            )
        ) {
            Text("With focused state", modifier = Modifier.padding(32.dp))
        }
        // [END android_compose_touchinput_focus_ripple]
        CompositionLocalProvider(
            LocalRippleConfiguration provides null
        ) {
            Box(
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Text("Without focused state", modifier = Modifier.padding(32.dp))
            }
        }
    }
}
