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

package com.example.ink.snippets.brush

import androidx.compose.ui.graphics.Color
import androidx.ink.brush.BrushBehavior
import androidx.ink.brush.BrushCoat
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.BrushPaint
import androidx.ink.brush.BrushTip
import androidx.ink.brush.SelfOverlap
import androidx.ink.brush.behavior.DampingNode
import androidx.ink.brush.behavior.OutOfRange
import androidx.ink.brush.behavior.ProgressDomain
import androidx.ink.brush.behavior.SourceNode
import androidx.ink.brush.behavior.TargetNode

private object BrushStructureSnippets {
    // [START android_compose_stylus_ink_brush_family_and_coat]
    val singleCoatFamily = BrushFamily(
        tip = BrushTip(scaleX = 1f, scaleY = 1f, cornerRounding = 1f),
        paint = BrushPaint(selfOverlap = SelfOverlap.ANY),
    )

    val multiCoatFamily = BrushFamily(
        coats = listOf(
            // Coat 0: Wider semi-transparent shadow coat rendered underneath
            BrushCoat(
                tip = BrushTip(scaleX = 1.3f, scaleY = 1.3f, cornerRounding = 1f),
                paint = BrushPaint(
                    selfOverlap = SelfOverlap.DISCARD,
                    colorFunctions = listOf(BrushPaint.ColorFunction.OpacityMultiplier(0.15f)),
                ),
            ),
            // Coat 1: Main opaque stroke coat rendered on top
            BrushCoat(
                tip = BrushTip(scaleX = 1f, scaleY = 1f, cornerRounding = 1f),
                paint = BrushPaint(selfOverlap = SelfOverlap.DISCARD),
            ),
        ),
    )

    val roundedVariant = singleCoatFamily.copy(
        coats = listOf(
            singleCoatFamily.coats.first().let { coat ->
                coat.copy(tip = coat.tip.copy(cornerRounding = 0.5f))
            }
        ),
    )
    // [END android_compose_stylus_ink_brush_family_and_coat]

    // [START android_compose_stylus_ink_brush_tip_shapes]
    // Narrow angled calligraphy nib (static angle in degrees)
    val calligraphyTip = BrushTip(
        scaleX = 0.3f,
        scaleY = 1.0f,
        cornerRounding = 0.2f,
        rotationDegrees = 45f,
    )

    // Wide flat marker tip
    val markerTip = BrushTip(
        scaleX = 2.0f,
        scaleY = 0.5f,
        cornerRounding = 0.4f,
    )
    // [END android_compose_stylus_ink_brush_tip_shapes]

    // [START android_compose_stylus_ink_brush_paint_color_functions]
    val translucentPaint = BrushPaint(
        selfOverlap = SelfOverlap.ANY,
        colorFunctions = listOf(
            BrushPaint.ColorFunction.OpacityMultiplier(0.7f),
        ),
    )

    val fixedBlackShadowPaint = BrushPaint(
        selfOverlap = SelfOverlap.DISCARD,
        colorFunctions = listOf(
            BrushPaint.ColorFunction.ReplaceColor.withColorLong(Color.Black.value.toLong()),
            BrushPaint.ColorFunction.OpacityMultiplier(0.2f),
        ),
    )
    // [END android_compose_stylus_ink_brush_paint_color_functions]
}

// [START android_compose_stylus_ink_brush_sliding_window_model]
/**
 * Creates a custom [BrushFamily] configured for smooth, low-jitter strokes.
 *
 * @param windowDurationMillis Sliding-window duration in milliseconds (default 30ms)
 *   controlling input path smoothing versus latency.
 * @param upsamplingFrequencyHz Target upsampling frequency in Hz (default 180Hz)
 *   for smooth curve subdivision.
 */
fun createSmoothBrushFamily(
    windowDurationMillis: Long = 30L,
    upsamplingFrequencyHz: Int = 180,
): BrushFamily {
    return BrushFamily(
        coats = listOf(
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1f,
                    scaleY = 1f,
                    cornerRounding = 1f,
                    behaviors = listOf(
                        BrushBehavior(
                            terminalNode = TargetNode(
                                target = TargetNode.Target.SIZE_MULTIPLIER,
                                targetModifierRangeStart = 0.5f,
                                targetModifierRangeEnd = 1.5f,
                                input = DampingNode(
                                    dampOver = ProgressDomain.TIME_IN_SECONDS,
                                    strength = 0.15f,
                                    input = SourceNode(
                                        source = SourceNode.Source.NORMALIZED_PRESSURE,
                                        sourceValueRangeStart = 0f,
                                        sourceValueRangeEnd = 1f,
                                        sourceOutOfRangeBehavior = OutOfRange.CLAMP,
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                paint = BrushPaint(selfOverlap = SelfOverlap.ANY),
            ),
        ),
        inputModel = BrushFamily.InputModel.SlidingWindowModel(
            windowDurationMillis = windowDurationMillis,
            upsamplingFrequencyHz = upsamplingFrequencyHz,
        ),
    )
}
// [END android_compose_stylus_ink_brush_sliding_window_model]
