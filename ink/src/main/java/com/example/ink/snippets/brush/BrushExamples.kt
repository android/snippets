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
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushBehavior
import androidx.ink.brush.BrushCoat
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.BrushPaint
import androidx.ink.brush.BrushTip
import androidx.ink.brush.SelfOverlap
import androidx.ink.brush.behavior.DampingNode
import androidx.ink.brush.behavior.NoiseNode
import androidx.ink.brush.behavior.OutOfRange
import androidx.ink.brush.behavior.ProgressDomain
import androidx.ink.brush.behavior.SourceNode
import androidx.ink.brush.behavior.TargetNode
import androidx.ink.brush.compose.createWithComposeColor

// [START android_compose_stylus_ink_brush_recipe_helpers]
fun smoothedBehavior(
    source: SourceNode.Source,
    sourceStart: Float,
    sourceEnd: Float,
    target: TargetNode.Target,
    targetStart: Float,
    targetEnd: Float,
    dampingSeconds: Float,
    outOfRange: OutOfRange = OutOfRange.CLAMP,
): BrushBehavior = BrushBehavior(
    terminalNode = TargetNode(
        target = target,
        targetModifierRangeStart = targetStart,
        targetModifierRangeEnd = targetEnd,
        input = DampingNode(
            dampOver = ProgressDomain.TIME_IN_SECONDS,
            strength = dampingSeconds,
            input = SourceNode(
                source = source,
                sourceValueRangeStart = sourceStart,
                sourceValueRangeEnd = sourceEnd,
                sourceOutOfRangeBehavior = outOfRange,
            ),
        ),
    ),
)

fun jitterBehavior(
    target: TargetNode.Target,
    targetStart: Float,
    targetEnd: Float,
    basePeriod: Float,
    seed: Int = 42,
): BrushBehavior = BrushBehavior(
    terminalNode = TargetNode(
        target = target,
        targetModifierRangeStart = targetStart,
        targetModifierRangeEnd = targetEnd,
        input = NoiseNode(
            seed = seed,
            varyOver = ProgressDomain.DISTANCE_IN_CENTIMETERS,
            basePeriod = basePeriod,
        ),
    ),
)
// [END android_compose_stylus_ink_brush_recipe_helpers]

// [START android_compose_stylus_ink_brush_recipe_pressure_pen]
fun createPressureResponsiveBrush(): Brush {
    val pressurePenFamily = BrushFamily(
        coats = listOf(
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1f,
                    scaleY = 1f,
                    cornerRounding = 1f,
                    behaviors = listOf(
                        smoothedBehavior(
                            source = SourceNode.Source.NORMALIZED_PRESSURE,
                            sourceStart = 0f,
                            sourceEnd = 1f,
                            target = TargetNode.Target.SIZE_MULTIPLIER,
                            targetStart = 0.5f,
                            targetEnd = 1.5f,
                            dampingSeconds = 0.15f,
                        ),
                        smoothedBehavior(
                            source = SourceNode.Source.NORMALIZED_PRESSURE,
                            sourceStart = 0f,
                            sourceEnd = 1f,
                            target = TargetNode.Target.OPACITY_MULTIPLIER,
                            targetStart = 0.3f,
                            targetEnd = 1.0f,
                            dampingSeconds = 0.1f,
                        ),
                    ),
                ),
                paint = BrushPaint(selfOverlap = SelfOverlap.ANY),
            ),
        ),
    )

    return Brush.createWithComposeColor(
        family = pressurePenFamily,
        color = Color.Black,
        size = 10f,
        epsilon = 0.1f,
    )
}
// [END android_compose_stylus_ink_brush_recipe_pressure_pen]

// [START android_compose_stylus_ink_brush_recipe_calligraphy]
fun createCalligraphyBrush(): BrushFamily {
    return BrushFamily(
        coats = listOf(
            BrushCoat(
                tip = BrushTip(
                    scaleX = 0.3f,
                    scaleY = 1.0f,
                    cornerRounding = 0.2f,
                    rotationDegrees = 45f,
                    behaviors = listOf(
                        smoothedBehavior(
                            source = SourceNode.Source.TILT_IN_RADIANS,
                            sourceStart = 0f,
                            sourceEnd = (Math.PI / 2).toFloat(),
                            target = TargetNode.Target.WIDTH_MULTIPLIER,
                            targetStart = 1.0f,
                            targetEnd = 2.5f,
                            dampingSeconds = 0.1f,
                        ),
                        smoothedBehavior(
                            source = SourceNode.Source.DIRECTION_IN_RADIANS,
                            sourceStart = 0f,
                            sourceEnd = (2 * Math.PI).toFloat(),
                            target = TargetNode.Target.ROTATION_OFFSET_IN_RADIANS,
                            targetStart = (-Math.PI).toFloat(),
                            targetEnd = Math.PI.toFloat(),
                            dampingSeconds = 0.05f,
                            outOfRange = OutOfRange.REPEAT,
                        ),
                    ),
                ),
                paint = BrushPaint(selfOverlap = SelfOverlap.DISCARD),
            ),
        ),
    )
}
// [END android_compose_stylus_ink_brush_recipe_calligraphy]

