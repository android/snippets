/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.touchinput

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.ripple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
private fun InteractionsSnippet1() {
    // [START android_compose_interactions_interaction_state]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = { /* do something */ },
        interactionSource = interactionSource
    ) {
        Text(if (isPressed) "Pressed!" else "Not pressed")
    }
    // [END android_compose_interactions_interaction_state]
}

// [START android_compose_interactions_interaction_source_input]
fun Modifier.focusBorder(interactionSource: InteractionSource): Modifier {
    // [START_EXCLUDE]
    return this
    // [END_EXCLUDE]
}
// [END android_compose_interactions_interaction_source_input]

// [START android_compose_interactions_mutable_interaction_source_input]
fun Modifier.hover(interactionSource: MutableInteractionSource, enabled: Boolean): Modifier {
    // [START_EXCLUDE]
    return this
    // [END_EXCLUDE]
}
// [END android_compose_interactions_mutable_interaction_source_input]

// [START android_compose_interactions_high_level_component]
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,

    // exposes MutableInteractionSource as a parameter
    interactionSource: MutableInteractionSource? = null,

    elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) { /* content() */ }
// [END android_compose_interactions_high_level_component]

@Composable
fun HoverExample() {
    // [START android_compose_interactions_hoverable]
    // This InteractionSource will emit hover interactions
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .size(100.dp)
            .hoverable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello!")
    }
    // [END android_compose_interactions_hoverable]
}

@Composable
fun FocusableExample() {
    // [START android_compose_interactions_focusable]
    // This InteractionSource will emit hover and focus interactions
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .size(100.dp)
            .hoverable(interactionSource = interactionSource)
            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello!")
    }
    // [END android_compose_interactions_focusable]
}

@Composable
fun ClickableExample() {
    // [START android_compose_interactions_clickable]
    // This InteractionSource will emit hover, focus, and press interactions
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .size(100.dp)
            .clickable(
                onClick = {},
                interactionSource = interactionSource,

                // Also show a ripple effect
                indication = ripple()
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello!")
    }
    // [END android_compose_interactions_clickable]
}

@Composable
private fun InteractionsSnippet2() {
    // [START android_compose_interactions_flow_apis]
    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }
                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }
            }
        }
    }
    // [END android_compose_interactions_flow_apis]
}

@Composable
private fun InteractionsSnippet3() {
    // [START android_compose_interactions_add_remove]
    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }
                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                }
                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }
                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }
                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                }
                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                }
            }
        }
    }
    // [END android_compose_interactions_add_remove]

    // [START android_compose_interactions_is_pressed_or_dragged]
    val isPressedOrDragged = interactions.isNotEmpty()
    // [END android_compose_interactions_is_pressed_or_dragged]

    // [START android_compose_interactions_last]
    val lastInteraction = when (interactions.lastOrNull()) {
        is DragInteraction.Start -> "Dragged"
        is PressInteraction.Press -> "Pressed"
        else -> "No state"
    }
    // [END android_compose_interactions_last]
}

@Composable
private fun InteractionsSnippet4() {
    // [START android_compose_interactions_batched]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(onClick = { /* do something */ }, interactionSource = interactionSource) {
        Text(if (isPressed) "Pressed!" else "Not pressed")
    }
    // [END android_compose_interactions_batched]
}

