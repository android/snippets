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

package com.example.compose.snippets.modifiers

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import kotlinx.coroutines.launch

private object ClipModifierExample {
    @SuppressLint("ModifierFactoryUnreferencedReceiver") // graphics layer does the reference
    // [START android_compose_custom_modifiers_1]
    fun Modifier.clip(shape: Shape) = graphicsLayer(shape = shape, clip = true)
    // [END android_compose_custom_modifiers_1]
}

// [START android_compose_custom_modifiers_2]
fun Modifier.myBackground(color: Color) = padding(16.dp)
    .clip(RoundedCornerShape(8.dp))
    .background(color)
// [END android_compose_custom_modifiers_2]

// [START android_compose_custom_modifiers_3]
@Composable
fun Modifier.fade(enable: Boolean): Modifier {
    val alpha by animateFloatAsState(if (enable) 0.5f else 1.0f)
    return this then Modifier.graphicsLayer { this.alpha = alpha }
}
// [END android_compose_custom_modifiers_3]

// [START android_compose_custom_modifiers_4]
@Composable
fun Modifier.fadedBackground(): Modifier {
    val color = LocalContentColor.current
    return this then Modifier.background(color.copy(alpha = 0.5f))
}
// [END android_compose_custom_modifiers_4]

private object CustomModifierSnippets5 {
    // [START android_compose_custom_modifiers_5]
    @Composable
    fun Modifier.myBackground(): Modifier {
        val color = LocalContentColor.current
        return this then Modifier.background(color.copy(alpha = 0.5f))
    }

    @Composable
    fun MyScreen() {
        CompositionLocalProvider(LocalContentColor provides Color.Green) {
            // Background modifier created with green background
            val backgroundModifier = Modifier.myBackground()

            // LocalContentColor updated to red
            CompositionLocalProvider(LocalContentColor provides Color.Red) {

                // Box will have green background, not red as expected.
                Box(modifier = backgroundModifier)
            }
        }
    }
    // [END android_compose_custom_modifiers_5]
}

// [START android_compose_custom_modifiers_6]
val extractedModifier = Modifier.background(Color.Red) // Hoisted to save allocations

@Composable
fun Modifier.composableModifier(): Modifier {
    val color = LocalContentColor.current.copy(alpha = 0.5f)
    return this then Modifier.background(color)
}

@Composable
fun MyComposable() {
    val composedModifier = Modifier.composableModifier() // Cannot be extracted any higher
}
// [END android_compose_custom_modifiers_6]

// [START android_compose_custom_modifiers_7]
// Modifier.Node
private class CircleNode(var color: Color) : DrawModifierNode, Modifier.Node() {
    override fun ContentDrawScope.draw() {
        drawCircle(color)
    }
}
// [END android_compose_custom_modifiers_7]

// [START android_compose_custom_modifiers_8]
// ModifierNodeElement
private data class CircleElement(val color: Color) : ModifierNodeElement<CircleNode>() {
    override fun create() = CircleNode(color)

    override fun update(node: CircleNode) {
        node.color = color
    }
}
// [END android_compose_custom_modifiers_8]

// [START android_compose_custom_modifiers_9]
// Modifier factory
fun Modifier.circle(color: Color) = this then CircleElement(color)
// [END android_compose_custom_modifiers_9]

private object CustomModifierSnippets10 {
    // [START android_compose_custom_modifiers_10]
    // Modifier factory
    fun Modifier.circle(color: Color) = this then CircleElement(color)

    // ModifierNodeElement
    private data class CircleElement(val color: Color) : ModifierNodeElement<CircleNode>() {
        override fun create() = CircleNode(color)

        override fun update(node: CircleNode) {
            node.color = color
        }
    }

    // Modifier.Node
    private class CircleNode(var color: Color) : DrawModifierNode, Modifier.Node() {
        override fun ContentDrawScope.draw() {
            drawCircle(color)
        }
    }
    // [END android_compose_custom_modifiers_10]
}

// [START android_compose_custom_modifiers_11]
fun Modifier.fixedPadding() = this then FixedPaddingElement

data object FixedPaddingElement : ModifierNodeElement<FixedPaddingNode>() {
    override fun create() = FixedPaddingNode()
    override fun update(node: FixedPaddingNode) {}
}

