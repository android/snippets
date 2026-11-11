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

package com.example.compose.preview.wasm.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.platform.Font
import com.example.compose.preview.wasm.registry.PREVIEW_FONT_NAME
import com.example.compose.preview.wasm.registry.PreviewFontWeights
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.fetch.Response

/**
 * Fetches the Roboto Flex variable font that is packaged next to `wasm.html` (see the
 * `packageDevelopmentSite`/`packageStaticSite` tasks) and builds a [FontFamily] with one entry per
 * [PreviewFontWeights], each pinned to its `wght` axis value.
 *
 * Compose for Web has no system fonts, and `skiko.wasm` only embeds Roboto Regular, so without
 * this the live preview draws Medium/Bold text with a synthesized weight that doesn't match the
 * Roborazzi screenshots.
 *
 * Returns `null` if the font can't be loaded, so the preview still renders with the fallback face.
 */
@OptIn(ExperimentalTextApi::class)
suspend fun loadPreviewFontFamily(): FontFamily? = runCatching {
    val response = window.fetch("fonts/$PREVIEW_FONT_NAME.ttf").await<Response>()
    if (!response.ok) error("HTTP ${response.status}")
    val buffer = response.arrayBuffer().await<ArrayBuffer>()
    val int8 = Int8Array(buffer)
    val bytes = ByteArray(int8.length) { int8[it] }
    FontFamily(
        PreviewFontWeights.map { weight ->
            Font(
                identity = "$PREVIEW_FONT_NAME-${weight.weight}",
                data = bytes,
                weight = weight,
                variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
            )
        }
    )
}.onFailure {
    println("Failed to load preview font, falling back to the default font: $it")
}.getOrNull()
