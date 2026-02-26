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

package com.example.compose.snippets.styles

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.fillSize
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// [START android_compose_styles_examples_hover_button]
@Preview
@Composable
fun HoverOutlinedButton() {
    BaseButton(
        onClick = {},
        style = Style {
            background(Color.Transparent)
            shape(RoundedCornerShape(0.dp))
            border(1.dp, Color.Black)
            contentColor(Color.Black)
            fontSize(16.sp)
            fontWeight(FontWeight.Light)
            letterSpacing(1.sp)
            contentPadding(vertical = 13.dp, horizontal = 20.dp)
            dropShadow(
                Shadow(
                    spread = 0.dp, color = Color(0xFFFFE54C),
                    radius = 0.dp,
                    offset = DpOffset(7.dp, 7.dp)
                )
            )
            hovered {
                animate(tween(200)) {
                    dropShadow(
                        Shadow(
                            spread = 0.dp, color = Color(0xFFFFE54C),
                            radius = 0.dp,
                            offset = DpOffset(0.dp, 0.dp)
                        )
                    )
                }
            }
        }
    ) {
        Text("Button 52")
    }
}
// [END android_compose_styles_examples_hover_button]

// [START android_compose_styles_examples_rounded_depth]
@Preview
@Composable
fun RoundedDepthEffectButton() {
    val density = LocalDensity.current
    val buttonStyle = Style {
        background(Color(0xFFFBEED0))
        border(2.dp, Color(0xFF422800))
        shape(RoundedCornerShape(30.dp))
        dropShadow(
            Shadow(
                color = Color(0xFF422800), offset = DpOffset(4.dp, 4.dp),
                radius = 0.dp, spread = 0.dp
            )
        )
        contentColor(Color(0xFF422800))
        fontWeight(FontWeight.SemiBold)
        fontSize(18.sp)
        contentPaddingHorizontal(25.dp)
        externalPadding(8.dp)
        height(50.dp)
        textAlign(TextAlign.Center)
        hovered {
            animate {
                background(Color.White)
            }
        }
        pressed {
            animate {
                dropShadow(
                    Shadow(
                        color = Color(0xFF422800),
                        offset = DpOffset(2.dp, 2.dp),
                        radius = 0.dp,
                        spread = 0.dp
                    )
                )
                translation(with(density) { 2.dp.toPx() }, with(density) { 2.dp.toPx() })
            }
        }
    }
    BaseButton(
        onClick = {},
        style = buttonStyle
    ) {
        Text("Button 74")
    }
}
// [END android_compose_styles_examples_rounded_depth]

// [START android_compose_styles_examples_depth_pressed]
@Preview
@Composable
fun DepthPressedEffectButton() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    val density = LocalDensity.current


    val edgeStyle = Style {
        fillSize()
        shape(RoundedCornerShape(16.dp))
        background(Color(0xFF1CB0F6))
    }

    val frontStyle = Style {
        fillSize()
        background(Color(0xFF1899D6))
        shape(RoundedCornerShape(16.dp))
        contentPadding(vertical = 13.dp, horizontal = 16.dp)
        translationY(with(density) { (-4).dp.toPx() })
        pressed {
            animate {
                translationY(with(density) { (0).dp.toPx() })
            }
        }
    }
    Box(modifier = Modifier.styleable(styleState, edgeStyle)) {
        Box(
            modifier = Modifier
                .styleable(styleState, frontStyle),
            contentAlignment = Alignment.Center
        ) {
            BaseText(
                "Button 19".toUpperCase(Locale.current),
                style = Style {
                    contentColor(Color.White)
                    fontSize(15.sp)
                    fontWeight(FontWeight.Bold)
                    letterSpacing(0.8.sp)
                }
            )
        }
    }
}
// [END android_compose_styles_examples_depth_pressed]

// [START android_compose_styles_examples_gradient_glow]
@Preview
@Composable
fun GlowingGradientButtonExample() {
    val infiniteTransition = rememberInfiniteTransition(label = "glowing_button_85_animation")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
        ), label = "progress"
    )

    val gradientColors = listOf(
        Color(0xffff0000), Color(0xffff7300), Color(0xfffffb00), Color(0xff48ff00),
        Color(0xff00ffd5), Color(0xff002bff), Color(0xff7a00ff), Color(0xffff00c8),
        Color(0xffff0000)
    )

    val glowingBrush = remember(animatedProgress) {
        object : ShaderBrush() {
            override fun createShader(size: androidx.compose.ui.geometry.Size): androidx.compose.ui.graphics.Shader {
                val width = size.width * 4
                val brushSize = width * animatedProgress
                return androidx.compose.ui.graphics.LinearGradientShader(
                    colors = gradientColors,
                    from = androidx.compose.ui.geometry.Offset(brushSize, 0f),
                    to = androidx.compose.ui.geometry.Offset(brushSize + width, 0f),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
    BaseButton(
        onClick = { },
        style = Style {
            dropShadow(
                Shadow(
                    brush = glowingBrush,
                    radius = 5.dp
                )
            )
            transformOrigin(TransformOrigin.Center)
            pressed {
                animate {
                    dropShadow(
                        Shadow(
                            brush = glowingBrush,
                            radius = 10.dp
                        )
                    )
                    scale(0.95f)
                }

            }
            size(width = 200.dp, height = 50.dp)
            background(Color(0xFF111111))
            shape(RoundedCornerShape(10.dp))
            contentColor(Color.White)
            contentPadding(vertical = (0.6f * 14).dp, horizontal = (2f * 14).dp)
            border(width = 0.dp, color = Color.Transparent)
        }
    ) {
        Text(
            text = "Button 85",
        )
    }
}
// [END android_compose_styles_examples_gradient_glow]