class FixedPaddingNode : LayoutModifierNode, Modifier.Node() {
    private val PADDING = 16.dp

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val paddingPx = PADDING.roundToPx()
        val horizontal = paddingPx * 2
        val vertical = paddingPx * 2

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            placeable.place(paddingPx, paddingPx)
        }
    }
}
// [END android_compose_custom_modifiers_11]

// [START android_compose_custom_modifiers_12]
class BackgroundColorConsumerNode :
    Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    override fun ContentDrawScope.draw() {
        val currentColor = currentValueOf(LocalContentColor)
        drawRect(color = currentColor)
        drawContent()
    }
}
// [END android_compose_custom_modifiers_12]

private object UnityDensity : Density {
    override val density: Float
        get() = 1f
    override val fontScale: Float
        get() = 1f
}
data class DefaultFlingBehavior(var flingDecay: DecayAnimationSpec<Density>)
// [START android_compose_custom_modifiers_13]
class ScrollableNode :
    Modifier.Node(),
    ObserverModifierNode,
    CompositionLocalConsumerModifierNode {

    // Place holder fling behavior, we'll initialize it when the density is available.
    val defaultFlingBehavior = DefaultFlingBehavior(splineBasedDecay(UnityDensity))

    override fun onAttach() {
        updateDefaultFlingBehavior()
        observeReads { currentValueOf(LocalDensity) } // monitor change in Density
    }

    override fun onObservedReadsChanged() {
        // if density changes, update the default fling behavior.
        updateDefaultFlingBehavior()
    }

    private fun updateDefaultFlingBehavior() {
        val density = currentValueOf(LocalDensity)
        defaultFlingBehavior.flingDecay = splineBasedDecay(density)
    }
}
// [END android_compose_custom_modifiers_13]

object CustomModifierSnippets14 {
    // [START android_compose_custom_modifiers_14]
    class CircleNode(var color: Color) : Modifier.Node(), DrawModifierNode {
        private val alpha = Animatable(1f)

        override fun ContentDrawScope.draw() {
            drawCircle(color = color, alpha = alpha.value)
            drawContent()
        }

        override fun onAttach() {
            coroutineScope.launch {
                alpha.animateTo(
                    0f,
                    infiniteRepeatable(tween(1000), RepeatMode.Reverse)
                ) {
                }
            }
        }
    }
    // [END android_compose_custom_modifiers_14]
}

class InteractionData
class FocusableNode(val interactionData: InteractionData) : DelegatableNode {
    override val node: Modifier.Node
        get() = TODO("Not yet implemented")
}
class IndicationNode(val interactionData: InteractionData) : DelegatableNode {
    override val node: Modifier.Node
        get() = TODO("Not yet implemented")
}
// [START android_compose_custom_modifiers_15]
class ClickableNode : DelegatingNode() {
    val interactionData = InteractionData()
    val focusableNode = delegate(
        FocusableNode(interactionData)
    )
    val indicationNode = delegate(
        IndicationNode(interactionData)
    )
}
// [END android_compose_custom_modifiers_15]

class ClickablePointerInputNode(var onClick: () -> Unit) : Modifier.Node(), DelegatableNode {
    fun update(onClick: () -> Unit) {
        this.onClick = onClick
    }
}
// [START android_compose_custom_modifiers_16]
class SampleInvalidatingNode(
    var color: Color,
    var size: IntSize,
    var onClick: () -> Unit
) : DelegatingNode(), LayoutModifierNode, DrawModifierNode {
    override val shouldAutoInvalidate: Boolean
        get() = false

    private val clickableNode = delegate(
        ClickablePointerInputNode(onClick)
    )

    fun update(color: Color, size: IntSize, onClick: () -> Unit) {
        if (this.color != color) {
            this.color = color
            // Only invalidate draw when color changes
            invalidateDraw()
        }

        if (this.size != size) {
            this.size = size
            // Only invalidate layout when size changes
            invalidateMeasurement()
        }

        // If only onClick changes, we don't need to invalidate anything
        clickableNode.update(onClick)
    }

    override fun ContentDrawScope.draw() {
        drawRect(color)
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val size = constraints.constrain(size)
        val placeable = measurable.measure(constraints)
        return layout(size.width, size.height) {
            placeable.place(0, 0)
        }
    }
}
// [END android_compose_custom_modifiers_16]
