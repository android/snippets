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
 * Snippet definitions for Search Bars, Menus, Carousels, and Swipe-to-Dismiss.
 */
object ListsMenusSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Search & Lists
        // ==========================================
        ComponentSnippet(
            id = "search-bar",
            title = "Search Bar",
            category = ComponentCategory.LISTS_MENUS,
            description = "Material 3 SearchBar with search input and expandable suggestions.",
            tags = listOf("search-bar", "search", "query"),
            codeSnippet = "",
            composable = { CenteredBox { SearchBarExamples() } }
        ),
        ComponentSnippet(
            id = "docked-search-bar",
            title = "Docked Search Bar",
            category = ComponentCategory.LISTS_MENUS,
            description = "Docked search bar anchored to top of screen.",
            tags = listOf("search-bar", "docked", "search"),
            codeSnippet = "",
            composable = { CenteredBox { CustomizableSearchBarExample() } }
        ),
        ComponentSnippet(
            id = "swipe-to-dismiss",
            title = "Swipe to Dismiss",
            category = ComponentCategory.LISTS_MENUS,
            description = "SwipeToDismissBox allowing list items to be dismissed with swipe gestures.",
            tags = listOf("swipe", "dismiss", "list"),
            codeSnippet = "",
            composable = { CenteredBox { SwipeToDismissBoxExamples() } }
        ),
        ComponentSnippet(
            id = "grouped-menu",
            title = "Grouped Menu",
            category = ComponentCategory.LISTS_MENUS,
            description = "Material 3 Expressive grouped dropdown menu with categorized items and dividers.",
            tags = listOf("menu", "grouped", "selection"),
            composable = { CenteredBox { GroupedMenuSample() } }
        ),
        ComponentSnippet(
            id = "search-bar-simple",
            title = "Simple Search Bar",
            category = ComponentCategory.LISTS_MENUS,
            description = "Simple standalone search bar with search and more action icons.",
            tags = listOf("search", "query", "inputs"),
            composable = { CenteredBox { SearchBarExamples() } }
        ),
        ComponentSnippet(
            id = "swipe-to-dismiss-item",
            title = "Swipe to Dismiss Item",
            category = ComponentCategory.LISTS_MENUS,
            description = "Swipe to dismiss item row with background color transition on swipe gestures.",
            tags = listOf("swipe", "dismiss", "list"),
            composable = { CenteredBox { SwipeToDismissBoxExamples() } }
        )
    )
}
