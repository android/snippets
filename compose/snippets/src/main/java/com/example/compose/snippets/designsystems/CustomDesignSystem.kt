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

package com.example.compose.snippets.designsystems

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object CustomDesignSystemExtend {
    // [START android_compose_designsystems_custom_extend]

    // Use with MaterialTheme.colorScheme.snackbarAction
    val ColorScheme.snackbarAction: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Red300 else Red700

    // Use with MaterialTheme.typography.textFieldInput
    val Typography.textFieldInput: TextStyle
        get() = TextStyle(/* ... */)

    // Use with MaterialTheme.shapes.card
    val Shapes.card: Shape
        get() = RoundedCornerShape(size = 20.dp)
    // [END android_compose_designsystems_custom_extend]

    val Red300 = Color(0xFFE57373)
    val Red700 = Color(0xFFD32F2F)
}

private object CustomDesignSystemExtendTheme {
    // [START android_compose_designsystems_custom_extend_theme]
    @Immutable
    data class ExtendedColors(
        val caution: Color,
        val onCaution: Color
    )

    val LocalExtendedColors = staticCompositionLocalOf {
        ExtendedColors(
            caution = Color.Unspecified,
            onCaution = Color.Unspecified
        )
    }

    @Composable
    fun ExtendedTheme(
        /* ... */
        content: @Composable () -> Unit
    ) {
        val extendedColors = ExtendedColors(
            caution = Color(0xFFFFCC02),
            onCaution = Color(0xFF2C2D30)
        )
        CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
            MaterialTheme(
                /* colors = ..., typography = ..., shapes = ... */
                content = content
            )
        }
    }

    // Use with eg. ExtendedTheme.colors.caution
    object ExtendedTheme {
        val colors: ExtendedColors
            @Composable
            get() = LocalExtendedColors.current
    }
    // [END android_compose_designsystems_custom_extend_theme]

    // [START android_compose_designsystems_extend_button]
    @Composable
    fun ExtendedButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = ExtendedTheme.colors.caution,
                contentColor = ExtendedTheme.colors.onCaution
                /* Other colors use values from MaterialTheme */
            ),
            onClick = onClick,
            modifier = modifier,
            content = content
        )
    }
    // [END android_compose_designsystems_extend_button]

    // [START android_compose_designsystems_use_button]
    @Composable
    fun ExtendedApp() {
        ExtendedTheme {
            /*...*/
            ExtendedButton(onClick = { /* ... */ }) {
                /* ... */
            }
        }
    }
    // [END android_compose_designsystems_use_button]
}

object ReplaceMaterialSystem {
    // [START android_compose_designsystems_replace_material]
    @Immutable
    data class ReplacementTypography(
        val body: TextStyle,
        val title: TextStyle
    )

    @Immutable
    data class ReplacementShapes(
        val component: Shape,
        val surface: Shape
    )

    val LocalReplacementTypography = staticCompositionLocalOf {
        ReplacementTypography(
            body = TextStyle.Default,
            title = TextStyle.Default
        )
    }
    val LocalReplacementShapes = staticCompositionLocalOf {
        ReplacementShapes(
            component = RoundedCornerShape(ZeroCornerSize),
            surface = RoundedCornerShape(ZeroCornerSize)
        )
    }

    @Composable
    fun ReplacementTheme(
        /* ... */
        content: @Composable () -> Unit
    ) {
        val replacementTypography = ReplacementTypography(
            body = TextStyle(fontSize = 16.sp),
            title = TextStyle(fontSize = 32.sp)
        )
        val replacementShapes = ReplacementShapes(
            component = RoundedCornerShape(percent = 50),
            surface = RoundedCornerShape(size = 40.dp)
        )
        CompositionLocalProvider(
            LocalReplacementTypography provides replacementTypography,
            LocalReplacementShapes provides replacementShapes
        ) {
            MaterialTheme(
                /* colors = ... */
                content = content
            )
        }
    }

