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

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.example.compose.snippets.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.example.compose.snippets.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Supporting declarations used by the snippets below.
private data class MigrationColorScheme(val primaryColor: Color)

private val LocalColorScheme = staticCompositionLocalOf { MigrationColorScheme(Color.Blue) }

private object MyTheme {
    val mainColor: Color
        @Composable get() = LocalColorScheme.current.primaryColor
}

private data class ThemePadding(val small: Dp)

private val LocalThemePadding = staticCompositionLocalOf { ThemePadding(small = 4.dp) }

private object PressScaleComposedSnippet {
    // [START android_compose_modifier_composed_migration_press_scale]
    // ❌ BAD: Using Modifier.composed is no longer recommended
    fun Modifier.pressScale(
        pressedScale: Float = 0.95f,
        onClick: () -> Unit
    ): Modifier = composed(
        inspectorInfo = debugInspectorInfo {
            name = "pressScale"
            properties["pressedScale"] = pressedScale
        }
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) pressedScale else 1f,
            animationSpec = spring(),
            label = "pressScale"
        )

        this
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    }
    // [END android_compose_modifier_composed_migration_press_scale]
}

private object UnderlineComposedSnippet {
    // [START android_compose_modifier_composed_migration_underline_composed]
    fun Modifier.underline(
        color: Color,
        thickness: Dp = 2.dp,
        animationDurationMillis: Int = 300
    ): Modifier = composed {
        val density = LocalDensity.current
        val strokePx = with(density) { thickness.toPx() }

        // Drives how much of the underline is drawn: 0f -> 1f
        val progress = remember { Animatable(0f) }

        LaunchedEffect(color, thickness) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animationDurationMillis)
            )
        }

        drawBehind {
            val y = size.height - strokePx / 2
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(size.width * progress.value, y),
                strokeWidth = strokePx
            )
        }
    }
    // [END android_compose_modifier_composed_migration_underline_composed]
}

private object UnderlineNodeStep1Snippet {
    // [START android_compose_modifier_composed_migration_underline_node_step1]
    private class UnderlineNode(
        private var color: Color,
        private var thickness: Dp,
        private var animationDurationMillis: Int
    ) : Modifier.Node() {
        fun update(color: Color, thickness: Dp, durationMillis: Int) {
        }
    }
    // [END android_compose_modifier_composed_migration_underline_node_step1]
}

private object UnderlineNodeSnippets {
    // [START android_compose_modifier_composed_migration_underline_node_step2]
    private class UnderlineNode(
        private var color: Color,
        private var thickness: Dp,
        private var animationDurationMillis: Int
    ) : Modifier.Node(), DrawModifierNode {

        private val progress = Animatable(0f)
        private var animationJob: Job? = null

        override fun onAttach() {
            restartAnimation()
        }

        fun update(color: Color, thickness: Dp, durationMillis: Int) {
            val needsRestart = this.color != color || this.thickness != thickness
            this.color = color
            this.thickness = thickness
            this.animationDurationMillis = durationMillis
            if (needsRestart) restartAnimation()
        }

        private fun restartAnimation() {
            animationJob?.cancel()
            animationJob = coroutineScope.launch {
                progress.snapTo(0f)
                progress.animateTo(1f, tween(animationDurationMillis))
            }
        }

        override fun ContentDrawScope.draw() {
            val strokePx = thickness.toPx()
            val y = size.height - strokePx / 2
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(size.width * progress.value, y),
                strokeWidth = strokePx
            )
            drawContent()
        }
    }
    // [END android_compose_modifier_composed_migration_underline_node_step2]

    // [START android_compose_modifier_composed_migration_underline_element]
    private class UnderlineElement(
        private val color: Color,
        private val thickness: Dp,
        private val animationDurationMillis: Int
    ) : ModifierNodeElement<UnderlineNode>() {

        override fun create() = UnderlineNode(color, thickness, animationDurationMillis)

        override fun update(node: UnderlineNode) {
            node.update(color, thickness, animationDurationMillis)
        }

        override fun InspectorInfo.inspectableProperties() {
            name = "underline"
            properties["color"] = color
            properties["thickness"] = thickness
            properties["animationDurationMillis"] = animationDurationMillis
        }

        override fun hashCode(): Int {
            var result = color.hashCode()
            result = 31 * result + thickness.hashCode()
            result = 31 * result + animationDurationMillis.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            val otherElement = other as? UnderlineElement ?: return false
            return color == otherElement.color &&
                thickness == otherElement.thickness &&
                animationDurationMillis == otherElement.animationDurationMillis
        }
    }
    // [END android_compose_modifier_composed_migration_underline_element]

    // [START android_compose_modifier_composed_migration_underline_factory]
    fun Modifier.underline(
        color: Color,
        thickness: Dp = 2.dp,
        animationDurationMillis: Int = 300
    ): Modifier = this then UnderlineElement(color, thickness, animationDurationMillis)
    // [END android_compose_modifier_composed_migration_underline_factory]
}

