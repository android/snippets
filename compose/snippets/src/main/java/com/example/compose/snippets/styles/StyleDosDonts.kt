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

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private object StyleDosDonts {

    // [START android_compose_styles_basics_do_expose]
    @Composable
    fun GradientButton(
        modifier: Modifier = Modifier,
        // ✅ DO: for design system components, expose a style modifier to consumers to be able to customize the components
        style: Style = Style
    ) {
        // Consume the style
    }
// [END android_compose_styles_basics_do_expose]

    // [START android_compose_styles_basics_do_replace]
// Before
    /*
    @Composable
    fun Button(background: Color, fontColor: Color) {

    }
    */
// After
// ✅ DO: Replace visual-based parameters with a style that includes same properties
    @Composable
    fun StyleButton(style: Style = Style) {

    }
// [END android_compose_styles_basics_do_replace]

// [START android_compose_styles_basics_do_wrappers]
    /*
    @Composable
    fun Button(modifier: Modifier = Modifier,
    style: Style = Style) {
    // Uses LocalTheme.appStyles.button + incoming style
    }
    */

    // ✅ Do create wrapper composables that expose common implementations of the same component
    @Composable
    fun WrapperGradientButton(
        modifier: Modifier = Modifier,
        style: Style = Style
    ) {
// Uses LocalTheme.appStyles.button + LocalTheme.appStyles.gradientButton + incoming style - merge these styles
    }
// [END android_compose_styles_basics_do_wrappers]

    // [START android_compose_styles_basics_dont_layout]
    @Composable
    fun FavouriteScreen(
        modifier: Modifier = Modifier,
        // ❌ DONT add style parameter to Screen-level composables
        style: Style = Style,
        // etc
    ) {

    }

    @Composable
    fun CircularCustomLayout(
        modifier: Modifier = Modifier,
        // ❌ DONT add style parameters to composables where the main function is layout.
        style: Style = Style
    ) {

    }
// [END android_compose_styles_basics_dont_layout]
}
