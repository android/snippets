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

package com.example.wear.snippets.list
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ComposeList() {
    // [START android_wear_list]
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton
        )
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "Header")
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
                    Text(text = "Header")
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

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun ComposeListPreview() {
    ComposeList()
}
