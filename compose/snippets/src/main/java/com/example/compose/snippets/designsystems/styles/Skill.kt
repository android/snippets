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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.designsystems.styles.CustomThemingWithStyles.JetsnackColors
import com.example.compose.snippets.designsystems.styles.CustomThemingWithStyles.JetsnackTheme.Companion.LocalJetsnackTheme



// [START android_compose_styles_skill_component_styles]
object ExampleComponentStyles {
    val customButtonStyle: Style = {

    }
    val customTextFieldStyle: Style = {

    }
}
// [END android_compose_styles_skill_component_styles]

// [START android_compose_styles_skill_component_styles_theme]
@Immutable
class JetsnackTheme(
    // other Design system properties
) {
    companion object {
        val colors: CustomThemingWithStyles.JetsnackColors
            @Composable @ReadOnlyComposable
            get() = LocalJetsnackTheme.current.colors
        // [START_EXCLUDE]
        val shapes: Shapes
            @Composable @ReadOnlyComposable
            get() = LocalJetsnackTheme.current.shapes
        val typography: Typography
            @Composable @ReadOnlyComposable
            get() = LocalJetsnackTheme.current.typography
        // [END_EXCLUDE]

        // add helper static reference
        val styles: ComponentStyles = ComponentStyles
    }
}
// [END android_compose_styles_skill_component_styles_theme]


// [START android_compose_styles_skill_scope_ext]
val StyleScope.colors: JetsnackColors
    get() = LocalJetsnackTheme.currentValue.colors

val StyleScope.typography: androidx.compose.material3.Typography
    get() = LocalJetsnackTheme.currentValue.typography

val StyleScope.shapes: Shapes
    get() = LocalJetsnackTheme.currentValue.shapes

// [END android_compose_styles_skill_scope_ext]

// [START android_compose_styles_skill_before_migration]
@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = JetsnackTheme.colors.brandLight,
    disabledBackgroundColor: Color = JetsnackTheme.colors.brandSecondary,
    shape: Shape = JetsnackTheme.shapes.extraLarge,
    textStyle: TextStyle = JetsnackTheme.typography.labelLarge,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier
            .clickable(onClick = onClick, indication = null, interactionSource = interactionSource)
            .background(if (enabled) backgroundColor else disabledBackgroundColor, shape)
            .defaultMinSize(58.dp, 40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
// [END android_compose_styles_skill_before_migration]

// [START android_compose_styles_skill_after_migration]
// Exposed via ComponentStyles.kt
object ComponentStyles {
    val buttonStyle = Style {
        background(colors.brandLight)
        shape(shapes.extraLarge)
        minWidth(58.dp)
        minHeight(40.dp)
        textStyle(typography.labelLarge)
        disabled {
            background(colors.brandSecondary)
        }
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }
    Row(
        modifier
            .clickable(onClick = onClick, indication = null, interactionSource = interactionSource)
            .styleable(styleState, JetsnackTheme.styles.buttonStyle, style),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
// [END android_compose_styles_skill_after_migration]