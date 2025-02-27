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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.Icon
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.Text

@Composable
fun ComposeList() {
    // [START android_wear_list]
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = columnState,
        contentPadding = contentPadding) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader(modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)){
                    Text(text = "Header")
                }
            }
            // ... other items
            item {
                Button(modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                    onClick = { /* ... */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "build",
                        )
                    },
                ){
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

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SnapAndFlingComposeList() {
    // [START android_wear_snap]
    val columnState = rememberResponsiveColumnState(
        // ...
        // [START_EXCLUDE]
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton
        ),
        // [END_EXCLUDE]
        rotaryMode = ScalingLazyColumnState.RotaryMode.Snap
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            // ...
            // [START_EXCLUDE]
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    androidx.wear.compose.material.Text(text = "Header")
                }
            }
            // ... other items
            item {
                Button(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Example Button",
                    onClick = { }
                )
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_snap]
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
