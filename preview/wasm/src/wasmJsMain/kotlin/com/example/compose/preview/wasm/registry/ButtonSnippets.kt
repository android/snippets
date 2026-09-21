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
 * Snippet definitions for Buttons, Floating Action Buttons, Icon Buttons, and Segmented Buttons.
 */
object ButtonSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Actions: Segmented Buttons
        // ==========================================
        ComponentSnippet(
            id = "single-choice-segmented-button",
            title = "Single-Choice Segmented Button",
            category = ComponentCategory.BUTTONS,
            description = "SingleChoiceSegmentedButtonRow allowing users to select a single option from a set of mutually exclusive choices.",
            tags = listOf("segmented", "button", "single-choice", "action"),
            codeSnippet = "",
            composable = { CenteredBox { SingleChoiceSegmentedButton() } }
        ),
        ComponentSnippet(
            id = "multi-choice-segmented-button",
            title = "Multi-Choice Segmented Button",
            category = ComponentCategory.BUTTONS,
            description = "MultiChoiceSegmentedButtonRow allowing users to select multiple options simultaneously with icon feedback.",
            tags = listOf("segmented", "button", "multi-choice", "action"),
            codeSnippet = "",
            composable = { CenteredBox { MultiChoiceSegmentedButton() } }
        ),
        ComponentSnippet(
            id = "segmented-button",
            title = "Segmented Button",
            category = ComponentCategory.BUTTONS,
            description = "Segmented buttons for single-choice and multi-choice selections.",
            tags = listOf("segmented", "button", "action"),
            codeSnippet = "",
            composable = { CenteredBox { SegmentedButtonExamples() } }
        ),
        // ==========================================
        // Actions: Buttons
        // ==========================================
        ComponentSnippet(
            id = "filled-button",
            title = "Filled Button",
            category = ComponentCategory.BUTTONS,
            description = "High-emphasis button used for the primary action on a screen.",
            tags = listOf("button", "filled", "primary", "action"),
            codeSnippet = "",
            composable = { CenteredBox { FilledButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "filled-tonal-button",
            title = "Filled Tonal Button",
            category = ComponentCategory.BUTTONS,
            description = "Medium-high emphasis button used for secondary actions requiring visual prominence.",
            tags = listOf("button", "tonal", "action"),
            codeSnippet = "",
            composable = { CenteredBox { FilledTonalButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "elevated-button",
            title = "Elevated Button",
            category = ComponentCategory.BUTTONS,
            description = "Elevated button with a shadow used on patterned or complex backgrounds.",
            tags = listOf("button", "elevated", "shadow", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ElevatedButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "outlined-button",
            title = "Outlined Button",
            category = ComponentCategory.BUTTONS,
            description = "Medium-emphasis button with a stroke outline for secondary, non-destructive actions.",
            tags = listOf("button", "outlined", "action"),
            codeSnippet = "",
            composable = { CenteredBox { OutlinedButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "text-button",
            title = "Text Button",
            category = ComponentCategory.BUTTONS,
            description = "Low-emphasis button without container borders, ideal for cards and dialogs.",
            tags = listOf("button", "text", "low-emphasis", "action"),
            codeSnippet = "",
            composable = { CenteredBox { TextButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "button-examples",
            title = "Button",
            category = ComponentCategory.BUTTONS,
            description = "All Material 3 button types: Filled, Filled Tonal, Elevated, Outlined, and Text.",
            tags = listOf("button", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ButtonExamples() } }
        ),
        ComponentSnippet(
            id = "button-with-animated-shape",
            title = "Button with Animated Shape",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive button with animated shape change on interaction.",
            tags = listOf("button", "expressive", "animated", "shape", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ButtonWithAnimatedShapeExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "square-button",
            title = "Square Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive square button with rounded corners.",
            tags = listOf("button", "expressive", "square", "action"),
            codeSnippet = "",
            composable = { CenteredBox { SquareButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "button-with-icon",
            title = "Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive button with icon and medium container height.",
            tags = listOf("button", "expressive", "icon", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "split-button",
            title = "Split Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive split button layout with leading primary action and trailing toggle.",
            tags = listOf("button", "split", "expressive", "action"),
            codeSnippet = "",
            composable = { CenteredBox { SplitButtonExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "button-group",
            title = "Connected Button Group",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive connected button group with toggle buttons.",
            tags = listOf("button", "group", "toggle", "expressive", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ButtonGroupExample() } }
        ),
        ComponentSnippet(
            id = "toggle-button",
            title = "Toggle Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive ToggleButton with selected and unselected states.",
            tags = listOf("button", "toggle", "expressive", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ToggleButtonSample() } }
        ),
        ComponentSnippet(
            id = "elevated-toggle-button",
            title = "Elevated Toggle Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive ElevatedToggleButton with container elevation.",
            tags = listOf("button", "toggle", "elevated", "expressive"),
            codeSnippet = "",
            composable = { CenteredBox { ElevatedToggleButtonSample() } }
        ),
        ComponentSnippet(
            id = "tonal-toggle-button",
            title = "Tonal Toggle Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive TonalToggleButton with secondary tonal container.",
            tags = listOf("button", "toggle", "tonal", "expressive"),
            codeSnippet = "",
            composable = { CenteredBox { TonalToggleButtonSample() } }
        ),
        ComponentSnippet(
            id = "outlined-toggle-button",
            title = "Outlined Toggle Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive OutlinedToggleButton with border styling.",
            tags = listOf("button", "toggle", "outlined", "expressive"),
            codeSnippet = "",
            composable = { CenteredBox { OutlinedToggleButtonSample() } }
        ),
        // ==========================================
        // Actions: Floating Action Button
        // ==========================================
        ComponentSnippet(
            id = "fab",
            title = "Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "Standard 56dp Floating Action Button performing the primary screen action.",
            tags = listOf("fab", "floating", "action"),
            codeSnippet = "",
            composable = { CenteredBox { Example(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "floating-action-button",
            title = "Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "Standard 56dp Floating Action Button performing the primary screen action.",
            tags = listOf("fab", "floating", "action"),
            codeSnippet = "",
            composable = { CenteredBox { FloatingActionButtonExamples() } }
        ),
        ComponentSnippet(
            id = "extended-fab",
            title = "Extended Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "Extended FAB combining an icon and descriptive text label.",
            tags = listOf("fab", "extended", "floating", "action"),
            codeSnippet = "",
            composable = { CenteredBox { ExtendedExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "floating-toolbar",
            title = "Floating Toolbar",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive horizontal floating toolbar with action buttons and FAB.",
            tags = listOf("toolbar", "floating", "expressive", "fab", "action"),
            codeSnippet = "",
            composable = { CenteredBox { FloatingToolbarExample() } }
        ),
        ComponentSnippet(
            id = "small-fab",
            title = "Small Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "Compact FAB for secondary actions or compact viewports.",
            tags = listOf("fab", "small", "action"),
            codeSnippet = "",
            composable = { CenteredBox { SmallExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "large-fab",
            title = "Large Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "96dp Large FAB for high prominence on large screens and foldables.",
            tags = listOf("fab", "large", "action"),
            codeSnippet = "",
            composable = { CenteredBox { LargeExample(onClick = {}) } }
        ),
        ComponentSnippet(
            id = "theme-builder",
            title = "Theme Builder / Material 3 Catalog",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Component Catalog showcase modeled after AndroidX Components.kt, previewing dynamic M3 color roles, tokens, and components.",
            tags = listOf("theme", "builder", "material", "catalog", "tokens", "components"),
            codeSnippet = "",
            composable = { ThemeBuilderPreview() }
        ),
        ComponentSnippet(
            id = "button-with-icon-sample",
            title = "Button with Icon Sample",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive button with icon and medium container height.",
            tags = listOf("button", "expressive", "icon", "action"),
            composable = { CenteredBox { ButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "toggle-button-with-icon",
            title = "Toggle Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive toggle button featuring leading icon and checked state.",
            tags = listOf("button", "toggle", "icon", "action"),
            composable = { CenteredBox { ToggleButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "xsmall-button-with-icon",
            title = "Extra Small Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive extra-small button with icon.",
            tags = listOf("button", "xsmall", "icon", "action"),
            composable = { CenteredBox { XSmallButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "xsmall-toggle-button-with-icon",
            title = "Extra Small Toggle Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive extra-small toggle button with icon.",
            tags = listOf("button", "toggle", "xsmall", "icon"),
            composable = { CenteredBox { XSmallToggleButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "medium-button-with-icon",
            title = "Medium Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive medium button with leading icon.",
            tags = listOf("button", "medium", "icon", "action"),
            composable = { CenteredBox { MediumButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "medium-toggle-button-with-icon",
            title = "Medium Toggle Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive medium toggle button with leading icon.",
            tags = listOf("button", "toggle", "medium", "icon"),
            composable = { CenteredBox { MediumToggleButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "large-button-with-icon",
            title = "Large Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive large button with leading icon.",
            tags = listOf("button", "large", "icon", "action"),
            composable = { CenteredBox { LargeButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "large-toggle-button-with-icon",
            title = "Large Toggle Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive large toggle button with leading icon.",
            tags = listOf("button", "toggle", "large", "icon"),
            composable = { CenteredBox { LargeToggleButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "xlarge-button-with-icon",
            title = "Extra Large Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive extra-large button with prominent leading icon.",
            tags = listOf("button", "xlarge", "icon", "action"),
            composable = { CenteredBox { XLargeButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "xlarge-toggle-button-with-icon",
            title = "Extra Large Toggle Button with Icon",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive extra-large toggle button with icon.",
            tags = listOf("button", "toggle", "xlarge", "icon"),
            composable = { CenteredBox { XLargeToggleButtonWithIconSample() } }
        ),
        ComponentSnippet(
            id = "square-toggle-button",
            title = "Square Toggle Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive square toggle button shape variant.",
            tags = listOf("button", "square", "toggle", "action"),
            composable = { CenteredBox { SquareToggleButtonSample() } }
        ),
        ComponentSnippet(
            id = "medium-fab",
            title = "Medium Floating Action Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive medium floating action button.",
            tags = listOf("fab", "medium", "floating", "action"),
            composable = { CenteredBox { MediumFloatingActionButtonSample() } }
        ),
        ComponentSnippet(
            id = "icon-button",
            title = "Icon Button",
            category = ComponentCategory.BUTTONS,
            description = "Compact icon button for actionable icons.",
            tags = listOf("icon-button", "button", "action"),
            composable = { CenteredBox { ToggleIconButtonExample() } }
        ),
        ComponentSnippet(
            id = "toggle-icon-button",
            title = "Toggle Icon Button",
            category = ComponentCategory.BUTTONS,
            description = "Interactive toggle icon button with selected/unselected state.",
            tags = listOf("icon-button", "toggle", "action"),
            composable = { CenteredBox { ToggleIconButtonExample() } }
        ),
        ComponentSnippet(
            id = "momentary-icon-button",
            title = "Momentary Icon Button",
            category = ComponentCategory.BUTTONS,
            description = "Momentary icon button supporting continuous stepped action while pressed.",
            tags = listOf("icon-button", "momentary", "action"),
            composable = { CenteredBox { MomentaryIconButtonExample() } }
        ),
        ComponentSnippet(
            id = "animated-icon-button",
            title = "Animated Icon Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive icon button with animated shape transitions.",
            tags = listOf("icon-button", "animated", "shape", "expressive"),
            composable = { CenteredBox { IconButtonWithAnimatedShapeSample() } }
        ),
        ComponentSnippet(
            id = "animated-toggle-icon-button",
            title = "Animated Toggle Icon Button",
            category = ComponentCategory.BUTTONS,
            description = "Material 3 Expressive toggle icon button with animated morphing shape.",
            tags = listOf("icon-button", "toggle", "animated", "shape"),
            composable = { CenteredBox { IconToggleButtonWithAnimatedShapeSample() } }
        )
    )
}
