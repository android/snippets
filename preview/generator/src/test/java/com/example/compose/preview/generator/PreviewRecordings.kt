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

package com.example.compose.preview.generator

import com.example.compose.preview.wasm.registry.AnimationPreviewFrame

/** One step of the interaction script used to record a snippet's animated preview. */
sealed interface PreviewStep {
    /** Lets time pass (and animations run) for [millis]. */
    data class Wait(val millis: Long) : PreviewStep

    /**
     * Clicks the node showing [text], or the first clickable node when [text] is null (for
     * snippets that toggle when you tap the animated composable itself).
     */
    data class Click(val text: String? = null) : PreviewStep

    /** Presses and holds the first clickable node for [millis], then releases it. */
    data class Press(val millis: Long) : PreviewStep
}

/**
 * A script for recording a snippet's animated preview (`<region_tag>.mp4` on DAC). The recording
 * should loop cleanly, so scripts usually end in the state they started in.
 */
data class PreviewRecording(val steps: List<PreviewStep>)

private val ToggleTwice = PreviewRecording(
    listOf(
        PreviewStep.Wait(600),
        PreviewStep.Click(),
        PreviewStep.Wait(1_200),
        PreviewStep.Click(),
        PreviewStep.Wait(1_200),
    )
)

private val ShowHideTwice = PreviewRecording(
    listOf(
        PreviewStep.Wait(600),
        PreviewStep.Click("Show/Hide"),
        PreviewStep.Wait(1_200),
        PreviewStep.Click("Show/Hide"),
        PreviewStep.Wait(1_200),
    )
)

/** One full cycle of a two-second `infiniteRepeatable(tween(1000), RepeatMode.Reverse)`. */
private val OneInfiniteCycle = PreviewRecording(listOf(PreviewStep.Wait(2_000)))

/**
 * Snippets whose static DAC preview is an animated GIF instead of a PNG, keyed by region tag.
 * [PreviewScreenshotTest.record] plays each script against the snippet in `SnippetRegistry`.
 * Give recorded snippets a frame in `SnippetPreviewFrames` (usually [AnimationPreviewFrame]).
 */
val SnippetRecordings: Map<String, PreviewRecording> = mapOf(
    "android_compose_animation_cookbook_visibility" to ShowHideTwice,
    "android_compose_animation_cookbook_visibility_alpha" to ShowHideTwice,
    "android_compose_animation_size_change" to ToggleTwice,
    "android_compose_animation_offset_change" to ToggleTwice,
    "android_compose_animation_layout_offset" to ToggleTwice,
    "android_compose_animation_padding" to ToggleTwice,
    "android_compose_animation_cookbook_elevation" to PreviewRecording(
        listOf(PreviewStep.Wait(500), PreviewStep.Press(1_200), PreviewStep.Wait(900))
    ),
    "android_compose_animation_cookbook_text" to OneInfiniteCycle,
    "android_compose_animation_cookbook_text_color" to OneInfiniteCycle,
    // Each state change crossfades for 3 seconds.
    "android_compose_animation_cookbook_animated_content" to PreviewRecording(
        listOf(
            PreviewStep.Wait(800),
            PreviewStep.Click(),
            PreviewStep.Wait(3_200),
            PreviewStep.Click(),
            PreviewStep.Wait(3_200),
            PreviewStep.Click(),
            PreviewStep.Wait(3_200),
        )
    ),
)
