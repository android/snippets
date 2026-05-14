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

package com.example.xr.glimmer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.list.GlimmerLazyColumn
import androidx.xr.glimmer.surface

// [START xr_glimmer_shapes]
@Composable
fun ShapesSample() {
    val shapes = GlimmerTheme.shapes
    GlimmerLazyColumn {
        item { ShapeItem("small", shape = shapes.small) }
        item { ShapeItem("medium", shape = shapes.medium) }
        item { ShapeItem("large", shape = shapes.large) }
    }
}

@Composable
private fun ShapeItem(name: String, shape: Shape, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier
            .aspectRatio(2.5f)
            .fillMaxWidth()
            .surface(interactionSource = interactionSource, shape = shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(name)
    }
}
// [END xr_glimmer_shapes]


// [START xr_glimmer_custom_shapes]
@Composable
fun CustomShapesSample() {
    val customShapes = GlimmerTheme.shapes.copy(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(20.dp)
    )

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .aspectRatio(2.5f)
            .fillMaxWidth()
            .surface(interactionSource = interactionSource, shape = customShapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Text("custom small")
    }
}
// [END xr_glimmer_custom_shapes]
