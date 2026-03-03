@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.designsystems.styles

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


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
