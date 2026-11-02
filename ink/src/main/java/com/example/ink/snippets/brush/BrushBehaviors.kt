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

import androidx.ink.brush.BrushBehavior
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.BrushPaint
import androidx.ink.brush.BrushTip
import androidx.ink.brush.InputToolType
import androidx.ink.brush.SelfOverlap
import androidx.ink.brush.behavior.BinaryOpNode
import androidx.ink.brush.behavior.ConstantNode
import androidx.ink.brush.behavior.DampingNode
import androidx.ink.brush.behavior.EasingFunction
import androidx.ink.brush.behavior.IntegralNode
import androidx.ink.brush.behavior.InterpolationNode
import androidx.ink.brush.behavior.NoiseNode
import androidx.ink.brush.behavior.OutOfRange
import androidx.ink.brush.behavior.PolarTargetNode
import androidx.ink.brush.behavior.ProgressDomain
import androidx.ink.brush.behavior.ResponseNode
import androidx.ink.brush.behavior.SourceNode
import androidx.ink.brush.behavior.TargetNode
import androidx.ink.brush.behavior.ToolTypeFilterNode

private object BrushBehaviorsSnippets {
    // [START android_compose_stylus_ink_brush_behavior_source_node]
    val pressureSource = SourceNode(
        source = SourceNode.Source.NORMALIZED_PRESSURE,
        sourceValueRangeStart = 0f,
        sourceValueRangeEnd = 1f,
        sourceOutOfRangeBehavior = OutOfRange.CLAMP,
    )
    // [END android_compose_stylus_ink_brush_behavior_source_node]

    // [START android_compose_stylus_ink_brush_behavior_operator_nodes]
    // 2. ConstantNode: fixed scalar output
    val halfConstant = ConstantNode(0.5f)

    // 3. NoiseNode: procedural noise over stroke distance or time
    val distanceNoise = NoiseNode(
        seed = 42,
        varyOver = ProgressDomain.DISTANCE_IN_CENTIMETERS,
        basePeriod = 0.3f,
    )

    // 4. ToolTypeFilterNode: pass signal only for selected InputToolType set
    val stylusOnlyPressure = ToolTypeFilterNode(
        enabledToolTypes = setOf(InputToolType.STYLUS),
        input = pressureSource,
    )

    // 5. DampingNode: low-pass smoothing over time or distance
    val dampedPressure = DampingNode(
        dampOver = ProgressDomain.TIME_IN_SECONDS,
        strength = 0.15f,
        input = pressureSource,
    )

    // 6. ResponseNode: non-linear easing (Predefined, CubicBezier, Linear, Steps)
    val easedPressure = ResponseNode(
        responseCurve = EasingFunction.Predefined.EASE_IN_OUT,
        input = dampedPressure,
    )

    // 7. IntegralNode: accumulates input over time or distance
    val accumulatedPressure = IntegralNode(
        integrateOver = ProgressDomain.TIME_IN_SECONDS,
        integralValueRangeStart = 0f,
        integralValueRangeEnd = 1f,
        integralOutOfRangeBehavior = OutOfRange.CLAMP,
        input = pressureSource,
    )

    // 8. BinaryOpNode: PRODUCT, SUM, MIN, MAX, AND_THEN, OR_ELSE, XOR_ELSE
    val scaledPressure = BinaryOpNode(
        operation = BinaryOpNode.BinaryOp.PRODUCT,
        firstInput = easedPressure,
        secondInput = halfConstant,
    )

    // 9. InterpolationNode: LERP or INVERSE_LERP between startInput and endInput
    val lerpedNode = InterpolationNode(
        interpolation = InterpolationNode.Interpolation.LERP,
        paramInput = easedPressure,
        startInput = ConstantNode(0.2f),
        endInput = ConstantNode(1.0f),
    )
    // [END android_compose_stylus_ink_brush_behavior_operator_nodes]

    // [START android_compose_stylus_ink_brush_behavior_polar_target_node]
    val polarScatterBehavior = BrushBehavior(
        terminalNode = PolarTargetNode(
            target = PolarTargetNode.PolarTarget.POSITION_OFFSET_ABSOLUTE_IN_RADIANS_AND_MULTIPLES_OF_BRUSH_SIZE,
            angleRangeStart = 0f,
            angleRangeEnd = (2 * Math.PI).toFloat(),
            angleInput = NoiseNode(
                seed = 101,
                varyOver = ProgressDomain.DISTANCE_IN_CENTIMETERS,
                basePeriod = 0.2f,
            ),
            magnitudeRangeStart = 0f,
            magnitudeRangeEnd = 0.5f,
            magnitudeInput = SourceNode(
                source = SourceNode.Source.NORMALIZED_PRESSURE,
                sourceValueRangeStart = 0f,
                sourceValueRangeEnd = 1f,
            ),
        ),
    )
    // [END android_compose_stylus_ink_brush_behavior_polar_target_node]

    // [START android_compose_stylus_ink_brush_behavior_reusable_patterns]
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

    // 1. Pressure -> Size (50% to 150% with 150ms damping)
    val pressureToSize = smoothedBehavior(
        source = SourceNode.Source.NORMALIZED_PRESSURE,
        sourceStart = 0f,
        sourceEnd = 1f,
        target = TargetNode.Target.SIZE_MULTIPLIER,
        targetStart = 0.5f,
        targetEnd = 1.5f,
        dampingSeconds = 0.15f,
    )

    // 2. Stylus Tilt -> Width (1x to 2.5x as tilt increases to pi/2)
    val tiltToWidth = smoothedBehavior(
        source = SourceNode.Source.TILT_IN_RADIANS,
        sourceStart = 0f,
        sourceEnd = (Math.PI / 2).toFloat(),
        target = TargetNode.Target.WIDTH_MULTIPLIER,
        targetStart = 1.0f,
        targetEnd = 2.5f,
        dampingSeconds = 0.1f,
    )

    // 3. Organic distance-based wobble using NoiseNode -> lateral position offset
    fun createWobblyBrush(): BrushFamily {
        val wobbleBehavior = BrushBehavior(
            terminalNode = TargetNode(
                target = TargetNode.Target.POSITION_OFFSET_LATERAL_IN_MULTIPLES_OF_BRUSH_SIZE,
                targetModifierRangeStart = -0.25f,
                targetModifierRangeEnd = 0.25f,
                input = NoiseNode(
                    seed = 12345,
                    varyOver = ProgressDomain.DISTANCE_IN_CENTIMETERS,
                    basePeriod = 0.4f,
                ),
            ),
        )
        return BrushFamily(
            tip = BrushTip(
                scaleX = 1f,
                scaleY = 1f,
                cornerRounding = 1f,
                behaviors = listOf(wobbleBehavior),
            ),
            paint = BrushPaint(selfOverlap = SelfOverlap.ANY),
        )
    }
    // [END android_compose_stylus_ink_brush_behavior_reusable_patterns]
}