    // Use with eg. ReplacementTheme.typography.body
    object ReplacementTheme {
        val typography: ReplacementTypography
            @Composable
            get() = LocalReplacementTypography.current
        val shapes: ReplacementShapes
            @Composable
            get() = LocalReplacementShapes.current
    }
    // [END android_compose_designsystems_replace_material]

    // [START android_compose_designsystems_replace_button]
    @Composable
    fun ReplacementButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit
    ) {
        Button(
            shape = ReplacementTheme.shapes.component,
            onClick = onClick,
            modifier = modifier,
            content = {
                ProvideTextStyle(
                    value = ReplacementTheme.typography.body
                ) {
                    content()
                }
            }
        )
    }
    // [END android_compose_designsystems_replace_button]

    // [START android_compose_designsystems_replace_theme]
    @Composable
    fun ReplacementApp() {
        ReplacementTheme {
            /*...*/
            ReplacementButton(onClick = { /* ... */ }) {
                /* ... */
            }
        }
    }
    // [END android_compose_designsystems_replace_theme]
}

object FullyCustomDesignSystem {
    // [START android_compose_designsystems_fully_custom]
    @Immutable
    data class CustomColors(
        val content: Color,
        val component: Color,
        val background: List<Color>
    )

    @Immutable
    data class CustomTypography(
        val body: TextStyle,
        val title: TextStyle
    )

    @Immutable
    data class CustomElevation(
        val default: Dp,
        val pressed: Dp
    )

    val LocalCustomColors = staticCompositionLocalOf {
        CustomColors(
            content = Color.Unspecified,
            component = Color.Unspecified,
            background = emptyList()
        )
    }
    val LocalCustomTypography = staticCompositionLocalOf {
        CustomTypography(
            body = TextStyle.Default,
            title = TextStyle.Default
        )
    }
    val LocalCustomElevation = staticCompositionLocalOf {
        CustomElevation(
            default = Dp.Unspecified,
            pressed = Dp.Unspecified
        )
    }

    @Composable
    fun CustomTheme(
        /* ... */
        content: @Composable () -> Unit
    ) {
        val customColors = CustomColors(
            content = Color(0xFFDD0D3C),
            component = Color(0xFFC20029),
            background = listOf(Color.White, Color(0xFFF8BBD0))
        )
        val customTypography = CustomTypography(
            body = TextStyle(fontSize = 16.sp),
            title = TextStyle(fontSize = 32.sp)
        )
        val customElevation = CustomElevation(
            default = 4.dp,
            pressed = 8.dp
        )
        CompositionLocalProvider(
            LocalCustomColors provides customColors,
            LocalCustomTypography provides customTypography,
            LocalCustomElevation provides customElevation,
            content = content
        )
    }

    // Use with eg. CustomTheme.elevation.small
    object CustomTheme {
        val colors: CustomColors
            @Composable
            get() = LocalCustomColors.current
        val typography: CustomTypography
            @Composable
            get() = LocalCustomTypography.current
        val elevation: CustomElevation
            @Composable
            get() = LocalCustomElevation.current
    }
    // [END android_compose_designsystems_fully_custom]

    // [START android_compose_designsystems_fully_custom_usage]
    @Composable
    fun CustomButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomTheme.colors.component,
                contentColor = CustomTheme.colors.content,
                disabledContainerColor = CustomTheme.colors.content
                    .copy(alpha = 0.12f)
                    .compositeOver(CustomTheme.colors.component),
                disabledContentColor = CustomTheme.colors.content
                    .copy(alpha = 0.38f)

            ),
            shape = ButtonShape,
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = CustomTheme.elevation.default,
                pressedElevation = CustomTheme.elevation.pressed
                /* disabledElevation = 0.dp */
            ),
            onClick = onClick,
            modifier = modifier,
            content = {
                ProvideTextStyle(
                    value = CustomTheme.typography.body
                ) {
                    content()
                }
            }
        )
    }

    val ButtonShape = RoundedCornerShape(percent = 50)
    // [END android_compose_designsystems_fully_custom_usage]
}
