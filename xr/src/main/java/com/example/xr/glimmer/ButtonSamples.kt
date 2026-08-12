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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.ButtonGroup
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.rememberButtonGroupState
import kotlinx.coroutines.launch

private val FavoriteIcon = Icons.Default.Favorite
private val SendIcon = Icons.AutoMirrored.Filled.Send

@Composable
fun ButtonSample() {
    // [START androidxr_glimmer_button]
    Button(
        onClick = { /* Handle navigation or action */ },
        leadingIcon = { Icon(FavoriteIcon, contentDescription = null) },
        trailingIcon = { Icon(SendIcon, contentDescription = null) }
    ) {
        Text("Text Label", style = GlimmerTheme.typography.titleSmall)
    }
    // [END androidxr_glimmer_button]
}

@Composable
fun ButtonGroupSample() {
    // [START androidxr_glimmer_button_group]
    ButtonGroup(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {}) { Text("Button 1") }
        Button(onClick = {}) { Text("Button 2") }
        Button(onClick = {}) { Text("Button 3") }
        Button(onClick = {}) { Text("Button 4") }
        Button(onClick = {}) { Text("Button 5") }
    }
    // [END androidxr_glimmer_button_group]
}

@Composable
fun ButtonGroupControlCurrentItemSample() {
    // [START androidxr_glimmer_button_group_control]
    val scope = rememberCoroutineScope()
    val state = rememberButtonGroupState()
    ButtonGroup(modifier = Modifier.fillMaxWidth(), state = state) {
        Button(onClick = { scope.launch { state.animateScrollToItem(1) } }) {
            Text("Select last item")
        }
        Button(onClick = { scope.launch { state.animateScrollToItem(0) } }) {
            Text("Select first item")
        }
    }
    // [END androidxr_glimmer_button_group_control]
}