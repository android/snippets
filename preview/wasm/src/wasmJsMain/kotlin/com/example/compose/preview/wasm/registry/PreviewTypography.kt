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

package com.example.compose.preview.wasm.registry

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * File name (without extension) of the variable Roboto Flex font shared by both preview
 * renderers. It lives in `compose/snippets/src/main/res/font/` and is loaded as an Android
 * resource by `:preview:generator` and fetched over HTTP by `:preview:wasm`.
 */
const val PREVIEW_FONT_NAME = "robotoflex_variable"

/**
 * The weights the Material 3 type scale uses. Each one is served from the same variable font
 * file with an explicit `wght` axis value, so the font engine draws a real weight instead of
 * synthesizing bold from the regular face.
 */
val PreviewFontWeights: List<FontWeight> = listOf(
    FontWeight.Normal,
    FontWeight.Medium,
    FontWeight.SemiBold,
    FontWeight.Bold,
)

/**
 * Returns the default Material 3 [Typography] with every style switched to [family].
 *
 * The static Roborazzi screenshots and the live WASM preview must draw text with the same
 * typeface. Without an explicit family, Robolectric draws with the platform Roboto (all weights)
 * while Compose for Web falls back to the Roboto Regular embedded in `skiko.wasm`, so Medium
 * labels (for example, button text) render at a different weight and width.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun previewTypography(family: FontFamily): Typography {
    val d = Typography()
    return d.copy(
        displayLarge = d.displayLarge.copy(fontFamily = family),
        displayMedium = d.displayMedium.copy(fontFamily = family),
        displaySmall = d.displaySmall.copy(fontFamily = family),
        headlineLarge = d.headlineLarge.copy(fontFamily = family),
        headlineMedium = d.headlineMedium.copy(fontFamily = family),
        headlineSmall = d.headlineSmall.copy(fontFamily = family),
        titleLarge = d.titleLarge.copy(fontFamily = family),
        titleMedium = d.titleMedium.copy(fontFamily = family),
        titleSmall = d.titleSmall.copy(fontFamily = family),
        bodyLarge = d.bodyLarge.copy(fontFamily = family),
        bodyMedium = d.bodyMedium.copy(fontFamily = family),
        bodySmall = d.bodySmall.copy(fontFamily = family),
        labelLarge = d.labelLarge.copy(fontFamily = family),
        labelMedium = d.labelMedium.copy(fontFamily = family),
        labelSmall = d.labelSmall.copy(fontFamily = family),
        displayLargeEmphasized = d.displayLargeEmphasized.copy(fontFamily = family),
        displayMediumEmphasized = d.displayMediumEmphasized.copy(fontFamily = family),
        displaySmallEmphasized = d.displaySmallEmphasized.copy(fontFamily = family),
        headlineLargeEmphasized = d.headlineLargeEmphasized.copy(fontFamily = family),
        headlineMediumEmphasized = d.headlineMediumEmphasized.copy(fontFamily = family),
        headlineSmallEmphasized = d.headlineSmallEmphasized.copy(fontFamily = family),
        titleLargeEmphasized = d.titleLargeEmphasized.copy(fontFamily = family),
        titleMediumEmphasized = d.titleMediumEmphasized.copy(fontFamily = family),
        titleSmallEmphasized = d.titleSmallEmphasized.copy(fontFamily = family),
        bodyLargeEmphasized = d.bodyLargeEmphasized.copy(fontFamily = family),
        bodyMediumEmphasized = d.bodyMediumEmphasized.copy(fontFamily = family),
        bodySmallEmphasized = d.bodySmallEmphasized.copy(fontFamily = family),
        labelLargeEmphasized = d.labelLargeEmphasized.copy(fontFamily = family),
        labelMediumEmphasized = d.labelMediumEmphasized.copy(fontFamily = family),
        labelSmallEmphasized = d.labelSmallEmphasized.copy(fontFamily = family),
    )
}
