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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.preview.wasm.model.ComponentCategory
import com.example.compose.preview.wasm.model.ComponentSnippet
import com.example.compose.snippets.components.*

object SnippetRegistry {
    val allSnippets: List<ComponentSnippet> = listOf(
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

        // ==========================================
        // Communication: Badges
        // ==========================================
        ComponentSnippet(
            id = "badge",
            title = "Badge",
            category = ComponentCategory.FEEDBACK,
            description = "Small status and count indicator displayed on top of icons or labels.",
            tags = listOf("badge", "indicator", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { BadgeExample() } }
        ),
        ComponentSnippet(
            id = "badge-interactive",
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
            id = "indeterminate-progress-indicator",
            title = "Indeterminate Progress Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Continuous circular progress indicator showing indeterminate loading.",
            tags = listOf("progress", "circular", "indeterminate", "loading"),
            codeSnippet = "",
            composable = { CenteredBox { IndeterminateCircularIndicator() } }
        ),
        ComponentSnippet(
            id = "determinate-progress-indicator",
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
            id = "loading-indicator",
            title = "Loading Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive loading indicator with indeterminate animation.",
            tags = listOf("loading", "progress", "expressive", "indicator", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { LoadingIndicatorExample() } }
        ),
        ComponentSnippet(
            id = "contained-loading-indicator",
            title = "Contained Loading Indicator",
            category = ComponentCategory.FEEDBACK,
            description = "Material 3 Expressive contained loading indicator on elevated container.",
            tags = listOf("loading", "progress", "expressive", "contained", "feedback"),
            codeSnippet = "",
            composable = { CenteredBox { ContainedLoadingIndicatorExample() } }
        ),

        // ==========================================
        // Communication: Tooltips
        // ==========================================
        ComponentSnippet(
            id = "plain-tooltip",
            title = "Plain Tooltip",
            category = ComponentCategory.FEEDBACK,
            description = "Plain tooltip displaying a short text label on long-press or hover.",
            tags = listOf("tooltip", "plain", "communication"),
            codeSnippet = "",
            composable = { CenteredBox { PlainTooltipExample() } }
        ),
        ComponentSnippet(
            id = "rich-tooltip",
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

        // ==========================================
        // Containment: Bottom Sheet
        // ==========================================
        ComponentSnippet(
            id = "partial-bottom-sheet",
            title = "Partial Bottom Sheet",
            category = ComponentCategory.CONTAINMENT,
            description = "Modal bottom sheet that anchors to a partial height before expanding.",
            tags = listOf("bottom-sheet", "modal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { PartialBottomSheet() } }
        ),
        ComponentSnippet(
            id = "bottom-sheet",
            title = "Bottom Sheet",
            category = ComponentCategory.CONTAINMENT,
            description = "Modal bottom sheet examples.",
            tags = listOf("bottom-sheet", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { PartialBottomSheet() } }
        ),

        // ==========================================
        // Containment: Cards
        // ==========================================
        ComponentSnippet(
            id = "filled-card",
            title = "Filled Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Filled card with container color distinguishing it from the background.",
            tags = listOf("card", "filled", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { FilledCardExample() } }
        ),
        ComponentSnippet(
            id = "elevated-card",
            title = "Elevated Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Card with elevation shadow providing visual separation.",
            tags = listOf("card", "elevated", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { ElevatedCardExample() } }
        ),
        ComponentSnippet(
            id = "outlined-card",
            title = "Outlined Card",
            category = ComponentCategory.CONTAINMENT,
            description = "Card with a subtle outline border for clean surface grouping.",
            tags = listOf("card", "outlined", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { OutlinedCardExample() } }
        ),
        ComponentSnippet(
            id = "card-examples",
            title = "Cards",
            category = ComponentCategory.CONTAINMENT,
            description = "Filled, elevated, and outlined cards.",
            tags = listOf("card", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { CardExamples() } }
        ),

        // ==========================================
        // Containment: Carousel
        // ==========================================
        ComponentSnippet(
            id = "multi-browse-carousel",
            title = "Multi-Browse Carousel",
            category = ComponentCategory.CONTAINMENT,
            description = "Multi-browse carousel displaying multiple items with peek previews.",
            tags = listOf("carousel", "containment", "lists"),
            codeSnippet = "",
            composable = { CenteredBox { CarouselExample_MultiBrowse() } }
        ),
        ComponentSnippet(
            id = "uncontained-carousel",
            title = "Uncontained Carousel",
            category = ComponentCategory.CONTAINMENT,
            description = "Uncontained carousel allowing items to scroll freely past edge boundaries.",
            tags = listOf("carousel", "uncontained", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { CarouselExample() } }
        ),
        ComponentSnippet(
            id = "carousel-examples",
            title = "Carousel",
            category = ComponentCategory.CONTAINMENT,
            description = "Multi-browse and uncontained carousel examples.",
            tags = listOf("carousel", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { CarouselExamples() } }
        ),

        // ==========================================
        // Containment: Dialogs
        // ==========================================
        ComponentSnippet(
            id = "alert-dialog",
            title = "Alert Dialog",
            category = ComponentCategory.CONTAINMENT,
            description = "Alert dialog with title, text, icon, and confirm/dismiss buttons.",
            tags = listOf("dialog", "alert", "modal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { AlertDialogExample(onDismissRequest = {}, onConfirmation = {}, dialogTitle = "Alert", dialogText = "Example dialog message", icon = Icons.Filled.Info) } }
        ),
        ComponentSnippet(
            id = "minimal-dialog",
            title = "Minimal Dialog",
            category = ComponentCategory.CONTAINMENT,
            description = "Minimal custom dialog without pre-styled buttons.",
            tags = listOf("dialog", "minimal", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { MinimalDialog(onDismissRequest = {}) } }
        ),
        ComponentSnippet(
            id = "dialog-with-image",
            title = "Dialog with Image",
            category = ComponentCategory.CONTAINMENT,
            description = "Dialog featuring an illustration or header image above content.",
            tags = listOf("dialog", "image", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { DialogExamples() } }
        ),
        ComponentSnippet(
            id = "dialog-examples",
            title = "Dialogs",
            category = ComponentCategory.CONTAINMENT,
            description = "Alert, minimal, and image dialogs.",
            tags = listOf("dialog", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { DialogExamples() } }
        ),

        // ==========================================
        // Containment: Dividers
        // ==========================================
        ComponentSnippet(
            id = "horizontal-divider",
            title = "Horizontal Divider",
            category = ComponentCategory.CONTAINMENT,
            description = "Horizontal thin line separating content sections.",
            tags = listOf("divider", "horizontal", "separator"),
            codeSnippet = "",
            composable = { CenteredBox { HorizontalDividerExample() } }
        ),
        ComponentSnippet(
            id = "vertical-divider",
            title = "Vertical Divider",
            category = ComponentCategory.CONTAINMENT,
            description = "Vertical thin line separating adjacent elements in a row.",
            tags = listOf("divider", "vertical", "separator"),
            codeSnippet = "",
            composable = { CenteredBox { VerticalDividerExample() } }
        ),
        ComponentSnippet(
            id = "divider-examples",
            title = "Dividers",
            category = ComponentCategory.CONTAINMENT,
            description = "Horizontal and vertical dividers.",
            tags = listOf("divider", "containment"),
            codeSnippet = "",
            composable = { CenteredBox { DividerExamples() } }
        ),

        // ==========================================
        // Containment: Scaffold
        // ==========================================
        ComponentSnippet(
            id = "scaffold",
            title = "Scaffold",
            category = ComponentCategory.CONTAINMENT,
            description = "Fundamental layout structure providing slots for top bar, bottom bar, FAB, and content.",
            tags = listOf("scaffold", "layout", "structure", "containment"),
            codeSnippet = "",
            composable = { ScaffoldExample() }
        ),
        ComponentSnippet(
            id = "scaffold-example",
            title = "Scaffold",
            category = ComponentCategory.CONTAINMENT,
            description = "Fundamental layout structure providing slots for top bar, bottom bar, FAB, and content.",
            tags = listOf("scaffold", "layout", "structure", "containment"),
            codeSnippet = "",
            composable = { ScaffoldExample() }
        ),

        // ==========================================
        // Navigation: App Bars
        // ==========================================
        ComponentSnippet(
            id = "center-aligned-top-app-bar",
            title = "Center-Aligned Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Top app bar with centered headline title and action icons.",
            tags = listOf("app-bar", "top-bar", "navigation"),
            codeSnippet = "",
            composable = { CenterAlignedTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "small-top-app-bar",
            title = "Small Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Standard compact top app bar with start-aligned title.",
            tags = listOf("app-bar", "top-bar", "navigation"),
            codeSnippet = "",
            composable = { SmallTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "medium-top-app-bar",
            title = "Medium Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Medium top app bar featuring a larger title area that collapses on scroll.",
            tags = listOf("app-bar", "medium", "navigation"),
            codeSnippet = "",
            composable = { MediumTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "large-top-app-bar",
            title = "Large Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Large top app bar with prominent headline typography that collapses on scroll.",
            tags = listOf("app-bar", "large", "navigation"),
            codeSnippet = "",
            composable = { LargeTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "app-bar-examples",
            title = "App Bars",
            category = ComponentCategory.NAVIGATION,
            description = "Center-aligned, small, medium, and large top app bars.",
            tags = listOf("app-bar", "navigation"),
            codeSnippet = "",
            composable = { AppBarExamples(navigateBack = {}) }
        ),

        // ==========================================
        // Navigation: Navigation Bar & Rail
        // ==========================================
        ComponentSnippet(
            id = "navigation-bar",
            title = "Navigation Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Bottom navigation bar providing access to 3 to 5 top-level destinations.",
            tags = listOf("navigation-bar", "bottom-navigation", "navigation"),
            codeSnippet = "",
            composable = { NavigationBarExample() }
        ),
        ComponentSnippet(
            id = "navigation-rail",
            title = "Navigation Rail",
            category = ComponentCategory.NAVIGATION,
            description = "Side navigation rail suited for tablets and wide screens.",
            tags = listOf("navigation-rail", "rail", "navigation"),
            codeSnippet = "",
            composable = { NavigationRailExample() }
        ),
        ComponentSnippet(
            id = "navigation-examples",
            title = "Navigation Bar & Rail",
            category = ComponentCategory.NAVIGATION,
            description = "Navigation bar and navigation rail examples.",
            tags = listOf("navigation"),
            codeSnippet = "",
            composable = { NavigationBarExample() }
        ),

        // ==========================================
        // Navigation: Navigation Drawer
        // ==========================================
        ComponentSnippet(
            id = "navigation-drawer",
            title = "Navigation Drawer",
            category = ComponentCategory.NAVIGATION,
            description = "Modal navigation drawer for navigation destinations on medium and large screens.",
            tags = listOf("navigation-drawer", "drawer", "navigation"),
            codeSnippet = "",
            composable = { NavigationDrawerExamples() }
        ),

        // ==========================================
        // Selection: Checkbox
        // ==========================================
        ComponentSnippet(
            id = "checkbox",
            title = "Checkbox",
            category = ComponentCategory.SELECTION,
            description = "Standard binary checkbox for selecting or deselecting a single item.",
            tags = listOf("checkbox", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { CheckboxMinimalExample() } }
        ),
        ComponentSnippet(
            id = "parent-checkbox",
            title = "Parent Checkbox (Tri-State)",
            category = ComponentCategory.SELECTION,
            description = "TriStateCheckbox controlling multiple child checkboxes.",
            tags = listOf("checkbox", "tri-state", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { CheckboxParentExample() } }
        ),
        ComponentSnippet(
            id = "checkbox-examples",
            title = "Checkbox",
            category = ComponentCategory.SELECTION,
            description = "Checkbox and tri-state parent checkbox examples.",
            tags = listOf("checkbox", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { CheckboxExamples() } }
        ),

        // ==========================================
        // Selection: Chips
        // ==========================================
        ComponentSnippet(
            id = "assist-chip",
            title = "Assist Chip",
            category = ComponentCategory.SELECTION,
            description = "Assist chip triggering an action related to primary content.",
            tags = listOf("chip", "assist", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { AssistChipExample() } }
        ),
        ComponentSnippet(
            id = "filter-chip",
            title = "Filter Chip",
            category = ComponentCategory.SELECTION,
            description = "Filter chip allowing users to filter content by selecting tags.",
            tags = listOf("chip", "filter", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { FilterChipExample() } }
        ),
        ComponentSnippet(
            id = "input-chip",
            title = "Input Chip",
            category = ComponentCategory.SELECTION,
            description = "Input chip representing a complex piece of information like a recipient or tag.",
            tags = listOf("chip", "input", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { InputChipExample(text = "Input Chip", onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "suggestion-chip",
            title = "Suggestion Chip",
            category = ComponentCategory.SELECTION,
            description = "Suggestion chip presenting dynamically generated recommendations.",
            tags = listOf("chip", "suggestion", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SuggestionChipExample() } }
        ),
        ComponentSnippet(
            id = "chip-examples",
            title = "Chips",
            category = ComponentCategory.SELECTION,
            description = "Assist, filter, input, and suggestion chips.",
            tags = listOf("chip", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { ChipExamples() } }
        ),

        // ==========================================
        // Selection: Date Pickers
        // ==========================================
        ComponentSnippet(
            id = "date-picker-modal",
            title = "Modal Date Picker",
            category = ComponentCategory.SELECTION,
            description = "Modal dialog for selecting a single calendar date.",
            tags = listOf("date-picker", "modal", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerModal(onDateSelected = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "date-picker-input-modal",
            title = "Modal Date Input",
            category = ComponentCategory.SELECTION,
            description = "Modal dialog allowing users to enter a date via text input.",
            tags = listOf("date-picker", "input", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerModalInput(onDateSelected = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "date-picker-docked",
            title = "Docked Date Picker",
            category = ComponentCategory.SELECTION,
            description = "Inline docked date picker anchored to a text input field.",
            tags = listOf("date-picker", "docked", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerDocked() } }
        ),
        ComponentSnippet(
            id = "date-range-picker",
            title = "Date Range Picker",
            category = ComponentCategory.SELECTION,
            description = "Modal dialog for selecting a start and end date range.",
            tags = listOf("date-picker", "range", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DateRangePickerModal(onDateRangeSelected = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "date-picker",
            title = "Date Pickers",
            category = ComponentCategory.SELECTION,
            description = "Modal, docked, and range date pickers.",
            tags = listOf("date-picker", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerExamples() } }
        ),

        // ==========================================
        // Selection: Menus
        // ==========================================
        ComponentSnippet(
            id = "minimal-dropdown-menu",
            title = "Dropdown Menu",
            category = ComponentCategory.SELECTION,
            description = "Basic dropdown menu anchored to an icon button.",
            tags = listOf("menu", "dropdown", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { MinimalDropdownMenu() } }
        ),
        ComponentSnippet(
            id = "scrollable-dropdown-menu",
            title = "Scrollable Dropdown Menu",
            category = ComponentCategory.SELECTION,
            description = "Dropdown menu with a long scrollable list of items.",
            tags = listOf("menu", "scrollable", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { LongBasicDropdownMenu() } }
        ),
        ComponentSnippet(
            id = "dropdown-menu-with-details",
            title = "Dropdown Menu with Details",
            category = ComponentCategory.SELECTION,
            description = "Dropdown menu items with leading icons, trailing icons, and shortcuts.",
            tags = listOf("menu", "icons", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DropdownMenuWithDetails() } }
        ),
        ComponentSnippet(
            id = "menu-examples",
            title = "Menus",
            category = ComponentCategory.SELECTION,
            description = "Dropdown menu examples.",
            tags = listOf("menu", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { MenusExamples() } }
        ),

        // ==========================================
        // Selection: Radio Button
        // ==========================================
        ComponentSnippet(
            id = "radio-button",
            title = "Radio Button",
            category = ComponentCategory.SELECTION,
            description = "Radio buttons for mutually exclusive single selections.",
            tags = listOf("radio-button", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { RadioButtonSingleSelection() } }
        ),

        // ==========================================
        // Selection: Sliders
        // ==========================================
        ComponentSnippet(
            id = "continuous-slider",
            title = "Continuous Slider",
            category = ComponentCategory.SELECTION,
            description = "Continuous slider for selecting a numeric value along a bar.",
            tags = listOf("slider", "continuous", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SliderMinimalExample() } }
        ),
        ComponentSnippet(
            id = "discrete-slider",
            title = "Discrete Slider (Steps)",
            category = ComponentCategory.SELECTION,
            description = "Slider with discrete steps and custom thumb.",
            tags = listOf("slider", "discrete", "steps", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SliderAdvancedExample() } }
        ),
        ComponentSnippet(
            id = "range-slider",
            title = "Range Slider",
            category = ComponentCategory.SELECTION,
            description = "Range slider with two thumbs for selecting a min and max value.",
            tags = listOf("slider", "range", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { RangeSliderExample() } }
        ),
        ComponentSnippet(
            id = "slider-examples",
            title = "Sliders",
            category = ComponentCategory.SELECTION,
            description = "Continuous, discrete, and range sliders.",
            tags = listOf("slider", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SliderExamples() } }
        ),

        // ==========================================
        // Selection: Switch
        // ==========================================
        ComponentSnippet(
            id = "minimal-switch",
            title = "Minimal Switch",
            category = ComponentCategory.SELECTION,
            description = "Standard binary toggle switch for turning settings on or off.",
            tags = listOf("switch", "toggle", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SwitchMinimalExample() } }
        ),
        ComponentSnippet(
            id = "switch-with-icon",
            title = "Switch with Icon",
            category = ComponentCategory.SELECTION,
            description = "Switch featuring a custom thumb icon reflecting current state.",
            tags = listOf("switch", "icon", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SwitchWithIconExample() } }
        ),
        ComponentSnippet(
            id = "switch-examples",
            title = "Switch",
            category = ComponentCategory.SELECTION,
            description = "Minimal and icon switch examples.",
            tags = listOf("switch", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SwitchExamples() } }
        ),

        // ==========================================
        // Selection: Time Pickers
        // ==========================================
        ComponentSnippet(
            id = "dial-time-picker",
            title = "Dial Time Picker",
            category = ComponentCategory.SELECTION,
            description = "Time picker featuring an interactive circular clock dial.",
            tags = listOf("time-picker", "dial", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DialExample(onConfirm = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "input-time-picker",
            title = "Input Time Picker",
            category = ComponentCategory.SELECTION,
            description = "Time picker with text input boxes for hour and minute entry.",
            tags = listOf("time-picker", "input", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { InputExample(onConfirm = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "time-picker",
            title = "Time Pickers",
            category = ComponentCategory.SELECTION,
            description = "Dial and input time picker dialogs.",
            tags = listOf("time-picker", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { TimePickerExamples() } }
        ),

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
        )
    )

    fun getById(id: String): ComponentSnippet? {
        return allSnippets.find { it.id.equals(id, ignoreCase = true) }
    }

    fun getByCategory(category: ComponentCategory): List<ComponentSnippet> {
        return allSnippets.filter { it.category == category }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

