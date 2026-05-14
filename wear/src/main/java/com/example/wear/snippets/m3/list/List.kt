/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.wear.snippets.m3.list

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.material3.EdgeButton
import com.example.wear.snippets.list.ComposeList
import com.example.wear.snippets.list.LARGE_DISPLAY_BREAKPOINT
import com.example.wear.snippets.list.SnapAndFlingComposeList
import com.example.wear.snippets.list.isLargeDisplay


@Composable
fun ComposeList() {
    // [START android_wear_list]
    val columnState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(
        scrollState = columnState
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(text = "Header")
                }
            }
            // ... other items
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec),
                    onClick = { /* ... */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "build",
                        )
                    },
                ) {
                    Text(
                        text = "Build",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
    // [END android_wear_list]
}

@Composable
fun SnapAndFlingComposeList() {
    val transformationSpec = rememberTransformationSpec()
    // [START android_wear_snap]
    val columnState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = columnState) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            flingBehavior = TransformingLazyColumnDefaults.snapFlingBehavior(columnState),
            rotaryScrollableBehavior = RotaryScrollableDefaults.snapBehavior(columnState)
        ) {
            // ...
            // [START_EXCLUDE]
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(text = "Header")
                }
            }
            // ... other items
            item {
                IconButton(modifier = Modifier.transformedHeight(this, transformationSpec), onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Example Button"
                    )
                }
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_snap]
}

@Composable
fun EdgeButtonComposeList() {
    val transformationSpec = rememberTransformationSpec()
    // [START android_wear_list_edge_button]
    val columnState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = columnState,
        edgeButton = {
            EdgeButton(
                onClick = { /* TODO */ },
                modifier = Modifier.scrollable(
                    columnState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                    // Apply overscroll to the EdgeButton for proper scrolling behavior.
                    overscrollEffect = rememberOverscrollEffect(),
                )
            ) {
                Text("More")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            contentPadding = contentPadding,
            state = columnState,
        ) {
            // ...
            // [START_EXCLUDE]
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(text = "Header")
                }
            }
            // ... other items
            item {
                IconButton(modifier = Modifier.transformedHeight(this, transformationSpec), onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Example Button"
                    )
                }
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_list_edge_button]
}

@Composable
fun EnhancedComposeList() {
    // [START android_wear_tlc_reverse]
    val columnState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = columnState) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
            reverseLayout = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(10) { index ->
                Button(
                    label = {
                        Text(
                            text = "Item ${index + 1}"
                        )
                    },
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                )
            }
            item {
                // With reverseLayout = true, the last item declared appears at the top.
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text("Header")
                }
            }
        }
    }
    // [END android_wear_tlc_reverse]
}

// [START android_wear_list_breakpoint]
const val LARGE_DISPLAY_BREAKPOINT = 225

@Composable
fun isLargeDisplay() =
    LocalConfiguration.current.screenWidthDp >= LARGE_DISPLAY_BREAKPOINT

// [START_EXCLUDE]
@Composable
fun breakpointDemo() {
    // [END_EXCLUDE]
// ... use in your Composables:
    if (isLargeDisplay()) {
        // Show additional content.
    } else {
        // Show content only for smaller displays.
    }
    // [START_EXCLUDE]
}
// [END_EXCLUDE]
// [END android_wear_list_breakpoint]

// [START android_wear_list_preview]
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun ComposeListPreview() {
    ComposeList()
}
// [END android_wear_list_preview]

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SnapAndFlingComposeListPreview() {
    SnapAndFlingComposeList()
}