// [START android_compose_stylus_ink_brush_recipe_shading_pencil]
fun createShadingPencilBrush(): BrushFamily {
    return BrushFamily(
        coats = listOf(
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1f,
                    scaleY = 1f,
                    cornerRounding = 1f,
                    behaviors = listOf(
                        smoothedBehavior(
                            source = SourceNode.Source.TILT_IN_RADIANS,
                            sourceStart = 0f,
                            sourceEnd = (Math.PI / 2).toFloat(),
                            target = TargetNode.Target.WIDTH_MULTIPLIER,
                            targetStart = 1.0f,
                            targetEnd = 2.5f,
                            dampingSeconds = 0.1f,
                        ),
                        smoothedBehavior(
                            source = SourceNode.Source.NORMALIZED_PRESSURE,
                            sourceStart = 0f,
                            sourceEnd = 1f,
                            target = TargetNode.Target.OPACITY_MULTIPLIER,
                            targetStart = 0.3f,
                            targetEnd = 1.0f,
                            dampingSeconds = 0.1f,
                        ),
                        jitterBehavior(
                            target = TargetNode.Target.SLANT_OFFSET_IN_RADIANS,
                            targetStart = -0.15f,
                            targetEnd = 0.15f,
                            basePeriod = 0.3f,
                        ),
                    ),
                ),
                paint = BrushPaint(
                    selfOverlap = SelfOverlap.ANY,
                    colorFunctions = listOf(BrushPaint.ColorFunction.OpacityMultiplier(0.8f)),
                    textureLayers = listOf(
                        BrushPaint.TilingTexture(
                            clientTextureId = "pencil-grain",
                            sizeX = 1.0f,
                            sizeY = 1.0f,
                            sizeUnit = BrushPaint.TextureLayer.SizeUnit.BRUSH_SIZE,
                            blendMode = BrushPaint.TextureLayer.BlendMode.MODULATE,
                        ),
                    ),
                ),
            ),
        ),
    )
}
// [END android_compose_stylus_ink_brush_recipe_shading_pencil]

// [START android_compose_stylus_ink_brush_recipe_watercolor]
fun createWatercolorBrush(): BrushFamily {
    return BrushFamily(
        coats = listOf(
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1f,
                    scaleY = 1f,
                    cornerRounding = 1f,
                    behaviors = listOf(
                        smoothedBehavior(
                            source = SourceNode.Source.SPEED_IN_MULTIPLES_OF_BRUSH_SIZE_PER_SECOND,
                            sourceStart = 0f,
                            sourceEnd = 8f,
                            target = TargetNode.Target.OPACITY_MULTIPLIER,
                            targetStart = 1.0f,
                            targetEnd = 0.2f,
                            dampingSeconds = 0.3f,
                        ),
                    ),
                ),
                paint = BrushPaint(
                    selfOverlap = SelfOverlap.ANY,
                    colorFunctions = listOf(BrushPaint.ColorFunction.OpacityMultiplier(0.6f)),
                    textureLayers = listOf(
                        BrushPaint.TilingTexture(
                            clientTextureId = "watercolor-wash",
                            sizeX = 2.0f,
                            sizeY = 2.0f,
                            sizeUnit = BrushPaint.TextureLayer.SizeUnit.BRUSH_SIZE,
                            blendMode = BrushPaint.TextureLayer.BlendMode.MODULATE,
                        ),
                    ),
                ),
            ),
        ),
    )
}
// [END android_compose_stylus_ink_brush_recipe_watercolor]

// [START android_compose_stylus_ink_brush_recipe_multi_coat]
fun createMultiCoatBrush(): BrushFamily {
    return BrushFamily(
        coats = listOf(
            // Coat 0: Wider semi-transparent shadow layer underneath
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1.3f,
                    scaleY = 1.3f,
                    cornerRounding = 1f,
                ),
                paint = BrushPaint(
                    selfOverlap = SelfOverlap.DISCARD,
                    colorFunctions = listOf(
                        BrushPaint.ColorFunction.OpacityMultiplier(0.15f),
                    ),
                ),
            ),
            // Coat 1: Main opaque stroke layer on top
            BrushCoat(
                tip = BrushTip(
                    scaleX = 1f,
                    scaleY = 1f,
                    cornerRounding = 1f,
                    behaviors = listOf(
                        smoothedBehavior(
                            source = SourceNode.Source.NORMALIZED_PRESSURE,
                            sourceStart = 0f,
                            sourceEnd = 1f,
                            target = TargetNode.Target.SIZE_MULTIPLIER,
                            targetStart = 0.5f,
                            targetEnd = 1.5f,
                            dampingSeconds = 0.15f,
                        ),
                    ),
                ),
                paint = BrushPaint(
                    selfOverlap = SelfOverlap.DISCARD,
                    colorFunctions = listOf(
                        BrushPaint.ColorFunction.OpacityMultiplier(1f),
                    ),
                ),
            ),
        ),
    )
}
// [END android_compose_stylus_ink_brush_recipe_multi_coat]
