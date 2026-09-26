/*
 * Copyright 2024 The Android Open Source Project
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

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.compose.preview.wasm.model.ComponentCategory
import com.example.compose.preview.wasm.model.ComponentSnippet
import com.example.compose.snippets.components.*

/**
 * Snippet definitions for Badges, Progress Indicators, and Tooltips.
 */
object FeedbackSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Communication: Badges
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_badge",
            title = "Badge",
            category = ComponentCategory.FEEDBACK,
            description = "Small status and count indicator displayed on top of icons or labels.",
            tags = listOf("badge", "indicator", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { BadgeExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_badgeinteractive",
            title = "Interactive Badge",
            category = ComponentCategory.FEEDBACK,
            description = "BadgedBox with dynamic numeric count incremented on user interaction.",
            tags = listOf("badge", "interactive", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { BadgeInteractiveExample() } }
        ),
        ComponentSnippet(
            id = "badge-examples",
            title = "Badges",
            category = ComponentCategory.FEEDBACK,
            description = "Badge and interactive BadgedBox examples.",
            tags = listOf("badge", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { BadgeExamples() } }
        ),
        // ==========================================
        // Communication: Progress Indicators
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_indeterminateindicator",
            title = "Indeterminate Progress Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Continuous circular progress indicator showing indeterminate loading.",
            tags = listOf("progress", "circular", "indeterminate", "loading"),
            codeSnippet = "",
            composable = { CenteredBox { IndeterminateCircularIndicator() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_determinateindicator",
            title = "Determinate Progress Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Linear progress indicator showing specific progress toward task completion.",
            tags = listOf("progress", "linear", "determinate"),
            codeSnippet = "",
            composable = { CenteredBox { LinearDeterminateIndicator() } }
        ),
        ComponentSnippet(
            id = "progress-indicator",
            title = "Progress Indicators",
            category = ComponentCategory.FEEDBACK,
            description = "Circular and linear progress indicators.",
            tags = listOf("progress", "loading"),
            codeSnippet = "",
            composable = { CenteredBox { ProgressIndicatorExamples() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_loadingindicator",
            title = "Loading Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive loading indicator with indeterminate animation.",
            tags = listOf("loading", "progress", "expressive", "indicator", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { LoadingIndicatorExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_containedloadingindicator",
            title = "Contained Loading Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive contained loading indicator on elevated container.",
            tags = listOf("loading", "progress", "expressive", "contained", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { ContainedLoadingIndicatorExample() } }
        ),
        ComponentSnippet(
            id = "linear-wavy-progress-indicator",
            title = "Linear Wavy Progress Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive linear wavy progress indicator.",
            tags = listOf("progress", "wavy", "linear", "expressive", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { LinearWavyProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "circular-wavy-progress-indicator",
            title = "Circular Wavy Progress Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive circular wavy progress indicator.",
            tags = listOf("progress", "wavy", "circular", "expressive", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { CircularWavyProgressIndicatorSample() } }
        ),
        // ==========================================
        // Communication: Tooltips
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_plaintooltipexample",
            title = "Plain Tooltip",
            category = ComponentCategory.FEEDBACK,
            description = "Plain tooltip displaying a short text label on long-press or hover.",
            tags = listOf("tooltip", "plain", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { PlainTooltipExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_richtooltipexample",
            title = "Rich Tooltip",
            category = ComponentCategory.FEEDBACK,
            description = "Rich tooltip with title, body text, and optional action buttons.",
            tags = listOf("tooltip", "rich", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { RichTooltipExample() } }
        ),
        ComponentSnippet(
            id = "tooltip-examples",
            title = "Tooltips",
            category = ComponentCategory.FEEDBACK,
            description = "Plain and rich tooltips.",
            tags = listOf("tooltip", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { TooltipExamples() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_pull_to_refresh_basic",
            title = "Pull to Refresh",
            category = ComponentCategory.FEEDBACK,
            description = "PullToRefreshBox providing swipe-to-refresh behavior with indicator.",
            tags = listOf("pull-to-refresh", "refresh", "indicator", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { PullToRefreshBasicPreview() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_determinatelinearwavyindicator",
            title = "Determinate Linear Wavy Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive linear wavy progress indicator showing determinate progress.",
            tags = listOf("progress", "wavy", "linear", "expressive"),
            composable = { CenteredBox { LinearWavyProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_indeterminatelinearwavyindicator",
            title = "Indeterminate Linear Wavy Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive linear wavy progress indicator with continuous animation.",
            tags = listOf("progress", "wavy", "linear", "indeterminate"),
            composable = { CenteredBox { IndeterminateLinearWavyProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_determinatecircularwavyindicator",
            title = "Determinate Circular Wavy Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive circular wavy progress indicator displaying determinate progress.",
            tags = listOf("progress", "wavy", "circular", "expressive"),
            composable = { CenteredBox { CircularWavyProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_indeterminatecircularwavyindicator",
            title = "Indeterminate Circular Wavy Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive circular wavy progress indicator with continuous spinning wave animation.",
            tags = listOf("progress", "wavy", "circular", "indeterminate"),
            composable = { CenteredBox { IndeterminateCircularWavyProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_determinatelinearindicator",
            title = "Determinate Linear Expressive Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive linear progress indicator with modern styling.",
            tags = listOf("progress", "linear", "expressive"),
            composable = { CenteredBox { LinearProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_indeterminatelinearindicator",
            title = "Indeterminate Linear Expressive Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive indeterminate linear progress bar.",
            tags = listOf("progress", "linear", "indeterminate", "expressive"),
            composable = { CenteredBox { IndeterminateLinearProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_determinatecircularindicator",
            title = "Determinate Circular Expressive Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive circular progress indicator with determinate value.",
            tags = listOf("progress", "circular", "expressive"),
            composable = { CenteredBox { CircularProgressIndicatorSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_indeterminatecircularindicator",
            title = "Indeterminate Circular Expressive Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive circular progress indicator with continuous rotation.",
            tags = listOf("progress", "circular", "indeterminate", "expressive"),
            composable = { CenteredBox { IndeterminateCircularProgressIndicatorSample() } }
        )
    )
}