// [START android_compose_interactions_press_icon_button]
@Composable
fun PressIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) {
    val isPressed = interactionSource?.collectIsPressedAsState()?.value ?: false

    Button(
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource
    ) {
        AnimatedVisibility(visible = isPressed) {
            if (isPressed) {
                Row {
                    icon()
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
            }
        }
        text()
    }
}
// [END android_compose_interactions_press_icon_button]

@Composable
fun PressIconButtonUsage() {
// [START android_compose_interactions_press_icon_button_usage]
    PressIconButton(
        onClick = {},
        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
        text = { Text("Add to cart") }
    )
// [END android_compose_interactions_press_icon_button_usage]
}

@Composable
fun InteractionsSnippet5() {
// [START android_compose_interactions_indication]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f, label = "scale")

    Button(
        modifier = Modifier.scale(scale),
        onClick = { },
        interactionSource = interactionSource
    ) {
        Text(if (isPressed) "Pressed!" else "Not pressed")
    }
// [END android_compose_interactions_indication]
}

// [START android_compose_interactions_scale_node]
private class ScaleNode(private val interactionSource: InteractionSource) :
    Modifier.Node(), DrawModifierNode {

    var currentPressPosition: Offset = Offset.Zero
    val animatedScalePercent = Animatable(1f)

    private suspend fun animateToPressed(pressPosition: Offset) {
        currentPressPosition = pressPosition
        animatedScalePercent.animateTo(0.9f, spring())
    }

    private suspend fun animateToResting() {
        animatedScalePercent.animateTo(1f, spring())
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(
            scale = animatedScalePercent.value,
            pivot = currentPressPosition
        ) {
            this@draw.drawContent()
        }
    }
}
// [END android_compose_interactions_scale_node]

// [START android_compose_interactions_scale_node_factory]
object ScaleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ScaleNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other === ScaleIndication
    override fun hashCode() = 100
}
// [END android_compose_interactions_scale_node_factory]

@Composable
fun InteractionSnippets6() {
// [START android_compose_interactions_button_indication]
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(
                onClick = {},
                indication = ScaleIndication,
                interactionSource = null
            )
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text("Hello!", color = Color.White)
    }
// [END android_compose_interactions_button_indication]
}

