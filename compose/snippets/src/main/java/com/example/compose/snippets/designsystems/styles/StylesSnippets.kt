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
@file:Suppress("Unused", "UnusedVariable")

package com.example.compose.snippets.designsystems.styles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.designsystems.FullyCustomDesignSystem.LocalCustomColors
import com.example.compose.snippets.designsystems.styles.components.BaseButton
import com.example.compose.snippets.designsystems.styles.components.BaseText

@Composable
fun BasicButtonStyle() {
    // [START android_compose_styles_basic_button]
    BaseButton(
        onClick = { },
        style = { }
    ) {
        BaseText("Click me")
    }
    // [END android_compose_styles_basic_button]
}

@Composable
fun ButtonBackgroundStyle() {
    // [START android_compose_styles_button_background]
    BaseButton(
        onClick = { },
        style = { background(Color.Blue) }
    ) {
        BaseText("Click me")
    }
    // [END android_compose_styles_button_background]
}


@Composable
fun RowStyleable() {
    // [START android_compose_styles_row_styleable]
    Row(
        modifier = Modifier.styleable { }
    ) {
        BaseText("Content")
    }
    // [END android_compose_styles_row_styleable]
}


@Composable
fun RowStyleableBackground() {
    // [START android_compose_styles_row_styleable_background]
    Row(
        modifier = Modifier.styleable {
            background(Color.Blue)
        }
    ) {
        BaseText("Content")
    }
    // [END android_compose_styles_row_styleable_background]
}


// [START android_compose_styles_standalone_style]
val style = Style { background(Color.Blue) }
// [END android_compose_styles_standalone_style]

@Composable
fun StandaloneStyleUsage() {
    // [START android_compose_styles_standalone_usage]
    val style = Style { background(Color.Blue) }

    // built in parameter
    BaseButton(onClick = { }, style = style) {
        BaseText("Button")
    }

    // modifier styleable
    val styleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(styleState, style)
    ) {
        BaseText("Column content")
    }
    // [END android_compose_styles_standalone_usage]
}

@Composable
fun MultipleComponentsStyle() {

// [START android_compose_styles_multiple_components]
    val style = Style { background(Color.Blue) }

    // built in parameter
    BaseButton(onClick = { }, style = style) {
        BaseText("Button")
    }
    BaseText("Different text that uses the same style parameter", style = style)

    // modifier styleable
    val columnStyleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(columnStyleState, style)
    ) {
        BaseText("Column")
    }
    val rowStyleState = remember { MutableStyleState(null) }
    Row(
        Modifier.styleable(rowStyleState, style)
    ) {
        BaseText("Row")
    }
    // [END android_compose_styles_multiple_components]
}


@Composable
fun MultiplePropertiesStyle() {

// [START android_compose_styles_multiple_properties]
    BaseButton(
        onClick = { },
        style = {
            background(Color.Blue)
            contentPaddingStart(16.dp)
        }
    ) {
        BaseText("Button")
    }
    // [END android_compose_styles_multiple_properties]
}


val TealColor = Color(0xFF008080)


@Composable
fun OverwritePropertiesStyle() {
    // [START android_compose_styles_overwrite_properties]
    BaseButton(
        style = {
            background(Color.Red)
            // Background of Red is now overridden with TealColor instead
            background(TealColor)
            // All directions of padding are set to 64.dp (top, start, end, bottom)
            contentPadding(64.dp)
            // Top padding is now set to 16.dp, all other paddings remain at 64.dp
            contentPaddingTop(16.dp)
        },
        onClick = {
            //
        }
    ) {
        BaseText("Click me!")
    }
    // [END android_compose_styles_overwrite_properties]
}



@Composable
fun MergeStyles() {
    // [START android_compose_styles_merge_styles]
    val style1 = Style { background(TealColor) }
    val style2 = Style { contentPaddingTop(16.dp) }

    BaseButton(
        style = style1 then style2,
        onClick = {

        },
    ) {
        BaseText("Click me!")
    }
    // [END android_compose_styles_merge_styles]
}

@Composable
fun MergeOverwriteStyles() {
    // [START android_compose_styles_merge_overwrite]
    val style1 = Style {
        background(Color.Red)
        contentPadding(32.dp)
    }

    val style2 = Style {
        contentPaddingHorizontal(8.dp)
        background(Color.LightGray)
    }

    BaseButton(
        style = style1 then style2,
        onClick = {

        },
    ) {
        BaseText("Click me!")
    }
    // [END android_compose_styles_merge_overwrite]
}

@Composable
fun ParentStyling() {
    // [START android_compose_styles_parent_styling]
    val styleState = remember { MutableStyleState(null) }
    Column(
        modifier = Modifier.styleable(styleState) {
            background(Color.LightGray)
            val blue = Color(0xFF4285F4)
            val purple = Color(0xFFA250EA)
            val colors = listOf(blue, purple)
            contentBrush(Brush.linearGradient(colors))
        },
    ) {
        BaseText("Children inherit", style = { width(60.dp) })
        BaseText("certain properties")
        BaseText("from their parents")
    }
    // [END android_compose_styles_parent_styling]
}


@Composable
fun ChildOverrideStyling() {
    // [START android_compose_styles_child_override]
    val styleState = remember { MutableStyleState(null) }
    Column(
        modifier = Modifier.styleable(styleState) {
            background(Color.LightGray)
            val blue = Color(0xFF4285F4)
            val purple = Color(0xFFA250EA)
            val colors = listOf(blue, purple)
            contentBrush(Brush.linearGradient(colors))
        },
    ) {
        BaseText("Children can ", style = {
            contentBrush(Brush.linearGradient(listOf(Color.Red, Color.Blue)))
        })
        BaseText("override properties")
        BaseText("set by their parents")
    }
    // [END android_compose_styles_child_override]
}

// [START android_compose_styles_custom_extension]
fun StyleScope.outlinedBackground(color: Color) {
    border(1.dp, color)
    background(color)
}
// [END android_compose_styles_custom_extension]

// [START android_compose_styles_custom_extension_usage]
val customExtensionStyle = Style {
    outlinedBackground(Color.Blue)
}
// [END android_compose_styles_custom_extension_usage]

@Composable
fun CompositionLocalStyle() {
    // [START android_compose_styles_composition_local]
    val buttonStyle = Style {
        contentPadding(12.dp)
        shape(RoundedCornerShape(50))
        background(Brush.verticalGradient(LocalCustomColors.currentValue.background))
    }
    // [END android_compose_styles_composition_local]
}

// [START android_compose_styles_design_system_component]
@Composable
fun LoginButtonSnippet(modifier: Modifier = Modifier, style: Style = Style) {
    // Your custom component applying the style via the styleable modifier
    // e.g., Box(modifier = modifier.styleable(styleState, style)) 
}
// [END android_compose_styles_design_system_component]

// [START android_compose_styles_theme_integration]
@Immutable
data class AppStyles(
    val baseButtonStyle: Style = Style,
    val baseTextStyle: Style = Style,
    val baseCardStyle: Style = Style
)

val LocalAppStyles = compositionLocalOf { AppStyles() }

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val styles = AppStyles()
    CompositionLocalProvider(
        LocalAppStyles provides styles,
        content = content
    )
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

    Box(
        modifier = modifier
            .clickable(interactionSource = interactionSource, onClick = { })
            .styleable(styleState, LocalAppStyles.current.baseButtonStyle, style)
    ) {
        BaseText(text)
    }
}
// [END android_compose_styles_theme_integration]
