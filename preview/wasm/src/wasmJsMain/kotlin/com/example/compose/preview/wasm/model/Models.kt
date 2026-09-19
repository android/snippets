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

package com.example.compose.preview.wasm.model

import androidx.compose.runtime.Composable
import com.example.compose.preview.wasm.registry.SnippetCodeMap

enum class ComponentCategory(val id: String, val displayName: String, val description: String) {
    BUTTONS("buttons", "Buttons & Actions", "Buttons, Floating Action Buttons, and Segmented Buttons"),
    CONTAINMENT("containment", "Containment & Sheets", "Cards, Dialogs, Bottom Sheets, Dividers, and Scaffolds"),
    NAVIGATION("navigation", "Navigation", "App Bars, Navigation Bars, Navigation Rails, and Drawers"),
    SELECTION("selection", "Selection & Inputs", "Checkboxes, Switches, Sliders, Chips, and Pickers"),
    FEEDBACK("feedback", "Feedback & Communication", "Badges, Progress Indicators, and Tooltips"),
    LISTS_MENUS("lists-menus", "Lists & Menus", "Search Bars, Menus, Carousels, and Swipe-to-Dismiss")
}

data class ComponentSnippet(
    val id: String,
    val title: String,
    val category: ComponentCategory,
    val description: String,
    val tags: List<String>,
    val codeSnippet: String = "",
    val composable: @Composable () -> Unit
) {
    val actualCode: String
        get() = if (codeSnippet.isNotBlank()) codeSnippet else SnippetCodeMap.getCode(id)
}
