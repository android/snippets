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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.designsystems.styles.CustomThemingWithStyles.JetsnackTheme.Companion.LocalJetsnackTheme


object ThemingStyles {
    // [START android_compose_styles_theming_atomic_styles ]

    // Define single-purpose "atomic" styles
    val paddingAtomic = Style {
        contentPadding(16.dp)
    }
    val roundedCornerShapeAtomic = Style {
        shape(RoundedCornerShape(8.dp))
    }
    val primaryBackgroundAtomic = Style {
        background(Color.Blue)
    }
    val largeSizeAtomic = Style {
        size(100.dp, 40.dp)
    }
    val interactiveShadowAtomic = Style {
        hovered {
            animate {
                dropShadow(
                    Shadow(
                        offset = DpOffset(
                            0.dp,
                            0.dp
                        ),
                        radius = 2.dp,
                        spread = 0.dp,
                        color = Color.Blue,
                    )
                )
            }
        }
    }

    // [END android_compose_styles_theming_atomic_styles ]

    // [START android_compose_styles_theming_traditional_non_atomic]
    // One large monolithic style
    val buttonStyle = Style {
        contentPadding(16.dp)
        shape(RoundedCornerShape(8.dp))
        background(Color.Blue)
    }
    // [END android_compose_styles_theming_traditional_non_atomic]

    object AtomicThenExample {
        // [START android_compose_styles_theming_traditional_atomic_then]
        // Combine atoms to create the final appearance
        val buttonStyle = paddingAtomic then roundedCornerShapeAtomic then primaryBackgroundAtomic then interactiveShadowAtomic
        // [END android_compose_styles_theming_traditional_atomic_then]
    }
}

object CustomThemingWithStyles {
    // [START android_compose_styles_default_object ]
    object JetsnackStyles{
        val buttonStyle: Style = Style {
            shape(shapes.medium)
            background(colors.brand)
            contentColor(colors.textPrimary)
            contentPaddingVertical(8.dp)
            contentPaddingHorizontal(24.dp)
            textStyle(typography.labelLarge)
            disabled {
                animate {
                    background(colors.brandSecondary)
                }
            }
        }
        val cardStyle: Style = Style {
            shape(shapes.medium)
            background(colors.uiBackground)
            contentColor(colors.textPrimary)
        }
    }
    // [END android_compose_styles_default_object ]

    @Immutable
    data class JetsnackColors(
        val brand: Color,
        val brandLight: Color,
        val brandSecondary: Color,
        val uiBackground: Color,
        val textPrimary: Color,
        val isDark: Boolean,
    )
    val LightJetsnackColors = JetsnackColors(
        isDark = false,
        brand = Color.Magenta,
        brandLight = Color.LightGray,
        uiBackground = Color.LightGray,
        textPrimary = Color.Black,
        brandSecondary = Color.Blue
    )
    val DarkJetsnackColors = JetsnackColors(
        isDark = true,
        brand = Color.Magenta,
        brandLight = Color.LightGray,
        uiBackground = Color.DarkGray,
        textPrimary = Color.White,
        brandSecondary = Color.Blue
    )
    // Theme.kt
    // [START android_compose_styles_custom_theme]
    @Immutable
    class JetsnackTheme(
        val colors: JetsnackColors = LightJetsnackColors,
        val typography: androidx.compose.material3.Typography = androidx.compose.material3.Typography(),
        val shapes: Shapes = Shapes()
    ) {
        companion object {
            val colors: JetsnackColors
                @Composable @ReadOnlyComposable
                get() = LocalJetsnackTheme.current.colors

            val typography: androidx.compose.material3.Typography
                @Composable @ReadOnlyComposable
                get() = LocalJetsnackTheme.current.typography

            val shapes: Shapes
                @Composable @ReadOnlyComposable
                get() = LocalJetsnackTheme.current.shapes

            val styles: JetsnackStyles = JetsnackStyles

            val LocalJetsnackTheme: ProvidableCompositionLocal<JetsnackTheme>
                get() = LocalJetsnackThemeInstance
        }
    }

    val StyleScope.colors: JetsnackColors
        get() = LocalJetsnackTheme.currentValue.colors

    val StyleScope.typography: androidx.compose.material3.Typography
        get() = LocalJetsnackTheme.currentValue.typography

    val StyleScope.shapes: Shapes
        get() = LocalJetsnackTheme.currentValue.shapes

    internal val LocalJetsnackThemeInstance = staticCompositionLocalOf { JetsnackTheme() }

    @Composable
    fun JetsnackTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
        val colors = if (darkTheme) DarkJetsnackColors else LightJetsnackColors
        val theme = JetsnackTheme(colors = colors)

        CompositionLocalProvider(
            LocalJetsnackTheme provides theme,
        ) {
            MaterialTheme(
                typography = LocalJetsnackTheme.current.typography,
                shapes = LocalJetsnackTheme.current.shapes,
                content = content,
            )
        }
    }
    // [END android_compose_styles_custom_theme]

    // [START android_compose_styles_custom_button_theme]
    @Composable
    fun CustomButton(modifier: Modifier,
                     style: Style = Style,
                     text: String) {
        val interactionSource = remember { MutableInteractionSource() }
        val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

        // Apply style to top level container in combination with incoming style from parameter.
        Box(modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Button,
                onClick = {

                },
            )
            .styleable(styleState, JetsnackTheme.styles.buttonStyle, style)) {
            Text(text)
        }
    }
    // [END android_compose_styles_custom_button_theme]
}