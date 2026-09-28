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

package com.example.wear.snippets.m3.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.example.wear.R

@Composable
fun EdgeButton() {
    val transformationSpec = rememberTransformationSpec()
    // [START android_wear_edgebutton]
    val state = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = state,
        edgeButton = {
            EdgeButton(
                onClick = { },
                modifier = Modifier.scrollable(
                    state,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                    // Apply overscroll to the EdgeButton for proper scrolling behavior.
                    overscrollEffect = rememberOverscrollEffect(),
                ),
            ) {
                Text(stringResource(R.string.show))
            }
        },
    ) { contentPadding ->
        TransformingLazyColumn(state = state, contentPadding = contentPadding) {
            // additional code here
            // [START_EXCLUDE]
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            ListHeaderDefaults.minimumTopListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(text = "Header")
                }
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_wear_edgebutton]
}

@Composable
fun buttonsList() {
    // [START android_wear_buttons_list]
    //M3 Buttons
    Button(onClick = { }){}
    CompactButton(onClick = { }){}
    IconButton(onClick = { }){}
    TextButton(onClick = { }){}
    // [END android_wear_buttons_list]
}
