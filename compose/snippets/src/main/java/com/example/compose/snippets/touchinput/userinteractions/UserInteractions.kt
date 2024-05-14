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

@file:Suppress("DEPRECATION_ERROR")

package com.example.compose.snippets.touchinput.userinteractions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.LocalUseFallbackRippleImplementation
import androidx.compose.material.RippleConfiguration
import androidx.compose.material.ripple
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import com.example.compose.snippets.architecture.Button
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// [START android_compose_userinteractions_scale_indication]
// [START android_compose_userinteractions_scale_indication_object]
object ScaleIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        // key the remember against interactionSource, so if it changes we create a new instance
        val instance = remember(interactionSource) { ScaleIndicationInstance() }

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> instance.animateToPressed(interaction.pressPosition)
                    is PressInteraction.Release -> instance.animateToResting()
                    is PressInteraction.Cancel -> instance.animateToResting()
                }
            }
        }

        return instance
    }
}
// [END android_compose_userinteractions_scale_indication_object]

// [START android_compose_userinteractions_scale_indication_instance]
private class ScaleIndicationInstance : IndicationInstance {
    var currentPressPosition: Offset = Offset.Zero
    val animatedScalePercent = Animatable(1f)

    suspend fun animateToPressed(pressPosition: Offset) {
        currentPressPosition = pressPosition
        animatedScalePercent.animateTo(0.9f, spring())
    }

    suspend fun animateToResting() {
        animatedScalePercent.animateTo(1f, spring())
    }

    override fun ContentDrawScope.drawIndication() {
        scale(
            scale = animatedScalePercent.value,
            pivot = currentPressPosition
        ) {
            this@drawIndication.drawContent()
        }
    }
}
// [END android_compose_userinteractions_scale_indication_instance]
// [END android_compose_userinteractions_scale_indication]

@Composable
private fun RememberRippleExample() {
    // [START android_compose_userinteractions_material_remember_ripple]
    Box(
        Modifier.clickable(
            onClick = {},
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        )
    ) {
        // ...
    }
    // [END android_compose_userinteractions_material_remember_ripple]
}

// [START android_compose_userinteractions_material_ripple]
@Composable
private fun RippleExample() {
    Box(
        Modifier.clickable(
            onClick = {},
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple()
        )
    ) {
        // ...
    }
}
// [END android_compose_userinteractions_material_ripple]

// [START android_compose_userinteractions_disabled_ripple_theme]
private object DisabledRippleTheme : RippleTheme {

    @Composable
    override fun defaultColor(): Color = Color.Transparent

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
}

// [START_EXCLUDE]
@Composable
private fun MyComposable() {
// [END_EXCLUDE]
    CompositionLocalProvider(LocalRippleTheme provides DisabledRippleTheme) {
        Button {
            // ...
        }
    }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE]
// [END android_compose_userinteractions_disabled_ripple_theme]

private val MyRippleAlpha = RippleAlpha(0.5f, 0.5f, 0.5f, 0.5f)

// [START android_compose_userinteractions_disabled_ripple_theme_color_alpha]
private object DisabledRippleThemeColorAndAlpha : RippleTheme {

    @Composable
    override fun defaultColor(): Color = Color.Red

    @Composable
    override fun rippleAlpha(): RippleAlpha = MyRippleAlpha
}

// [START_EXCLUDE]
@Composable
private fun MyComposable2() {
// [END_EXCLUDE]
    CompositionLocalProvider(LocalRippleTheme provides DisabledRippleThemeColorAndAlpha) {
        Button {
            // ...
        }
    }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE]
// [END android_compose_userinteractions_disabled_ripple_theme_color_alpha]

// Snippets for new ripple API

// [START android_compose_userinteractions_scale_indication_node_factory]
object ScaleIndicationNodeFactory : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ScaleIndicationNode(interactionSource)
    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}
// [END android_compose_userinteractions_scale_indication_node_factory]

// [START android_compose_userinteractions_scale_indication_node]
private class ScaleIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
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
// [END android_compose_userinteractions_scale_indication_node]

@Composable
fun App() {
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LocalUseFallbackRippleImplementationExample() {
// [START android_compose_userinteractions_localusefallbackrippleimplementation]
    CompositionLocalProvider(LocalUseFallbackRippleImplementation provides true) {
        MaterialTheme {
            App()
        }
    }
// [END android_compose_userinteractions_localusefallbackrippleimplementation]
}

// [START android_compose_userinteractions_localusefallbackrippleimplementation_app_theme]
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalUseFallbackRippleImplementation provides true) {
        MaterialTheme(content = content)
    }
}
// [END android_compose_userinteractions_localusefallbackrippleimplementation_app_theme]

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyComposableDisabledRippleConfig() {
    // [START android_compose_userinteractions_disabled_ripple_configuration]
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        Button {
            // ...
        }
    }
    // [END android_compose_userinteractions_disabled_ripple_configuration]
}

// [START android_compose_userinteractions_my_ripple_configuration]
@OptIn(ExperimentalMaterialApi::class)
private val MyRippleConfiguration =
    RippleConfiguration(color = Color.Red, rippleAlpha = MyRippleAlpha)

// [START_EXCLUDE]
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyComposableMyRippleConfig() {
// [END_EXCLUDE]
    CompositionLocalProvider(LocalRippleConfiguration provides MyRippleConfiguration) {
        Button {
            // ...
        }
    }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE]
// [END android_compose_userinteractions_my_ripple_configuration]
