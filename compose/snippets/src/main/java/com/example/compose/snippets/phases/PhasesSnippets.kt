/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.phases

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
private fun StateReadWithoutPropertyDelegate() {
    // [START android_compose_phases_state_read_without_property_delegate]
    // State read without property delegate.
    val paddingState: MutableState<Dp> = remember { mutableStateOf(8.dp) }
    Text(
        text = "Hello",
        modifier = Modifier.padding(paddingState.value)
    )
    // [END android_compose_phases_state_read_without_property_delegate]
}

@Suppress("CanBeVal")
@Composable
private fun StateReadWithPropertyDelegate() {
    // [START android_compose_phases_state_read_with_property_delegate]
    // State read with property delegate.
    var padding: Dp by remember { mutableStateOf(8.dp) }
    Text(
        text = "Hello",
        modifier = Modifier.padding(padding)
    )
    // [END android_compose_phases_state_read_with_property_delegate]
}

@Suppress("CanBeVal")
@Composable
private fun CompositionSnippet() {
    // [START android_compose_phases_composition]
    var padding by remember { mutableStateOf(8.dp) }
    Text(
        text = "Hello",
        // The `padding` state is read in the composition phase
        // when the modifier is constructed.
        // Changes in `padding` will invoke recomposition.
        modifier = Modifier.padding(padding)
    )
    // [END android_compose_phases_composition]
}

@Suppress("CanBeVal")
@Composable
private fun LayoutSnippet() {
    // [START android_compose_phases_layout]
    var offsetX by remember { mutableStateOf(8.dp) }
    Text(
        text = "Hello",
        modifier = Modifier.offset {
            // The `offsetX` state is read in the placement step
            // of the layout phase when the offset is calculated.
            // Changes in `offsetX` restart the layout.
            IntOffset(offsetX.roundToPx(), 0)
        }
    )
    // [END android_compose_phases_layout]
}

@Suppress("CanBeVal")
@Composable
private fun DrawingSnippet(modifier: Modifier) {
    // [START android_compose_phases_drawing]
    var color by remember { mutableStateOf(Color.Red) }
    Canvas(modifier = modifier) {
        // The `color` state is read in the drawing phase
        // when the canvas is rendered.
        // Changes in `color` restart the drawing.
        drawRect(color)
    }
    // [END android_compose_phases_drawing]
}

@Composable
private fun OptimizeStateReadsBefore() {
    // [START android_compose_phases_optimize_state_reads_before]
    Box {
        val listState = rememberLazyListState()

        Image(
            // [START_EXCLUDE]
            painterResource(id = android.R.drawable.star_on),
            contentDescription = null,
            // [END_EXCLUDE]
            // Non-optimal implementation!
            Modifier.offset(
                with(LocalDensity.current) {
                    // State read of firstVisibleItemScrollOffset in composition
                    (listState.firstVisibleItemScrollOffset / 2).toDp()
                }
            )
        )

        LazyColumn(state = listState) {
            // [START_EXCLUDE]
            // [END_EXCLUDE]
        }
    }
    // [END android_compose_phases_optimize_state_reads_before]
}

@Composable
private fun OptimizeStateReadsAfter() {
    // [START android_compose_phases_optimize_state_reads_after]
    Box {
        val listState = rememberLazyListState()

        Image(
            // [START_EXCLUDE]
            painterResource(id = android.R.drawable.star_on),
            contentDescription = null,
            // [END_EXCLUDE]
            Modifier.offset {
                // State read of firstVisibleItemScrollOffset in Layout
                IntOffset(x = 0, y = listState.firstVisibleItemScrollOffset / 2)
            }
        )

        LazyColumn(state = listState) {
            // [START_EXCLUDE]
            // [END_EXCLUDE]
        }
    }
    // [END android_compose_phases_optimize_state_reads_after]
}

private object Loop {
    @Suppress("ClassName")
    object R {
        object drawable {
            const val rectangle = 1
        }
    }

    @Composable
    fun Loop() {
        // [START android_compose_phases_loop]
        Box {
            var imageHeightPx by remember { mutableStateOf(0) }

            Image(
                painter = painterResource(R.drawable.rectangle),
                contentDescription = "I'm above the text",
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        // Don't do this
                        imageHeightPx = size.height
                    }
            )

            Text(
                text = "I'm below the image",
                modifier = Modifier.padding(
                    top = with(LocalDensity.current) { imageHeightPx.toDp() }
                )
            )
        }
        // [END android_compose_phases_loop]
    }
}
