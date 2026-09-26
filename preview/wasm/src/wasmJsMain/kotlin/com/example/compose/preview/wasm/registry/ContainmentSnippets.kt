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

import com.example.compose.snippets.components.AppIcons

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.compose.preview.wasm.model.ComponentCategory
import com.example.compose.preview.wasm.model.ComponentSnippet
import com.example.compose.snippets.components.*

/**
 * Snippet definitions for Cards, Dialogs, Bottom Sheets, Dividers, and Scaffolds.
 */
object ContainmentSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Containment: Bottom Sheet
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_partialbottomsheet",
            title = "Partial Bottom Sheet",
            category = ComponentCategory.CONTAINMENT,
            description = "Modal bottom sheet that anchors to a partial height before expanding.",
            tags = listOf("bottom-sheet", "modal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { PartialBottomSheet() } }
        ),
        // ==========================================
        // Containment: Cards
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_filledcard",
            title = "Filled Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Filled card with container color distinguishing it from the background.",
            tags = listOf("card", "filled", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { FilledCardExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_elevatedcard",
            title = "Elevated Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Card with elevation shadow providing visual separation.",
            tags = listOf("card", "elevated", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { ElevatedCardExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_outlinedcard",
            title = "Outlined Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Card with a subtle outline border for clean surface grouping.",
            tags = listOf("card", "outlined", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { OutlinedCardExample() } }
        ),
        // ==========================================
        // Containment: Carousel
        // ==========================================
        ComponentSnippet(
            id = "android_compose_carousel_multi_browse_basic",
            title = "Multi-Browse Carousel",
            category = ComponentCategory.CONTAINMENT,
            description = "Multi-browse carousel displaying multiple items with peek previews.",
            tags = listOf("carousel", "containment", "lists"),
            codeSnippet = "",
            composable = { CenteredBox { CarouselExample_MultiBrowse() } }
        ),
        ComponentSnippet(
            id = "android_compose_carousel_uncontained_basic",
            title = "Uncontained Carousel",
            category = ComponentCategory.CONTAINMENT,
            description = "Uncontained carousel allowing items to scroll freely past edge boundaries.",
            tags = listOf("carousel", "uncontained", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { CarouselExample() } }
        ),
        // ==========================================
        // Containment: Dialogs
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_alertdialog",
            title = "Alert Dialog",
            category = ComponentCategory.CONTAINMENT,
            description = "Alert dialog with title, text, icon, and confirm/dismiss buttons.",
            tags = listOf("dialog", "alert", "modal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { AlertDialogExample(onDismissRequest = {}, onConfirmation = {}, dialogTitle = "Alert", dialogText = "Example dialog message", icon = AppIcons.Info) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_minimaldialog",
            title = "Minimal Dialog",
            category = ComponentCategory.CONTAINMENT,
            description = "Minimal custom dialog without pre-styled buttons.",
            tags = listOf("dialog", "minimal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { MinimalDialog(onDismissRequest = {}) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_dialogwithimage",
            title = "Dialog with Image",
            category = ComponentCategory.CONTAINMENT,
            description = "Dialog featuring an illustration or header image above content.",
            tags = listOf("dialog", "image", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { DialogExamples() } }
        ),
        // ==========================================
        // Containment: Dividers
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_horizontaldivider",
            title = "Horizontal Divider",
            category = ComponentCategory.CONTAINMENT,
            description = "Horizontal thin line separating content sections.",
            tags = listOf("divider", "horizontal", "separator"),
            codeSnippet = "",
            composable = { CenteredBox { HorizontalDividerExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_verticaldivider",
            title = "Vertical Divider",
            category = ComponentCategory.CONTAINMENT,
            description = "Vertical thin line separating adjacent elements in a row.",
            tags = listOf("divider", "vertical", "separator"),
            codeSnippet = "",
            composable = { CenteredBox { VerticalDividerExample() } }
        ),
        // ==========================================
        // Containment: Scaffold
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_scaffold",
            title = "Scaffold",
            category = ComponentCategory.CONTAINMENT,
            description = "Fundamental layout structure providing slots for top bar, bottom bar, FAB, and content.",
            tags = listOf("scaffold", "layout", "structure", "containment"),
            codeSnippet = "",
            composable = { ScaffoldExample() }
        ),
    )
}
