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
 * Central registry of all Jetpack Compose snippets available in the WebAssembly interactive runner,
 * keyed directly by their canonical `[START <region_tag>]` identifier.
 *
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

    fun getById(id: String): ComponentSnippet? {
        val trimmed = id.trim()
        return allSnippets.find { it.id.equals(trimmed, ignoreCase = true) }
    }

    fun getByCategory(category: ComponentCategory): List<ComponentSnippet> {
        return allSnippets.filter { it.category == category }
    }
}