// [START android_compose_interactions_scale_button]
@Composable
fun ScaleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = CircleShape,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 76.dp, minHeight = 48.dp)
            .clickable(
                enabled = enabled,
                indication = ScaleIndication,
                interactionSource = interactionSource,
                onClick = onClick
            )
            .border(width = 2.dp, color = Color.Blue, shape = shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
// [END android_compose_interactions_scale_button]

@Composable
fun ScaleButtonExample() {
// [START android_compose_interactions_scale_button_example]
    ScaleButton(onClick = {}) {
        Icon(Icons.Filled.ShoppingCart, "")
        Spacer(Modifier.padding(10.dp))
        Text(text = "Add to cart!")
    }
// [END android_compose_interactions_scale_button_example]
}

// [START android_compose_interactions_neon_node]
private class NeonNode(
    private val shape: Shape,
    private val borderWidth: Dp,
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    var currentPressPosition: Offset = Offset.Zero
    val animatedProgress = Animatable(0f)
    val animatedPressAlpha = Animatable(1f)

    var pressedAnimation: Job? = null
    var restingAnimation: Job? = null

    private suspend fun animateToPressed(pressPosition: Offset) {
        // Finish any existing animations, in case of a new press while we are still showing
        // an animation for a previous one
        restingAnimation?.cancel()
        pressedAnimation?.cancel()
        pressedAnimation = coroutineScope.launch {
            currentPressPosition = pressPosition
            animatedPressAlpha.snapTo(1f)
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(1f, tween(450))
        }
    }

    private fun animateToResting() {
        restingAnimation = coroutineScope.launch {
            // Wait for the existing press animation to finish if it is still ongoing
            pressedAnimation?.join()
            animatedPressAlpha.animateTo(0f, tween(250))
            animatedProgress.snapTo(0f)
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val (startPosition, endPosition) = calculateGradientStartAndEndFromPressPosition(
            currentPressPosition, size
        )
        val brush = animateBrush(
            startPosition = startPosition,
            endPosition = endPosition,
            progress = animatedProgress.value
        )
        val alpha = animatedPressAlpha.value

        drawContent()

        val outline = shape.createOutline(size, layoutDirection, this)
        // Draw overlay on top of content
        drawOutline(
            outline = outline,
            brush = brush,
            alpha = alpha * 0.1f
        )
        // Draw border on top of overlay
        drawOutline(
            outline = outline,
            brush = brush,
            alpha = alpha,
            style = Stroke(width = borderWidth.toPx())
        )
    }

    /**
     * Calculates a gradient start / end where start is the point on the bounding rectangle of
     * size [size] that intercepts with the line drawn from the center to [pressPosition],
     * and end is the intercept on the opposite end of that line.
     */
    private fun calculateGradientStartAndEndFromPressPosition(
        pressPosition: Offset,
        size: Size
    ): Pair<Offset, Offset> {
        // Convert to offset from the center
        val offset = pressPosition - size.center
        // y = mx + c, c is 0, so just test for x and y to see where the intercept is
        val gradient = offset.y / offset.x
        // We are starting from the center, so halve the width and height - convert the sign
        // to match the offset
        val width = (size.width / 2f) * sign(offset.x)
        val height = (size.height / 2f) * sign(offset.y)
        val x = height / gradient
        val y = gradient * width

        // Figure out which intercept lies within bounds
        val intercept = if (abs(y) <= abs(height)) {
            Offset(width, y)
        } else {
            Offset(x, height)
        }

        // Convert back to offsets from 0,0
        val start = intercept + size.center
        val end = Offset(size.width - start.x, size.height - start.y)
        return start to end
    }

    private fun animateBrush(
        startPosition: Offset,
        endPosition: Offset,
        progress: Float
    ): Brush {
        if (progress == 0f) return TransparentBrush

        // This is *expensive* - we are doing a lot of allocations on each animation frame. To
        // recreate a similar effect in a performant way, it would be better to create one large
        // gradient and translate it on each frame, instead of creating a whole new gradient
        // and shader. The current approach will be janky!
        val colorStops = buildList {
            when {
                progress < 1 / 6f -> {
                    val adjustedProgress = progress * 6f
                    add(0f to Blue)
                    add(adjustedProgress to Color.Transparent)
                }
                progress < 2 / 6f -> {
                    val adjustedProgress = (progress - 1 / 6f) * 6f
                    add(0f to Purple)
                    add(adjustedProgress * MaxBlueStop to Blue)
                    add(adjustedProgress to Blue)
                    add(1f to Color.Transparent)
                }
                progress < 3 / 6f -> {
                    val adjustedProgress = (progress - 2 / 6f) * 6f
                    add(0f to Pink)
                    add(adjustedProgress * MaxPurpleStop to Purple)
                    add(MaxBlueStop to Blue)
                    add(1f to Blue)
                }
                progress < 4 / 6f -> {
                    val adjustedProgress = (progress - 3 / 6f) * 6f
                    add(0f to Orange)
                    add(adjustedProgress * MaxPinkStop to Pink)
                    add(MaxPurpleStop to Purple)
                    add(MaxBlueStop to Blue)
                    add(1f to Blue)
                }
                progress < 5 / 6f -> {
                    val adjustedProgress = (progress - 4 / 6f) * 6f
                    add(0f to Yellow)
                    add(adjustedProgress * MaxOrangeStop to Orange)
                    add(MaxPinkStop to Pink)
                    add(MaxPurpleStop to Purple)
                    add(MaxBlueStop to Blue)
                    add(1f to Blue)
                }
                else -> {
                    val adjustedProgress = (progress - 5 / 6f) * 6f
                    add(0f to Yellow)
                    add(adjustedProgress * MaxYellowStop to Yellow)
                    add(MaxOrangeStop to Orange)
                    add(MaxPinkStop to Pink)
                    add(MaxPurpleStop to Purple)
                    add(MaxBlueStop to Blue)
                    add(1f to Blue)
                }
            }
        }

        return linearGradient(
            colorStops = colorStops.toTypedArray(),
            start = startPosition,
            end = endPosition
        )
    }

    companion object {
        val TransparentBrush = SolidColor(Color.Transparent)
        val Blue = Color(0xFF30C0D8)
        val Purple = Color(0xFF7848A8)
        val Pink = Color(0xFFF03078)
        val Orange = Color(0xFFF07800)
        val Yellow = Color(0xFFF0D800)
        const val MaxYellowStop = 0.16f
        const val MaxOrangeStop = 0.33f
        const val MaxPinkStop = 0.5f
        const val MaxPurpleStop = 0.67f
        const val MaxBlueStop = 0.83f
    }
}
// [END android_compose_interactions_neon_node]

// [START android_compose_interactions_neon_indication]
data class NeonIndication(private val shape: Shape, private val borderWidth: Dp) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return NeonNode(
            shape,
            // Double the border size for a stronger press effect
            borderWidth * 2,
            interactionSource
        )
    }
}
// [END android_compose_interactions_neon_indication]
