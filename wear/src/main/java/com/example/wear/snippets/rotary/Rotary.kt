/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.wear.snippets.rotary

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales

@Composable
fun ScrollableScreen() {
    // This sample doesn't add a Time Text at the top of the screen.
    // If using Time Text, add padding to ensure content does not overlap with Time Text.
    // [START android_wear_rotary_input_snap_fling]
    val listState = rememberScalingLazyListState()
    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {

        val state = rememberScalingLazyListState()
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            flingBehavior = ScalingLazyColumnDefaults.snapFlingBehavior(state = state)
        ) {
            // Content goes here
            // [START_EXCLUDE]
            item { ListHeader { Text(text = "List Header") } }
            items(20) {
                Chip(
                    onClick = {},
                    label = { Text("List item $it") },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_rotary_input_snap_fling]
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun ScrollableScreenPreview() {
    ScrollableScreen()
}
