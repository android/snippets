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

@file:Suppress("unused")

package com.example.compose.snippets.designsystems

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

// [START android_compose_anatomy_classes]
@Immutable
data class ColorSystem(
    val color: Color,
    val gradient: List<Color>
    /* ... */
)

@Immutable
data class TypographySystem(
    val fontFamily: FontFamily,
    val textStyle: TextStyle
)
/* ... */

@Immutable
data class CustomSystem(
    val value1: Int,
    val value2: String
    /* ... */
)

/* ... */
// [END android_compose_anatomy_classes]

// [START android_compose_anatomy_composition_locals]
val LocalColorSystem = staticCompositionLocalOf {
    ColorSystem(
        color = Color.Unspecified,
        gradient = emptyList()
    )
}

val LocalTypographySystem = staticCompositionLocalOf {
    TypographySystem(
        fontFamily = FontFamily.Default,
        textStyle = TextStyle.Default
    )
}

val LocalCustomSystem = staticCompositionLocalOf {
    CustomSystem(
        value1 = 0,
        value2 = ""
    )
}

/* ... */
// [END android_compose_anatomy_composition_locals]

// [START android_compose_anatomy_functions]
@Composable
fun Theme(
    /* ... */
    content: @Composable () -> Unit
) {
    val colorSystem = ColorSystem(
        color = Color(0xFF3DDC84),
        gradient = listOf(Color.White, Color(0xFFD7EFFF))
    )
    val typographySystem = TypographySystem(
        fontFamily = FontFamily.Monospace,
        textStyle = TextStyle(fontSize = 18.sp)
    )
    val customSystem = CustomSystem(
        value1 = 1000,
        value2 = "Custom system"
    )
    /* ... */
    CompositionLocalProvider(
        LocalColorSystem provides colorSystem,
        LocalTypographySystem provides typographySystem,
        LocalCustomSystem provides customSystem,
        /* ... */
        content = content
    )
}
// [END android_compose_anatomy_functions]

// [START android_compose_anatomy_object]
// Use with eg. Theme.colorSystem.color
object Theme {
    val colorSystem: ColorSystem
        @Composable
        get() = LocalColorSystem.current
    val typographySystem: TypographySystem
        @Composable
        get() = LocalTypographySystem.current
    val customSystem: CustomSystem
        @Composable
        get() = LocalCustomSystem.current
    /* ... */
}
// [END android_compose_anatomy_object]
