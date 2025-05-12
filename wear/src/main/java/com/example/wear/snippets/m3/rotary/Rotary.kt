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

package com.example.wear.snippets.m3.rotary

import android.view.MotionEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Picker
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.rememberPickerState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch

@Composable
fun TimePicker() {
    val textStyle = MaterialTheme.typography.displayMedium

    // [START android_wear_rotary_input_picker]
    var selectedColumn by remember { mutableIntStateOf(0) }

    val hoursFocusRequester = remember { FocusRequester() }
    val minutesRequester = remember { FocusRequester() }
    // [START_EXCLUDE]
    val coroutineScope = rememberCoroutineScope()

    @Composable
    fun Option(column: Int, text: String) = Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text, style = textStyle,
            color = if (selectedColumn == column) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .pointerInteropFilter {
                    if (it.action == MotionEvent.ACTION_DOWN) selectedColumn = column
                    true
                }
        )
    }
    // [END_EXCLUDE]
    ScreenScaffold(modifier = Modifier.fillMaxSize()) {
        Row(
            // [START_EXCLUDE]
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            // [END_EXCLUDE]
            // ...
        ) {
            // [START_EXCLUDE]
            val hourState = rememberPickerState(
                initialNumberOfOptions = 12,
                initiallySelectedIndex = 5
            )
            val hourContentDescription by remember {
                derivedStateOf { "${hourState.selectedOptionIndex + 1 } hours" }
            }
            // [END_EXCLUDE]
            Picker(
                readOnly = selectedColumn != 0,
                modifier = Modifier.size(64.dp, 100.dp)
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            hourState.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(hoursFocusRequester)
                    .focusable(),
                onSelected = { selectedColumn = 0 },
                // ...
                // [START_EXCLUDE]
                state = hourState,
                contentDescription = { hourContentDescription },
                option = { hour: Int -> Option(0, "%2d".format(hour + 1)) }
                // [END_EXCLUDE]
            )
            // [START_EXCLUDE]
            Spacer(Modifier.width(8.dp))
            Text(text = ":", style = textStyle, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.width(8.dp))
            val minuteState =
                rememberPickerState(initialNumberOfOptions = 60, initiallySelectedIndex = 0)
            val minuteContentDescription by remember {
                derivedStateOf { "${minuteState.selectedOptionIndex} minutes" }
            }
            // [END_EXCLUDE]
            Picker(
                readOnly = selectedColumn != 1,
                modifier = Modifier.size(64.dp, 100.dp)
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            minuteState.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(minutesRequester)
                    .focusable(),
                onSelected = { selectedColumn = 1 },
                // ...
                // [START_EXCLUDE]
                state = minuteState,
                contentDescription = { minuteContentDescription },
                option = { minute: Int -> Option(1, "%02d".format(minute)) }
                // [END_EXCLUDE]
            )
            LaunchedEffect(selectedColumn) {
                listOf(
                    hoursFocusRequester,
                    minutesRequester
                )[selectedColumn]
                    .requestFocus()
            }
        }
    }
    // [END android_wear_rotary_input_picker]
}

@Composable
fun SnapScrollableScreen() {
    // This sample doesn't add a Time Text at the top of the screen.
    // If using Time Text, add padding to ensure content does not overlap with Time Text.
    // [START android_wear_rotary_input_snap_fling]
    val listState = rememberScalingLazyListState()
    ScreenScaffold(
        scrollIndicator = {
            ScrollIndicator(state = listState)
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
                Button(
                    onClick = {},
                    label = { Text("List item $it") },
                    colors = ButtonDefaults.filledTonalButtonColors()
                )
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_rotary_input_snap_fling]
}

@Composable
fun PositionScrollIndicator() {
    // [START android_wear_rotary_position_indicator]
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollIndicator = {
            ScrollIndicator(state = listState)
        }
    ) {
        // ...
    }
    // [END android_wear_rotary_position_indicator]
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun TimePickerPreview() {
    TimePicker()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SnapScrollableScreenPreview() {
    SnapScrollableScreen()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PositionScrollIndicatorPreview() {
    PositionScrollIndicator()
}
