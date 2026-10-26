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

@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class
)

package com.example.compose.preview.wasm.registry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.components.ElevatedButtonExample
import com.example.compose.snippets.components.FilledButtonExample
import com.example.compose.snippets.components.FilledTonalButtonExample
import com.example.compose.snippets.components.OutlinedButtonExample
import com.example.compose.snippets.components.TextButtonExample

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    // A Column rather than a Box so snippets that emit several top-level elements (for example
    // CheckboxMinimalExample's Row followed by a Text) stack instead of drawing on top of each
    // other.
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

/**
 * The area, in dp, a snippet preview is laid out in.
 *
 * The Roborazzi screenshot is captured on a device of exactly this size, and the live WASM preview
 * scales its density so this area fills the DAC stage. Both renderers therefore show the same
 * layout, and components that need more room than [DefaultPreviewFrame] (dialogs and pickers)
 * are zoomed out rather than clipped.
 */
data class PreviewFrame(val widthDp: Int, val heightDp: Int)

/** 16:9, matching the default 270px-tall DAC stage at 1.25x. */
val DefaultPreviewFrame = PreviewFrame(widthDp = 384, heightDp = 216)

/**
 * Larger frames for snippets that don't fit [DefaultPreviewFrame] (for example dialogs and
 * pickers). Keep 16:9 where possible so the static image fills the DAC stage.
 */
private val SnippetPreviewFrames: Map<String, PreviewFrame> = mapOf()

fun previewFrameFor(id: String): PreviewFrame = SnippetPreviewFrames[id] ?: DefaultPreviewFrame

/**
 * Single source of truth mapping DAC snippet region tag IDs to their
 * interactive `@Composable` preview lambdas.
 *
 * Used by both:
 * - `:preview:wasm` (`WasmPreviewApp`) to render interactive snippets by `?id=<region_tag>`
 * - `:preview:generator` (`PreviewScreenshotTest`) to capture static `<region_tag>.png` screenshots
 */
object SnippetRegistry {
    val snippets: Map<String, @Composable () -> Unit> = mapOf(
        "android_compose_components_filledbutton" to { CenteredBox { FilledButtonExample(onClick = {}) } },
        "android_compose_components_filledtonalbutton" to { CenteredBox { FilledTonalButtonExample(onClick = {}) } },
        "android_compose_components_elevatedbutton" to { CenteredBox { ElevatedButtonExample(onClick = {}) } },
        "android_compose_components_outlinedbutton" to { CenteredBox { OutlinedButtonExample(onClick = {}) } },
        "android_compose_components_textbutton" to { CenteredBox { TextButtonExample(onClick = {}) } },
    )

    fun getById(id: String): (@Composable () -> Unit)? = snippets[id]
}
