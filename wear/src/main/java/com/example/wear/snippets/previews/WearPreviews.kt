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

package com.example.wear.snippets.previews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalScrollCaptureInProgress
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.tooling.preview.devices.WearDevices

// [START android_wear_preview_screens]
@WearPreviewDevices
@Composable
fun WorkoutScreenPreview() {
    MaterialTheme {
        // AppScaffold provides the top-level TimeText overlay
        AppScaffold {
            // WorkoutScreen contains its own ScreenScaffold and content
            WorkoutScreen(
                heartRate = 142,
                elapsedTime = "12:45"
            )
        }
    }
}
// [END android_wear_preview_screens]

// [START android_wear_preview_components]
@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun HeartRateCardPreview() {
    MaterialTheme {
        HeartRateCard(bpm = 142, zone = "Aerobic")
    }
}
// [END android_wear_preview_components]

// [START android_wear_preview_multipreview]
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MessageDetailScreenPreview() {
    MaterialTheme {
        AppScaffold {
            MessageDetailScreen(
                sender = "Alex",
                body = "Running 5 mins late!"
            )
        }
    }
}
// [END android_wear_preview_multipreview]

// [START android_wear_preview_custom_spec]
@Preview(
    name = "XL Round Watch (240dp)",
    device = "spec:width=240dp,height=240dp,dpi=320,isRound=true",
    showSystemUi = true,
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun WorkoutScreenXlPreview() {
    MaterialTheme {
        AppScaffold {
            WorkoutScreen(heartRate = 142, elapsedTime = "12:45")
        }
    }
}
// [END android_wear_preview_custom_spec]

// [START android_wear_preview_extremes]
@Preview(
    name = "1. Standard Large Round",
    group = "Layout extremes",
    device = WearDevices.LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xFF000000,
    showBackground = true
)
@Preview(
    name = "2. Extreme Small Round (Largest Font + German)",
    group = "Layout extremes",
    device = WearDevices.SMALL_ROUND,
    fontScale = 1.24f,
    locale = "de-rDE",
    showSystemUi = true,
    backgroundColor = 0xFF000000,
    showBackground = true
)
annotation class WearPreviewExtremes
// [END android_wear_preview_extremes]

@WearPreviewExtremes
@Composable
fun MessageDetailScreenExtremesPreview() {
    MaterialTheme {
        AppScaffold {
            MessageDetailScreen(
                sender = "Alex",
                body = "Running 5 mins late!"
            )
        }
    }
}

// [START android_wear_preview_hoist_state]
@Composable
fun InboxScreen(
    messages: List<Message>,
    columnState: TransformingLazyColumnState = rememberTransformingLazyColumnState(),
) {
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = columnState,
        edgeButton = {
            EdgeButton(onClick = { /* Compose new */ }) {
                Text("New message")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
        ) {
            items(messages.size) { index ->
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            CardDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(messages[index].subject)
                }
            }
        }
    }
}
// [END android_wear_preview_hoist_state]

// [START android_wear_preview_anchor_index]
@WearPreviewLargeRound
@Composable
fun InboxScreenTopPreview() {
    MaterialTheme {
        AppScaffold {
            // Default (-1): Pinned to top of list (index 0)
            InboxScreen(messages = sampleMessages)
        }
    }
}

@WearPreviewLargeRound
@Composable
fun InboxScreenScrolledMiddlePreview() {
    MaterialTheme {
        AppScaffold {
            // Centers item index 3 in the viewport, showing top/bottom item morphing
            InboxScreen(
                messages = sampleMessages,
                columnState = rememberTransformingLazyColumnState(
                    initialAnchorItemIndex = 3
                )
            )
        }
    }
}

@WearPreviewLargeRound
@Composable
fun InboxScreenBottomEdgeButtonPreview() {
    MaterialTheme {
        AppScaffold {
            // Anchors on the last item so the EdgeButton is visible at the bottom
            InboxScreen(
                messages = sampleMessages,
                columnState = rememberTransformingLazyColumnState(
                    initialAnchorItemIndex = sampleMessages.lastIndex
                )
            )
        }
    }
}
// [END android_wear_preview_anchor_index]

@Composable
fun InboxScreenWithScrollCaptureGuard(
    messages: List<Message>,
    columnState: TransformingLazyColumnState = rememberTransformingLazyColumnState(),
) {
    // [START android_wear_preview_scroll_capture]
    ScreenScaffold(
        scrollState = columnState,
        scrollIndicator = {
            if (!LocalScrollCaptureInProgress.current) {
                ScrollIndicator(state = columnState)
            }
        }
    ) { contentPadding ->
        // TransformingLazyColumn content...
        // [START_EXCLUDE]
        val transformationSpec = rememberTransformationSpec()
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
        ) {
            items(messages.size) { index ->
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            CardDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(messages[index].subject)
                }
            }
        }
        // [END_EXCLUDE]
    }
    // [END android_wear_preview_scroll_capture]
}

// Helper composables and sample data for previews
data class Message(val sender: String, val subject: String)

val sampleMessages = listOf(
    Message("Alex", "Running 5 mins late!"),
    Message("Sam", "Coffee after standup?"),
    Message("Jordan", "Updated the design doc"),
    Message("Taylor", "Lunch at 12:30?"),
    Message("Morgan", "Wear OS build passed"),
    Message("Casey", "Reviewing PR #1099"),
    Message("Jamie", "Sprint planning tomorrow"),
    Message("Robin", "See you at the gym!")
)

@Composable
fun WorkoutScreen(
    heartRate: Int,
    elapsedTime: String,
    columnState: TransformingLazyColumnState = rememberTransformingLazyColumnState()
) {
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = columnState) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text("Running • $elapsedTime")
                }
            }
            item {
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            CardDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text("$heartRate bpm • Aerobic")
                }
            }
        }
    }
}

@Composable
fun HeartRateCard(bpm: Int, zone: String) {
    Card(
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$bpm bpm ($zone)")
    }
}

@Composable
fun MessageDetailScreen(
    sender: String,
    body: String,
    columnState: TransformingLazyColumnState = rememberTransformingLazyColumnState()
) {
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = columnState) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(sender)
                }
            }
            item {
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            CardDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(body)
                }
            }
        }
    }
}
