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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.designsystems.styles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.designsystems.styles.components.BaseButton
import com.example.compose.snippets.designsystems.styles.components.BaseText

val outlinedButtonStyle = Style {
    externalPadding(48.dp)
    contentPadding(12.dp)
    shape(RoundedCornerShape(8.dp))
    clip(true)
    border(2.dp, lightBlue)
}

val lightBlue = Color(0xFF03A9F4)
val lightPurple = Color(0xFF9575CD)
val lightOrange = Color(0xFFFFE0B2)
val lightRed = Color(0xFFE57373)

// [START android_compose_styles_state_basic]
@Preview
@Composable
private fun OpenButton() {
    BaseButton(
        style = outlinedButtonStyle then {
            background(Color.White)
            hovered {
                background(lightPurple)
                border(2.dp, lightPurple)
            }
            focused {
                background(lightBlue)
            }
        },
        onClick = {  },
        content = {
            BaseText("Open in Studio", style = {
                contentColor(Color.Black)
                fontSize(26.sp)
                textAlign(TextAlign.Center)
            })
        }
    )
}

// [END android_compose_styles_state_basic]

@Preview
// [START android_compose_styles_state_basic_combined_states]
@Composable
private fun OpenButton_CombinedStates() {
    BaseButton(
        style = outlinedButtonStyle then {
            background(Color.White)
            hovered {
                // light purple
                background(lightPurple)
                pressed {
                    // When running on a device that can hover, whilst hovering and then pressing the button this would be invoked
                    background(lightOrange)
                }
            }
            pressed {
                // when running on a device without a mouse attached, this would be invoked as you wouldn't be in a hovered state only
                background(lightRed)
            }
            focused {
                background(lightBlue)
            }
        },
        onClick = {  },
        content = {
            BaseText("Open in Studio", style = {
                contentColor(Color.Black)
                fontSize(26.sp)
                textAlign(TextAlign.Center)
            })
        }
    )
}
// [END android_compose_styles_state_basic_combined_states]

val baseGradientButtonStyle = Style {
    contentPadding(16.dp)
    externalPadding(64.dp)
    fontSize(16.sp)
    shape(RoundedCornerShape(16.dp))
    clip(true)
    background(Brush.linearGradient(listOf(lightPurple, lightBlue)))
}

// [START android_compose_styles_state_custom_state_gradient]
@Composable
private fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }
    Row(
        modifier =
            modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, baseGradientButtonStyle then style),
        content = content,
    )
}
// [END android_compose_styles_state_custom_state_gradient]

// [START android_compose_styles_state_override]
@Preview
@Composable
fun LoginButton() {
    val loginButtonStyle = Style {
        pressed {
            background(
                Brush.linearGradient(
                    listOf(Color.Magenta, Color.Red)
                )
            )
        }
    }
    GradientButton(onClick = {
        // Login logic
    }, style = loginButtonStyle) {
        BaseText("Login")
    }
}
// [END android_compose_styles_state_override]

// [START android_compose_styles_animate_style_basic]
val animatingStyle = Style {
    externalPadding(48.dp)
    border(3.dp, Color.Black)
    background(Color.White)
    size(100.dp)

    pressed {
        animate {
            borderColor(Color.Magenta)
            background(Color(0xFFB39DDB))
        }
    }
}

@Preview
@Composable
private fun AnimatingStyleChanges() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    Box(modifier = Modifier
        .clickable(
            interactionSource,
            enabled = true,
            indication = null,
            onClick = {

            }
        )
        .styleable(styleState, animatingStyle)) {

    }
}
// [END android_compose_styles_animate_style_basic]

// [START android_compose_styles_animate_style_set_spec]
val animatingStyleSpec = Style {
    externalPadding(48.dp)
    border(3.dp, Color.Black)
    background(Color.White)
    size(100.dp)
    transformOrigin(TransformOrigin.Center)
    pressed {
        animate {
            borderColor(Color.Magenta)
            background(Color(0xFFB39DDB))
        }
        animate(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) {
            scale(1.2f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatingStyleChangesSpec() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    Box(modifier = Modifier
        .clickable(
            interactionSource,
            enabled = true,
            indication = null,
            onClick = {

            }
        )
        .styleable(styleState, animatingStyleSpec))
}
// [END android_compose_styles_animate_style_set_spec]
