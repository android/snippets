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
import com.example.compose.preview.wasm.registry.DefaultPreviewFrame

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

    /**
     * Smoothly drags across the root viewport from ([startXFraction], [startYFraction]) to
     * ([endXFraction], [endYFraction]) over [durationMillis], stepping frame-by-frame so
     * `recordRoboVideo` captures intermediate scroll frames without fling overshoot.
     */
    data class Drag(
        val startXFraction: Float = 0.5f,
        val startYFraction: Float,
        val endXFraction: Float = startXFraction,
        val endYFraction: Float,
        val durationMillis: Long = 800L,
    ) : PreviewStep

    /**
     * Smoothly moves the snippet's `Slider` from [from] to [to] over [durationMillis] via
     * `SemanticsActions.SetProgress`, so determinate indicators animate smoothly and return to
     * their exact starting value for seamless looping.
     */
    data class Slide(
        val from: Float,
        val to: Float,
        val durationMillis: Long = 1_200L,
    ) : PreviewStep
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

/** Full polygon morphing cycle for M3 indeterminate `LoadingIndicator` (7 shapes × 650 ms). */
private val LoadingIndicatorCycle = PreviewRecording(listOf(PreviewStep.Wait(4_550)))

/** Slides a determinate wavy progress indicator from its initial 0.1f up to 0.9f and back. */
private val DeterminateWavySliderCycle = PreviewRecording(
    listOf(
        PreviewStep.Wait(500),
        PreviewStep.Slide(from = 0.1f, to = 0.9f, durationMillis = 1_200),
        PreviewStep.Wait(600),
        PreviewStep.Slide(from = 0.9f, to = 0.1f, durationMillis = 1_200),
        PreviewStep.Wait(500),
    )
)

/** Scrolls down and back to the top using drags below a top app bar or above a bottom toolbar. */
private fun verticalScrollCycle(
    xFraction: Float = 0.5f,
    bottomYFraction: Float,
    topYFraction: Float,
    dragMillis: Long = 800L,
): PreviewRecording = PreviewRecording(
    listOf(
        PreviewStep.Wait(500),
        PreviewStep.Drag(
            startXFraction = xFraction,
            startYFraction = bottomYFraction,
            endYFraction = topYFraction,
            durationMillis = dragMillis,
        ),
        PreviewStep.Wait(600),
        PreviewStep.Drag(
            startXFraction = xFraction,
            startYFraction = topYFraction,
            endYFraction = bottomYFraction,
            durationMillis = dragMillis,
        ),
        PreviewStep.Wait(600),
    )
)

/**
 * Snippets whose static DAC preview is a looping MP4 instead of a PNG, keyed by region tag.
 * [PreviewScreenshotTest.record] plays each script against the snippet in `SnippetRegistry`.
 * Every recorded snippet must also declare its frame in `SnippetPreviewFrames` (for example
 * [DefaultPreviewFrame], [AnimationPreviewFrame], or a custom portrait frame for scrollable demos).
 */
/** Taps DeviceToggle's Foldable, Tablet and Phone buttons, so the card's layout switches. */
private val CardDeviceTour = PreviewRecording(
    listOf(
        PreviewStep.Wait(1_200),
        PreviewStep.Click("Foldable"),
        PreviewStep.Wait(1_500),
        PreviewStep.Click("Tablet"),
        PreviewStep.Wait(1_500),
        PreviewStep.Click("Phone"),
        PreviewStep.Wait(1_200),
    )
)

/**
 * Starts on Tablet, expands the description, switches to Phone and back to show the expanded
 * state survives, then collapses it again so the video loops.
 */
private val CardShowMoreTour = PreviewRecording(
    listOf(
        PreviewStep.Wait(1_000),
        PreviewStep.Click("Show more"),
        PreviewStep.Wait(1_200),
        PreviewStep.Click("Phone"),
        PreviewStep.Wait(1_200),
        PreviewStep.Click("Tablet"),
        PreviewStep.Wait(1_200),
        PreviewStep.Click("Show less"),
        PreviewStep.Wait(1_000),
    )
)

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
    // Support different display sizes: cards in a DeviceToggle.
    "android_compose_layouts_manual_measurements" to CardDeviceTour,
    "android_compose_layouts_manual_measurements_3" to CardShowMoreTour,
    // Material 3 Expressive loading indicators.
    "android_compose_expressive_components_loadingindicator" to LoadingIndicatorCycle,
    "android_compose_expressive_components_containedloadingindicator" to LoadingIndicatorCycle,
    "android_compose_expressive_components_determinateloadingindicator" to PreviewRecording(
        listOf(
            PreviewStep.Wait(400),
            PreviewStep.Slide(from = 0f, to = 1f, durationMillis = 1_400),
            PreviewStep.Wait(800),
            PreviewStep.Slide(from = 1f, to = 0f, durationMillis = 1_400),
            PreviewStep.Wait(800),
        )
    ),
    // Material 3 Expressive wavy and indeterminate progress indicators.
    "android_compose_expressive_components_determinatelinearwavyindicator" to DeterminateWavySliderCycle,
    "android_compose_expressive_components_indeterminatelinearwavyindicator" to PreviewRecording(
        listOf(PreviewStep.Wait(3_000))
    ),
    "android_compose_expressive_components_determinatecircularwavyindicator" to DeterminateWavySliderCycle,
    "android_compose_expressive_components_indeterminatecircularwavyindicator" to PreviewRecording(
        listOf(PreviewStep.Wait(3_000))
    ),
    "android_compose_expressive_components_indeterminatelinearindicator" to PreviewRecording(
        listOf(PreviewStep.Wait(1_800))
    ),
    "android_compose_expressive_components_indeterminatecircularindicator" to PreviewRecording(
        listOf(PreviewStep.Wait(2_664))
    ),
    // Material 3 Expressive scrollable top app bars (drag starts below the expanded bar).
    "android_compose_expressive_components_alwaysentertopappbar" to verticalScrollCycle(
        bottomYFraction = 0.80f,
        topYFraction = 0.35f,
        dragMillis = 800,
    ),
    "android_compose_expressive_components_exituntilcollapsedtopappbar" to verticalScrollCycle(
        bottomYFraction = 0.85f,
        topYFraction = 0.40f,
        dragMillis = 900,
    ),
    "android_compose_expressive_components_exituntilcollapsedlargetopappbar" to verticalScrollCycle(
        bottomYFraction = 0.85f,
        topYFraction = 0.45f,
        dragMillis = 900,
    ),
    // Material 3 Expressive floating toolbars (drag avoids the bottom/right floating toolbar).
    "android_compose_expressive_components_expandablehorizontalfloatingtoolbar" to verticalScrollCycle(
        bottomYFraction = 0.70f,
        topYFraction = 0.25f,
        dragMillis = 800,
    ),
    "android_compose_expressive_components_expandableverticalfloatingtoolbar" to verticalScrollCycle(
        xFraction = 0.35f,
        bottomYFraction = 0.75f,
        topYFraction = 0.25f,
        dragMillis = 800,
    ),
    "android_compose_expressive_components_scrollablehorizontalfloatingtoolbar" to verticalScrollCycle(
        bottomYFraction = 0.70f,
        topYFraction = 0.25f,
        dragMillis = 800,
    ),
)
