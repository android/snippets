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

package com.example.wear.snippets.m3.gestures

/*
 * NOTE: One-Handed Gesture APIs (androidx.wear.compose.material3.onehandedgesture) require
 * Wear Compose Material 3 version 1.7.0-alpha01 or newer (or 1.7.0-SNAPSHOT).
 *
 * To compile and test this snippet locally, pass `-PwearComposeOverride=1.7.0-SNAPSHOT` to Gradle:
 *     ./gradlew :wear:assembleDebug -PwearComposeOverride=1.7.0-SNAPSHOT
 */

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.OutlinedIconButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureAction
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureClickIndicator
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureClickIndicatorState
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureDefaults
import androidx.wear.compose.material3.onehandedgesture.OneHandedGesturePriority
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureScrollIndicator
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureScrollIndicatorState
import androidx.wear.compose.material3.onehandedgesture.oneHandedGesture
import androidx.wear.compose.material3.onehandedgesture.rememberOneHandedGestureConfiguration
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch

// [START android_wear_one_handed_gesture_screen]
@Composable
fun OneHandedGestureScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val onClick = remember { { isPlaying = !isPlaying } }

    val scrollState = rememberTransformingLazyColumnState()

    val scrollGestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary,
        priority = OneHandedGesturePriority.Scrollable
    )
    val scrollIndicatorState = remember { OneHandedGestureScrollIndicatorState() }

    val buttonGestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary,
        priority = OneHandedGesturePriority.Clickable
    )
    val buttonIndicatorState = remember { OneHandedGestureClickIndicatorState() }
    val coroutineScope = rememberCoroutineScope()

    ScreenScaffold(
        scrollState = scrollState,
        scrollIndicator = {
            OneHandedGestureScrollIndicator(
                gestureConfiguration = scrollGestureConfig,
                indicatorState = scrollIndicatorState,
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            modifier = modifier
                .fillMaxSize()
                .oneHandedGesture(
                    gestureConfiguration = scrollGestureConfig,
                    onGestureLabel = "Scroll to next item",
                    onGestureAvailable = {
                        coroutineScope.launch { scrollIndicatorState.showIndicator() }
                    },
                    onGesture = { OneHandedGestureDefaults.scrollDownToNextItem(scrollState) }
                )
        ) {
            item {
                ListHeader {
                    Text("Gestures Demo")
                }
            }

            // Interactive Play/Pause Button
            item {
                val buttonInteractionSource = remember { MutableInteractionSource() }

                Button(
                    onClick = onClick,
                    interactionSource = buttonInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .oneHandedGesture(
                            gestureConfiguration = buttonGestureConfig,
                            interactionSource = buttonInteractionSource,
                            onGestureLabel = if (isPlaying) "Pause" else "Play",
                            onGestureAvailable = {
                                coroutineScope.launch { buttonIndicatorState.showIndicator() }
                            },
                            onGesture = onClick
                        )
                ) {
                    OneHandedGestureClickIndicator(
                        gestureConfiguration = buttonGestureConfig,
                        state = buttonIndicatorState
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                            Icon(icon, contentDescription = "Play/Pause")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isPlaying) "Pause" else "Play")
                        }
                    }
                }
            }

            // Scrollable Items to demonstrate gesture scrolling
            items(10) { index ->
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 10.dp)
                ) {
                    Text("Scrollable Item ${index + 1}", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
// [END android_wear_one_handed_gesture_screen]

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun OneHandedGestureScreenPreview() {
    OneHandedGestureScreen()
}

@Composable
private fun ScrollGestureSnippet() {
    // [START android_wear_one_handed_gesture_scroll]
    val scrollState = rememberTransformingLazyColumnState()
    val gestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary
    )

    TransformingLazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .oneHandedGesture(
                gestureConfiguration = gestureConfig,
                onGestureLabel = "Scroll down",
                onGesture = { OneHandedGestureDefaults.scrollDownToNextItem(scrollState) }
            )
    ) {
        items(10) { index ->
            Text("Item $index", modifier = Modifier.padding(8.dp))
        }
    }
    // [END android_wear_one_handed_gesture_scroll]
}

@Suppress("UnusedVariable", "UNUSED_PARAMETER")
@Composable
private fun ButtonGestureSnippet(onClick: () -> Unit) {
    // [START android_wear_one_handed_gesture_button]
    val gestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary
    )
    // Share MutableInteractionSource so gesture events emit visual press state on the button
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .oneHandedGesture(
                gestureConfiguration = gestureConfig,
                interactionSource = interactionSource,
                onGestureLabel = "Click button",
                onGesture = onClick
            )
    ) {
        Text("Click button")
    }
    // [END android_wear_one_handed_gesture_button]
}

@Composable
fun ButtonGestureHintSnippet() {
    // [START android_wear_one_handed_gesture_button_hint]
    var isPlaying by remember { mutableStateOf(false) }
    val onClick = { isPlaying = !isPlaying }

    val gestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary
    )
    val indicatorState = remember { OneHandedGestureClickIndicatorState() }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedIconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .size(IconButtonDefaults.LargeButtonSize)
            .oneHandedGesture(
                gestureConfiguration = gestureConfig,
                interactionSource = interactionSource,
                onGestureLabel = if (isPlaying) "Pause" else "Play",
                onGestureAvailable = { coroutineScope.launch { indicatorState.showIndicator() } },
                onGesture = onClick
            )
    ) {
        OneHandedGestureClickIndicator(
            gestureConfiguration = gestureConfig,
            state = indicatorState
        ) {
            val icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
            val description = if (isPlaying) "Pause" else "Play"
            Icon(icon, contentDescription = description)
        }
    }
    // [END android_wear_one_handed_gesture_button_hint]
}

@Composable
fun ScrollGestureHintSnippet() {
    // [START android_wear_one_handed_gesture_scroll_hint]
    val scrollState = rememberTransformingLazyColumnState()
    val gestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary
    )
    val indicatorState = remember { OneHandedGestureScrollIndicatorState() }
    val coroutineScope = rememberCoroutineScope()

    ScreenScaffold(
        scrollState = scrollState,
        scrollIndicator = {
            OneHandedGestureScrollIndicator(
                gestureConfiguration = gestureConfig,
                indicatorState = indicatorState,
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .oneHandedGesture(
                    gestureConfiguration = gestureConfig,
                    onGestureLabel = "Scroll down",
                    onGestureAvailable = {
                        coroutineScope.launch { indicatorState.showIndicator() }
                    },
                    onGesture = { OneHandedGestureDefaults.scrollDownToNextItem(scrollState) }
                )
        ) {
            items(10) { index ->
                Text("Item $index", modifier = Modifier.padding(8.dp))
            }
        }
    }
    // [END android_wear_one_handed_gesture_scroll_hint]
}