private object ThemedContainerBorderComposedSnippet {
    // [START android_compose_modifier_composed_migration_composition_local_composed]
    // ❌ BAD: Using Modifier.composed to read a single CompositionLocal
    fun Modifier.themedContainerBorder(): Modifier =
        Modifier.composed {
            Modifier.border(
                BorderStroke(
                    width = 2.dp,
                    color = LocalColorScheme.current.primaryColor,
                )
            )
                .clipToBounds()
        }
    // [END android_compose_modifier_composed_migration_composition_local_composed]
}

private object ThemedContainerBorderComposableSnippet {
    // [START android_compose_modifier_composed_migration_composition_local_composable]
    // ✅ GOOD: If the modifier is @Composable, it should be able to access the locals.
    @Composable
    fun Modifier.themedContainerBorder() =
        this then Modifier.border(
            BorderStroke(
                width = 2.dp,
                color = MyTheme.mainColor,
            )
        )
            .clipToBounds()
    // [END android_compose_modifier_composed_migration_composition_local_composable]
}

private object AdaptivePaddingComposedSnippet {
    // [START android_compose_modifier_composed_migration_composition_local_subsequent_composed]
    // ❌ BAD: Using Modifier.composed to read a CompositionLocal then using it in another modifier.
    fun Modifier.adaptiveAccessibilityPadding(basePadding: Dp): Modifier = Modifier.composed {
        // Reading LocalThemePadding.current.small (CompositionLocal)
        val extraPadding = LocalThemePadding.current.small
        Modifier.padding(basePadding + extraPadding)
    }
    // [END android_compose_modifier_composed_migration_composition_local_subsequent_composed]
}

private object AdaptivePaddingNodeSnippet {
    // [START android_compose_modifier_composed_migration_composition_local_subsequent_node]
    // ✅ GOOD: A custom Modifier that combines the capabilities of both (layout and composition local reader) modifiers.
    fun Modifier.adaptiveAccessibilityPadding(basePadding: Dp): Modifier =
        this.then(AdaptivePaddingElement(basePadding))

    private data class AdaptivePaddingElement(
        val basePadding: Dp,
    ) : ModifierNodeElement<AdaptivePaddingNode>() {
        override fun create() = AdaptivePaddingNode(basePadding)

        override fun update(node: AdaptivePaddingNode) {
            node.basePadding = basePadding
        }

        override fun InspectorInfo.inspectableProperties() {
            name = "adaptiveAccessibilityPadding"
            properties["basePadding"] = basePadding
        }
    }

    private class AdaptivePaddingNode(
        var basePadding: Dp,
    ) : Modifier.Node(), LayoutModifierNode, CompositionLocalConsumerModifierNode {

        override fun MeasureScope.measure(
            measurable: Measurable,
            constraints: Constraints,
        ): MeasureResult {
            val extraPadding = currentValueOf(LocalThemePadding).small
            val total = (basePadding + extraPadding).roundToPx()

            val horizontal = total * 2
            val vertical = total * 2

            val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

            val width = constraints.constrainWidth(placeable.width + horizontal)
            val height = constraints.constrainHeight(placeable.height + vertical)

            return layout(width, height) {
                placeable.place(total, total)
            }
        }
    }
    // [END android_compose_modifier_composed_migration_composition_local_subsequent_node]
}

private object NiceBackgroundComposedSnippet {
    // [START android_compose_modifier_composed_migration_composable_function_composed]
    // ❌ BAD: Using Modifier.composed to access a composable function such as colorResource
    fun Modifier.niceBackground() = composed {
        // Reading composable function colorResource
        val gradientColor1 = colorResource(R.color.my_special_color)
        background(color = gradientColor1, shape = CircleShape)
    }
    // [END android_compose_modifier_composed_migration_composable_function_composed]
}

