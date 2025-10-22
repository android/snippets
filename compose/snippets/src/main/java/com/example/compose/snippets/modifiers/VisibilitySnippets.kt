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

package com.example.compose.snippets.modifiers

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

private object OnVisibilityChangedSample {

    @Composable
    fun OnVisibilityChangedExample(modifier: Modifier = Modifier) {
        // [START android_compose_modifiers_onVisibilityChanged]
        Text(
            text = "Some text",
            modifier = Modifier
                .onVisibilityChanged { visible ->
                    if (visible) {
                        // Do something if visible
                    } else {
                        // Do something if not visible
                    }
                }
                .padding(vertical = 8.dp)
        )
        // [END android_compose_modifiers_onVisibilityChanged]
    }
}

private object OnVisibilityChangedMinFractionVisible {

    @Composable
    fun OnVisibilityChangedModifierMinFractionExample(modifier: Modifier = Modifier) {
        // [START android_compose_modifiers_onVisibilityChangedMinFraction]
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        // [START_EXCLUDE]
                        .fillParentMaxWidth()
                        .border(0.5.dp, Color.Gray)
                        // [END_EXCLUDE]
                        // Here the visible callback gets triggered when 20% of the composable is visible
                        .onVisibilityChanged(
                            minFractionVisible = 0.2f,
                        ) { visible ->
                            if (visible) {
                                // Call specific logic here
                                // viewModel.fetchDataFromNetwork()
                            }
                        }
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Sample Text",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
        // [END android_compose_modifiers_onVisibilityChangedMinFraction]
    }
}

private object onVisibilityChangedMinDuration {

    val MutedPlum = Color(0xFF7B4B6B)
    val PalePink = Color(0xFFF3E9EB)

    @Composable
    fun OnVisibilityChangedMinDurationExample(
        @DrawableRes imageRes: Int,
        modifier: Modifier = Modifier,
    ) {
        // [START android_compose_modifiers_onVisibilityChangedMinDuration]
        var background by remember { mutableStateOf(PalePink) }
        Card(
            modifier = modifier
                // [START_EXCLUDE]
                .height(300.dp)
                .fillMaxWidth()
                // [END_EXCLUDE]
                .onVisibilityChanged(minDurationMs = 3000) {
                    if (it) {
                        background = MutedPlum
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    // [START_EXCLUDE]
                    .fillMaxSize()
                    // [END_EXCLUDE]
                    .background(background),
                contentAlignment = Alignment.Center,
            ) {
                // [START_EXCLUDE]
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Androidify Bot",
                )
                // [END_EXCLUDE]
            }
        }
        // [END android_compose_modifiers_onVisibilityChangedMinDuration]
    }
}


private object OnFirstVisibleSample {

    @Composable
    fun OnFirstVisibleExample(id: Int, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // [START android_compose_modifiers_onFirstVisible]
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .onFirstVisible {
                        println("OnFirstVisible : ProductCard: $id is visible")
                    }
                    .padding(8.dp),
                text = "Product $id"
            )
            // [END android_compose_modifiers_onFirstVisible]
        }
    }
}