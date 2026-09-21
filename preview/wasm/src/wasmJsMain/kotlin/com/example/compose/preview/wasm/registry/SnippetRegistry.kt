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

package com.example.compose.preview.wasm.registry

import com.example.compose.preview.wasm.model.ComponentCategory
import com.example.compose.preview.wasm.model.ComponentSnippet

/**
 * Central registry of all Jetpack Compose snippets available in the WebAssembly interactive runner.
 * Aggregates snippets across categorized providers:
 * - [ButtonSnippets]: Buttons, Segmented Buttons, Floating Action Buttons
 * - [ContainmentSnippets]: Cards, Dialogs, Bottom Sheets, Dividers, Scaffolds
 * - [NavigationSnippets]: App Bars, Navigation Bars, Navigation Rails, Drawers
 * - [SelectionSnippets]: Checkboxes, Switches, Sliders, Chips, Pickers
 * - [FeedbackSnippets]: Badges, Progress Indicators, Tooltips
 * - [ListsMenusSnippets]: Search Bars, Menus, Carousels, Swipe to Dismiss
 */
object SnippetRegistry {
    val allSnippets: List<ComponentSnippet> =
        ButtonSnippets.snippets +
        ContainmentSnippets.snippets +
        NavigationSnippets.snippets +
        SelectionSnippets.snippets +
        FeedbackSnippets.snippets +
        ListsMenusSnippets.snippets

    private val aliasMap = mapOf(
        "button" to "filled-button",
        "button-examples" to "filled-button",
        "fab" to "floating-action-button",
        "card" to "card-examples",
        "dialog" to "dialog-examples",
        "menu" to "menu-examples",
        "slider" to "slider-examples",
        "switch" to "switch-examples",
        "tooltip" to "tooltip-examples",
        "badge" to "badge-examples",
        "checkbox" to "checkbox-examples",
        "chip" to "chip-examples",
        "progress" to "progress-indicator",
        "app-bar" to "app-bar-examples",
        "navigation" to "navigation-examples",
        "swipe-to-dismiss-box" to "swipe-to-dismiss"
    )

    fun getById(id: String): ComponentSnippet? {
        val trimmed = id.trim().lowercase()
        // 1. Direct exact match
        allSnippets.find { it.id.equals(trimmed, ignoreCase = true) }?.let { return it }
        // 2. Alias mapping
        aliasMap[trimmed]?.let { targetId ->
            allSnippets.find { it.id.equals(targetId, ignoreCase = true) }?.let { return it }
        }
        // 3. Normalized alphanumeric match
        val normalized = trimmed.replace("-", "").replace("_", "")
        allSnippets.find { it.id.replace("-", "").replace("_", "").equals(normalized, ignoreCase = true) }?.let { return it }
        // 4. Prefix match
        return allSnippets.find { it.id.startsWith(trimmed, ignoreCase = true) || trimmed.startsWith(it.id, ignoreCase = true) }
    }

    fun getByCategory(category: ComponentCategory): List<ComponentSnippet> {
        return allSnippets.filter { it.category == category }
    }
}