private object NiceBackgroundComposableSnippet {
    // [START android_compose_modifier_composed_migration_composable_function_composable]
    // ✅ GOOD: A modifier can be annotation with @Composable to reference composable functions.
    @Composable // Modifier can be Composable itself.
    private fun Modifier.niceBackground(): Modifier {
        val gradientColor1 = colorResource(R.color.my_special_color)
        return this then background(color = gradientColor1, shape = CircleShape)
    }
    // [END android_compose_modifier_composed_migration_composable_function_composable]
}

// [START android_compose_modifier_composed_migration_coroutine_scope_composed]
// ❌ BAD: Using Modifier.composed to get access to a coroutine scope.
fun Modifier.onClickAsyncComposed(onClick: suspend () -> Unit): Modifier =
    Modifier.composed {
        val scope = rememberCoroutineScope()
        Modifier.pointerInput(onClick) {
            detectTapGestures {
                // Needs a coroutine scope to launch suspend lambda.
                scope.launch {
                    onClick()
                }
            }
        }
    }
// [END android_compose_modifier_composed_migration_coroutine_scope_composed]

// [START android_compose_modifier_composed_migration_coroutine_scope_node]
// ✅ GOOD: A custom Modifier.Node has a scoped (modifier lifecycle) coroutineScope that can be used to launch async work.
fun Modifier.onClickAsync(onClick: suspend () -> Unit): Modifier =
    this.then(OnClickAsyncElement(onClick))

private data class OnClickAsyncElement(val onClick: suspend () -> Unit) :
    ModifierNodeElement<OnClickAsyncNode>() {
    override fun create(): OnClickAsyncNode = OnClickAsyncNode(onClick)

    override fun update(node: OnClickAsyncNode) {
        node.update(onClick)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "onClickAsync"
        properties["onClick"] = onClick
    }
}

private class OnClickAsyncNode(private var onClick: suspend () -> Unit) :
    DelegatingNode(), PointerInputModifierNode {

    private val pointerInputNode =
        delegate(
            SuspendingPointerInputModifierNode {
                detectTapGestures {
                    // Modifier.Node provides `coroutineScope` directly.
                    coroutineScope.launch { onClick() }
                }
            }
        )

    fun update(onClick: suspend () -> Unit) {
        if (this.onClick != onClick) {
            this.onClick = onClick
            pointerInputNode.resetPointerInputHandler()
        }
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }
}
// [END android_compose_modifier_composed_migration_coroutine_scope_node]

// [START android_compose_modifier_composed_migration_remember_state_composed]
// ❌ BAD: Using Modifier.composed to make the modifier stateful.
fun Modifier.tapCountHighlightComposed(colors: List<Color>): Modifier = Modifier.composed {
    // 1. Must use `remember` so `tapCount` isn't reset to 0 on every recomposition
    var tapCount by remember { mutableIntStateOf(0) }

    Modifier
        .pointerInput(colors) { detectTapGestures { tapCount++ } }
        .drawBehind { drawRect(colors[tapCount % colors.size]) }
}
// [END android_compose_modifier_composed_migration_remember_state_composed]

// [START android_compose_modifier_composed_migration_remember_state_node]
// ✅ GOOD: Modifier.Node is the recommended way of creating stateful modifiers.
fun Modifier.tapCountHighlight(colors: List<Color>): Modifier =
    this then TapCountHighlightElement(colors)

private data class TapCountHighlightElement(
    val colors: List<Color>,
) : ModifierNodeElement<TapCountHighlightNode>() {
    override fun create() = TapCountHighlightNode(colors)

    override fun update(node: TapCountHighlightNode) {
        node.updateColors(colors)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "tapCountHighlight"
        properties["colors"] = colors
    }
}

private class TapCountHighlightNode(
    private var colors: List<Color>,
) : DelegatingNode(), DrawModifierNode {
    private var tapCount = 0 // Stateful modifier, this property will survive recompositions since Modifier.Nodes are held in the modifier tree.

    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures {
                tapCount++
                invalidateDraw()
            }
        }
    )

    override fun ContentDrawScope.draw() {
        drawRect(colors[tapCount % colors.size])
        drawContent()
    }

    fun updateColors(colors: List<Color>) {
        this.colors = colors
        invalidateDraw()
    }
}
// [END android_compose_modifier_composed_migration_remember_state_node]

