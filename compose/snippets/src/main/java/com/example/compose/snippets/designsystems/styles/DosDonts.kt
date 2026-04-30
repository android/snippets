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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.compose.snippets.designsystems.styles.CustomThemingWithStyles.JetsnackColors


// [START android_compose_styles_dos_expose_style]
@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    // ✅ DO: for design system components, expose a style modifier to consumers to be able to customize the components
    style: Style = Style
) {
    // Consume the style
}
// [END android_compose_styles_dos_expose_style]

// [START android_compose_styles_dos_replace_params]
// Before
@Composable
fun OldButton(background: Color, fontColor: Color) {
}

// After
// ✅ DO: Replace visual-based parameters with a style that includes same properties
@Composable
fun NewButton(style: Style = Style) {
}
// [END android_compose_styles_dos_replace_params]

// [START android_compose_styles_dos_wrapper]
@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    // Uses LocalTheme.appStyles.button + incoming style
}

// ✅ Do create wrapper composables that expose common implementations of the same component
@Composable
fun SpecialGradientButton(
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    // Uses LocalTheme.appStyles.button + LocalTheme.appStyles.gradientButton + incoming style - merge these styles
}
// [END android_compose_styles_dos_wrapper]

// [START android_compose_styles_donts_default_style]
@Composable
fun BadButton(
    modifier: Modifier = Modifier,
    // ❌ DON'T set a default style here as a parameter
    style: Style = Style { background(Color.Red) }
) {
}
// [END android_compose_styles_donts_default_style]

// [START android_compose_styles_do_default_style]
@Composable
fun GoodButton(
    modifier: Modifier = Modifier,
    // ✅ Do: always pass it as a Style, do not pass other defaults
    style: Style = Style
) {
    // [START_EXCLUDE]
    // this is a snippet of the BaseButton - see the full snippet in /components/Button.kt
    val effectiveInteractionSource = remember {
        MutableInteractionSource()
    }
    val styleState = remember(effectiveInteractionSource) {
        MutableStyleState(effectiveInteractionSource)
    }
    // [END_EXCLUDE]
    val defaultStyle = Style { background(Color.Red) }
    // ✅ Do Combine defaults inside with incoming parameter
    Box(modifier = modifier.styleable(styleState, defaultStyle, style)) {
      // your logic
    }
}
// [END android_compose_styles_do_default_style]

private object ThemingDoDonts {
    object JetsnackStyles {

    }
    val LightJetsnackColors = JetsnackColors(
        isDark = false,
        brand = Color.Magenta,
        brandLight = Color.LightGray,
        uiBackground = Color.LightGray,
        textPrimary = Color.Black,
        brandSecondary = Color.Blue
    )

    val LocalJetsnackTheme: ProvidableCompositionLocal<JetsnackTheme>
        get() = LocalJetsnackThemeInstance

    internal val LocalJetsnackThemeInstance = staticCompositionLocalOf { JetsnackTheme() }

    @Immutable
    class JetsnackTheme(
        val colors: JetsnackColors = LightJetsnackColors,
        val typography: androidx.compose.material3.Typography = androidx.compose.material3.Typography(),
        val shapes: Shapes = Shapes(),
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

    // [START android_compose_styles_theming_dos_composition]
    // DON'T - Create styles in Composition that access composition locals in this way - this will likely lead to issues when style is used / accessed, as it would not get updated when the value changes.
    @Composable
    fun containerStyle(): Style {
        val background = MaterialTheme.colorScheme.background
        val onBackground = MaterialTheme.colorScheme.onBackground
        return Style {
            background(background)
            contentColor(onBackground)
        }
    }

    // Do: Instead, Create StyleScope extension functions for your subsystems to access themed composition Locals
    val StyleScope.colors: JetsnackColors
        get() = JetsnackTheme.LocalJetsnackTheme.currentValue.colors

    val StyleScope.typography: androidx.compose.material3.Typography
        get() = JetsnackTheme.LocalJetsnackTheme.currentValue.typography
    val StyleScope.shapes: Shapes
        get() = JetsnackTheme.LocalJetsnackTheme.currentValue.shapes
    // Access CompositionLocals
    val button = Style {
        background(colors.brandSecondary)
        shape(shapes.small)
    }
    // [END android_compose_styles_theming_dos_composition]

    // [START android_compose_styles_themed_values]
    // Do: Use CompositionLocals or themed values to create a single style
    val buttonStyle = Style {
        background(colors.brandSecondary)
        shape(shapes.small)
    }
    // [END android_compose_styles_themed_values]

    // [START android_compose_styles_switch_product]
    // DO Switch out whole styles when many properties differ - if Product A and Product B are two white labelled apps that provide different Themes.
    val productBThemedButton = Style {
        shape(shapes.small)
        background(colors.brandSecondary)
        // other properties are fundamentally different
    }

    val productAThemedButton = Style {
        shape(shapes.large)
        background(colors.brand)
        // other properties are fundamentally different
    }
    // [END android_compose_styles_switch_product]
}

