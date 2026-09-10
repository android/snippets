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

@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")

package com.example.compose.snippets.performance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// [START android_compose_performance_backwards_write_read_comp_write_layout]
// [START android_compose_performance_backwards_write_layout_bad]
// ❌ BAD: Read in Composition, Written in Layout (onSizeChanged)
@Composable
fun BadAspectRatioImage(painter: Painter) {
    var calculatedHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    // State read during COMPOSITION:
    Image(
        painter = painter,
        contentDescription = "Dynamic Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(calculatedHeight)
            .onSizeChanged { size ->
                // State write during LAYOUT phase!
                // Triggers backwards write and recomposition pass
                val aspectRatio = 16f / 9f
                val widthDp = with(density) { size.width.toDp() }
                calculatedHeight = widthDp / aspectRatio
            }
    )
}

// ✅ GOOD: Measure and calculate aspect ratio height in Phase 2 (Layout) without recomposition
@Composable
fun GoodAspectRatioImage(
    painter: Painter,
    aspectRatio: Float = 16f / 9f,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            Image(
                painter = painter,
                contentDescription = "Dynamic Image"
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = (width / aspectRatio).toInt() // Illustrative, you can use Modifier.aspectRatio()
        val imageConstraints = constraints.copy(
            minWidth = width,
            maxWidth = width,
            minHeight = height,
            maxHeight = height
        )
        val placeable = measurables.first().measure(imageConstraints)
        layout(width, height) {
            placeable.placeRelative(0, 0)
        }
    }
}
// [END android_compose_performance_backwards_write_layout_good]


// [START android_compose_performance_backwards_write_layout_subcomposition]
// ✅ Okay option: Subcomposition delays Composition until parent constraints are known
@Composable
fun SizedContent() {
    BoxWithConstraints {
        // maxHeight and maxWidth are known during this sub-composition pass
        Text(text = "Height constraint is: $maxHeight")
    }
}
// [END android_compose_performance_backwards_write_layout_subcomposition]
// [END android_compose_performance_backwards_write_read_comp_write_layout]

// [START android_compose_performance_backwards_write_read_comp_write_draw]
// ❌ BAD: Read in Composition, Written in Draw ()
@Composable
fun BadBackwardsWriteDraw() {
    var componentHeight by remember { mutableStateOf(0.dp) }
    // State read during COMPOSITION:
    Text(
        text = "Height is: $componentHeight",
        modifier = Modifier.drawBehind {
            // State write during the DRAW phase!
            // Invalidates Composition -> triggers recomposition loop!
            componentHeight = size.height.dp
        }
    )
}
// [END android_compose_performance_backwards_write_read_comp_write_draw]

// [START android_compose_performance_backwards_write_read_comp_write_comp]
// [START android_compose_performance_backwards_write_bad_counter]
// ❌ BAD: Direct write in Composable body after read
@Composable
fun BadCounter() {
    var count by remember { mutableIntStateOf(0) }
    Text("Count: $count") // State read in Composition
    Box {
        count += 1 // State write in Composition (Backwards write!)
    }
}
// [END android_compose_performance_backwards_write_bad_counter]

// [START android_compose_performance_backwards_write_ok_counter]
// Acceptable - but error-prone as someone may add a read before the write : Direct write in Composable body before read
@Composable
fun OkCounter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = {}) {
        count++ // State  write in Composition
    }
    Text("Count: $count") // State read in Composition
}
// [END android_compose_performance_backwards_write_ok_counter]
// [END android_compose_performance_backwards_write_read_comp_write_comp]
