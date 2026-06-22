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

package com.example.compose.snippets.graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun MeshGradientBasic(modifier: Modifier = Modifier) {
    // [START android_compose_graphics_mesh_gradient_basic]
    val rows = 1
    val columns = 1

    val gradientPainter = remember {
        MeshGradientPainter(rows, columns) {
            // Parameters: row, column, position, color
            setVertex(0, 0, Offset(0f, 0f), Color.Red)     // Top-Left
            setVertex(0, 1, Offset(1f, 0f), Color.Blue)    // Top-Right
            setVertex(1, 0, Offset(0f, 1f), Color.Green)   // Bottom-Left
            setVertex(1, 1, Offset(1f, 1f), Color.Yellow)  // Bottom-Right
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(16/9f)
            .fillMaxWidth()
            .paint(gradientPainter)
    )
    // [END android_compose_graphics_mesh_gradient_basic]
}

@Preview
@Composable
fun MeshGradientControlPoints(modifier: Modifier = Modifier) {
    // [START android_compose_graphics_mesh_gradient_control_points]
    val customTangentPainter = remember {
        MeshGradientPainter(rows = 1, columns = 1) {
            // Tweak the top-left vertex to curve outwards to the right and bottom
            setVertex(
                row = 0,
                column = 0,
                position = Offset(0f, 0f),
                color = Color.Magenta,
                rightControlPoint = Offset(0.4f, 0.1f),
                bottomControlPoint = Offset(0.1f, 0.4f)
            )

            // Other points can remain unspecified to use default inferred fallback tangents
            setVertex(0, 1, Offset(1f, 0f), Color.Cyan)
            setVertex(1, 0, Offset(0f, 1f), Color.Blue)
            setVertex(1, 1, Offset(1f, 1f), Color.Black)
        }
    }
    Box(
        modifier = modifier
            .aspectRatio(16/9f)
            .fillMaxWidth()
            .paint(customTangentPainter)
    )
    // [END android_compose_graphics_mesh_gradient_control_points]
}

@Preview
@Composable
fun MeshGradientComplex(modifier: Modifier = Modifier) {
    val purple = Color(0xFFAF52DE)
    val indigo = Color(0xFF5856D6)
    val yellow = Color(0xFFFFCC00)
    val pink = Color(0xFFFF2D55)
    val orange = Color(0xFFFF9500)
    // [START android_compose_graphics_mesh_gradient_complex]
    val points = remember {
        listOf(
            Offset(0.0f, 0.0f), Offset(0.3f, 0.0f), Offset(0.7f, 0.0f), Offset(1.0f, 0.0f),
            Offset(0.0f, 0.3f), Offset(0.2f, 0.4f), Offset(0.7f, 0.2f), Offset(1.0f, 0.3f),
            Offset(0.0f, 0.7f), Offset(0.3f, 0.8f), Offset(0.7f, 0.6f), Offset(1.0f, 0.7f),
            Offset(0.0f, 1.0f), Offset(0.3f, 1.0f), Offset(0.7f, 1.0f), Offset(1.0f, 1.0f)
        )
    }

    val gradientPainter = remember {
        MeshGradientPainter(rows = 3, columns = 3) {
            // Row 0
            setVertex(0, 0, points[0], yellow)
            setVertex(0, 1, points[1], orange)
            setVertex(0, 2, points[2], yellow)
            setVertex(0, 3, points[3], purple)

            // Row 1
            setVertex(1, 0, points[4], pink)
            setVertex(1, 1, points[5], yellow)
            setVertex(1, 2, points[6], pink)
            setVertex(1, 3, points[7], purple)

            // Row 2
            setVertex(2, 0, points[8], indigo)
            setVertex(2, 1, points[9], pink)
            setVertex(2, 2, points[10], purple)
            setVertex(2, 3, points[11], indigo)

            // Row 3
            setVertex(3, 0, points[12], purple)
            setVertex(3, 1, points[13], indigo)
            setVertex(3, 2, points[14], pink)
            setVertex(3, 3, points[15], yellow)
        }
    }

    Box(
        modifier = modifier.padding(32.dp)
            .aspectRatio(16 / 9f)
            .fillMaxWidth()
            .paint(gradientPainter)
            // [START_EXCLUDE]
            .drawWithContent {
                drawContent()
                points.forEach { normalizedOffset ->
                    val pixelOffset = Offset(
                        x = normalizedOffset.x * size.width,
                        y = normalizedOffset.y * size.height
                    )
                    // Draw a subtle black circle outline and white center for maximum visibility
                    drawCircle(
                        color = Color.Black,
                        radius = 10f,
                        center = pixelOffset
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = pixelOffset
                    )
                }
            }
            // [END_EXCLUDE]
    )
    // [END android_compose_graphics_mesh_gradient_complex]
}

@Preview
@Composable
fun MeshGradientAnimation(modifier: Modifier = Modifier) {
    // [START android_compose_graphics_mesh_gradient_animation]
    val infiniteTransition = rememberInfiniteTransition(label = "meshMovement")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val coral = Color(255, 90, 90)
    val peach = Color(255, 139, 90)
    val amber = Color(255, 169, 90)
    val sunshine = Color(255, 212, 90)
    val indigo = Color(0xFF5856D6)
    val pink = Color(0xFFFF2D55)


    val gradientPainter = remember {
        MeshGradientPainter(rows = 3, columns = 3) {
            // Row 0
            setVertex(0, 0, Offset(0.0f, 0.0f), indigo)
            setVertex(0, 1, Offset(0.3f, 0.0f), peach)
            setVertex(0, 2, Offset(0.7f, 0.0f), amber)
            setVertex(0, 3, Offset(1.0f, 0.0f), sunshine)
            // Row 1
            setVertex(1, 0, Offset(0.0f, 0.3f), pink)
            setVertex(1, 1, Offset(0.2f, 0.4f) + Offset(animatedOffset, animatedOffset), coral)
            setVertex(1, 2, Offset(0.7f, 0.2f) + Offset(animatedOffset, animatedOffset), peach)
            setVertex(1, 3, Offset(1.0f, 0.3f), indigo)

            // Row 2
            setVertex(2, 0, Offset(0.0f, 0.7f), coral)
            setVertex(2, 1, Offset(0.3f, 0.8f) + Offset(animatedOffset, 0f), pink)
            setVertex(2, 2, Offset(0.7f, 0.6f) + Offset(animatedOffset, 0f), sunshine)
            setVertex(2, 3, Offset(1.0f, 0.7f), amber)

            // Row 3
            setVertex(3, 0, Offset(0.0f, 1.0f), sunshine)
            setVertex(3, 1, Offset(0.3f, 1.0f), amber)
            setVertex(3, 2, Offset(0.7f, 1.0f), pink)
            setVertex(3, 3, Offset(1.0f, 1.0f), indigo)
        }
    }


    Box(
        modifier = modifier.padding(32.dp)
            .safeContentPadding()
            .aspectRatio(16 / 9f)
            .fillMaxWidth()
            .paint(gradientPainter)
    )
    // [END android_compose_graphics_mesh_gradient_animation]
}