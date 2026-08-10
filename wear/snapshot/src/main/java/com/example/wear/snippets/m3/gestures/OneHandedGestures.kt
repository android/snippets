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
 * NOTE: One-Handed Gesture APIs require Wear Compose Material 3 1.7.0-SNAPSHOT (or a future 1.7.0-alpha08+ release).
 * Published alpha releases up to 1.7.0-alpha07 used older API names (GestureAction/GesturePriority) and will not compile with these snippets.
 *
 * To compile and test this snippet locally, pass `-PwearGesturesVersionOverride=1.7.0-SNAPSHOT` to Gradle:
 *     ./gradlew :wear:assembleDebug -PwearGesturesVersionOverride=1.7.0-SNAPSHOT
 */

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
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

@Composable
fun OneHandedGestureScreen(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val onClick = remember { { isPlaying = !isPlaying } }

    val scrollState = rememberTransformingLazyColumnState()

    val scrollGestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary,
        priority = OneHandedGesturePriority.Scrollable
    )
    val scrollIndicatorState = remember(scrollGestureConfig) { OneHandedGestureScrollIndicatorState() }

    val buttonGestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary,
        priority = OneHandedGesturePriority.Clickable
    )
    val buttonIndicatorState = remember { OneHandedGestureClickIndicatorState() }
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    ScreenScaffold(
        scrollState = scrollState,
        edgeButton = {
            EdgeButton(
                onClick = onClick,
                interactionSource = buttonInteractionSource,
                modifier =
                    if (scrollState.canScrollForward) {
                        Modifier
                    } else {
                        Modifier.oneHandedGesture(
                            gestureConfiguration = buttonGestureConfig,
                            interactionSource = buttonInteractionSource,
                            onGestureLabel = if (isPlaying) "pause" else "play",
                            onGestureAvailable = {
                                coroutineScope.launch { buttonIndicatorState.showIndicator() }
                            },
                            onGesture = onClick
                        )
                    } then
                        Modifier.scrollable(
                            state = scrollState,
                            orientation = Orientation.Vertical,
                            reverseDirection = true,
                            overscrollEffect = rememberOverscrollEffect()
                        )
            ) {
                OneHandedGestureClickIndicator(
                    gestureConfiguration = buttonGestureConfig,
                    state = buttonIndicatorState
                ) {
                    Text(if (isPlaying) "Pause" else "Play")
                }
            }
        },
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
                    onGestureLabel = "scroll",
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

            // Scrollable Items to demonstrate gesture scrolling
            items(10) { index ->
                Text("Item $index", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun OneHandedGestureScreenPreview() {
    OneHandedGestureScreen()
}

@Composable
private fun ButtonGestureSnippet() {
    // [START android_wear_one_handed_gesture_button]
    var isPlaying by remember { mutableStateOf(false) }
    val onClick = { isPlaying = !isPlaying }

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
                onGestureLabel = if (isPlaying) "pause" else "play",
                onGesture = onClick
            )
    ) {
        Text(if (isPlaying) "Pause" else "Play")
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

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .oneHandedGesture(
                gestureConfiguration = gestureConfig,
                interactionSource = interactionSource,
                onGestureLabel = if (isPlaying) "pause" else "play",
                onGestureAvailable = { coroutineScope.launch { indicatorState.showIndicator() } },
                onGesture = onClick
            )
    ) {
        OneHandedGestureClickIndicator(
            gestureConfiguration = gestureConfig,
            state = indicatorState
        ) {
            Text(if (isPlaying) "Pause" else "Play", modifier = Modifier.fillMaxWidth())
        }
    }
    // [END android_wear_one_handed_gesture_button_hint]
}

@Composable
fun ScrollGestureHintSnippet() {
    // [START android_wear_one_handed_gesture_scroll_hint]
    val scrollState = rememberTransformingLazyColumnState()
    val gestureConfig = rememberOneHandedGestureConfiguration(
        action = OneHandedGestureAction.Primary,
        priority = OneHandedGesturePriority.Scrollable
    )
    val indicatorState = remember(gestureConfig) { OneHandedGestureScrollIndicatorState() }
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
                    onGestureLabel = "scroll",
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

