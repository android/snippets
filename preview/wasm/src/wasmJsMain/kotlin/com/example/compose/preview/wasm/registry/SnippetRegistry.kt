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
import com.example.compose.snippets.animations.AnimateElevation
import com.example.compose.snippets.animations.AnimateOffset
import com.example.compose.snippets.animations.AnimatePadding
import com.example.compose.snippets.animations.AnimateSizeChange
import com.example.compose.snippets.animations.AnimateTextColor
import com.example.compose.snippets.animations.AnimatedContentExampleSwitch
import com.example.compose.snippets.animations.AnimatedVisibilityCookbook
import com.example.compose.snippets.animations.AnimatedVisibilityCookbook_ModifierAlpha
import com.example.compose.snippets.animations.AnimationLayout
import com.example.compose.snippets.animations.SmoothAnimateText
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
/**
 * 16:9 frame for the animation quick guide snippets. They're written for a phone-sized screen
 * (for example a box that grows from 200dp to 400dp tall), so they need more room than
 * [DefaultPreviewFrame].
 */
val AnimationPreviewFrame = PreviewFrame(widthDp = 768, heightDp = 432)

private val SnippetPreviewFrames: Map<String, PreviewFrame> = mapOf(
    "android_compose_animation_cookbook_visibility" to AnimationPreviewFrame,
    "android_compose_animation_cookbook_visibility_alpha" to AnimationPreviewFrame,
    "android_compose_animation_size_change" to AnimationPreviewFrame,
    "android_compose_animation_offset_change" to AnimationPreviewFrame,
    "android_compose_animation_layout_offset" to AnimationPreviewFrame,
    "android_compose_animation_padding" to AnimationPreviewFrame,
    "android_compose_animation_cookbook_elevation" to AnimationPreviewFrame,
    "android_compose_animation_cookbook_text" to AnimationPreviewFrame,
    "android_compose_animation_cookbook_text_color" to AnimationPreviewFrame,
    "android_compose_animation_cookbook_animated_content" to AnimationPreviewFrame,
)

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
        // Animation quick guide. Their static DAC previews are GIFs recorded by
        // PreviewScreenshotTest from PreviewRecordings.
        "android_compose_animation_cookbook_visibility" to { AnimatedVisibilityCookbook() },
        "android_compose_animation_cookbook_visibility_alpha" to { AnimatedVisibilityCookbook_ModifierAlpha() },
        "android_compose_animation_size_change" to { AnimateSizeChange() },
        "android_compose_animation_offset_change" to { AnimateOffset() },
        "android_compose_animation_layout_offset" to { AnimationLayout() },
        "android_compose_animation_padding" to { AnimatePadding() },
        "android_compose_animation_cookbook_elevation" to { AnimateElevation() },
        "android_compose_animation_cookbook_text" to { SmoothAnimateText() },
        "android_compose_animation_cookbook_text_color" to { AnimateTextColor() },
        "android_compose_animation_cookbook_animated_content" to { AnimatedContentExampleSwitch() },
    )

    fun getById(id: String): (@Composable () -> Unit)? = snippets[id]
}