// [START android_compose_modifier_composed_migration_effect_composed]
// ❌ BAD: Using Modifier.composed to launch/run an effect.
fun Modifier.logImpressionComposed(
    targetId: String,
    onLog: suspend (targetId: String) -> Unit,
): Modifier =
    Modifier.composed {
        // LaunchedEffect is tied to Composition lifecycle
        LaunchedEffect(targetId) { onLog(targetId) }
        this
    }
// [END android_compose_modifier_composed_migration_effect_composed]

// [START android_compose_modifier_composed_migration_effect_node]
// ✅ GOOD: Modifier.Node has lifecycle callbacks (e.g onAttach, onDetach) that can be used to emulate effects behaviors.
fun Modifier.logImpression(targetId: String, onLog: suspend (targetId: String) -> Unit): Modifier =
    this.then(LogImpressionElement(targetId, onLog))

private data class LogImpressionElement(
    val targetId: String,
    val onLog: suspend (targetId: String) -> Unit,
) : ModifierNodeElement<LogImpressionNode>() {
    override fun create(): LogImpressionNode = LogImpressionNode(targetId, onLog)

    override fun update(node: LogImpressionNode) {
        node.update(targetId, onLog)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "logImpression"
        properties["targetId"] = targetId
    }
}

private class LogImpressionNode(
    var targetId: String,
    var onLog: suspend (targetId: String) -> Unit,
) : Modifier.Node() {
    private var job: Job? = null

    override fun onAttach() {
        super.onAttach()
        runEffect() // Uses onAttach to track modifier lifecycle.
    }

    fun update(targetId: String, onLog: suspend (targetId: String) -> Unit) {
        // Re-run the effect if the key (`targetId`) changed
        if (this.targetId != targetId) {
            runEffect()
        }
        this.targetId = targetId
        this.onLog = onLog
    }

    private fun runEffect() {
        job?.cancel()
        job = coroutineScope.launch { onLog(targetId) }
    }
}
// [END android_compose_modifier_composed_migration_effect_node]

// [START android_compose_modifier_composed_migration_animation_composed]
// ❌ BAD: Using Modifier.composed to save an animation state.
fun Modifier.fadeInOnHoverComposed(isHovered: Boolean): Modifier =
    Modifier.composed {
        val alpha by
            animateFloatAsState(
                targetValue = if (isHovered) 1f else 0.4f,
                animationSpec = tween(durationMillis = 300),
                label = "alphaAnimation",
            )

        Modifier.graphicsLayer { this.alpha = alpha }
    }
// [END android_compose_modifier_composed_migration_animation_composed]

// [START android_compose_modifier_composed_migration_animation_node]
// ✅ GOOD: Animation state can be saved in Modifier.Node like other types of stateful implementations.
fun Modifier.fadeInOnHover(isHovered: Boolean): Modifier =
    this.then(FadeInOnHoverElement(isHovered))

private data class FadeInOnHoverElement(val isHovered: Boolean) :
    ModifierNodeElement<FadeInOnHoverNode>() {
    override fun create(): FadeInOnHoverNode = FadeInOnHoverNode(isHovered)

    override fun update(node: FadeInOnHoverNode) {
        node.update(isHovered)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "fadeInOnHover"
        properties["isHovered"] = isHovered
    }
}

private class FadeInOnHoverNode(var isHovered: Boolean) : Modifier.Node(), LayoutModifierNode {
    // 1. Persistent Animatable field on the Node instance
    private val alphaAnimatable = Animatable(if (isHovered) 1f else 0.4f)

    override fun onAttach() {
        super.onAttach()
        startAnimation(isHovered)
    }

    // 2. Trigger animation imperatively when `isHovered` argument changes
    fun update(isHovered: Boolean) {
        if (this.isHovered != isHovered) {
            this.isHovered = isHovered
            if (isAttached) {
                startAnimation(isHovered)
            }
        }
    }

    private fun startAnimation(hovered: Boolean) {
        val targetAlpha = if (hovered) 1f else 0.4f
        // Use Node's built-in coroutineScope
        coroutineScope.launch {
            alphaAnimatable.animateTo(
                targetValue = targetAlpha,
                animationSpec = tween(durationMillis = 300),
            )
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            // Read current animation value during layout placement layer
            placeable.placeWithLayer(0, 0) { alpha = alphaAnimatable.value }
        }
    }
}
// [END android_compose_modifier_composed_migration_animation_node]
