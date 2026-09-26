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
 * Snippet definitions for Checkboxes, Switches, Sliders, Chips, and Pickers.
 */
object SelectionSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Selection: Checkbox
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_checkbox_minimal",
            title = "Checkbox",
            category = ComponentCategory.SELECTION,
            description = "Standard binary checkbox for selecting or deselecting a single item.",
            tags = listOf("checkbox", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { CheckboxMinimalExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_checkbox_parent",
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
            id = "android_compose_components_assistchip",
            title = "Assist Chip",
            category = ComponentCategory.SELECTION,
            description = "Assist chip triggering an action related to primary content.",
            tags = listOf("chip", "assist", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { AssistChipExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_filterchip",
            title = "Filter Chip",
            category = ComponentCategory.SELECTION,
            description = "Filter chip allowing users to filter content by selecting tags.",
            tags = listOf("chip", "filter", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { FilterChipExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_inputchip",
            title = "Input Chip",
            category = ComponentCategory.SELECTION,
            description = "Input chip representing a complex piece of information like a recipient or tag.",
            tags = listOf("chip", "input", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { InputChipExample(text = "Input Chip", onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_suggestionchip",
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
            id = "android_compose_components_datepicker_modal",
            title = "Modal Date Picker",
            category = ComponentCategory.SELECTION,
            description = "Modal dialog for selecting a single calendar date.",
            tags = listOf("date-picker", "modal", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerModal(onDateSelected = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_datepicker_inputmodal",
            title = "Modal Date Input",
            category = ComponentCategory.SELECTION,
            description = "Modal dialog allowing users to enter a date via text input.",
            tags = listOf("date-picker", "input", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerModalInput(onDateSelected = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_datepicker_docked",
            title = "Docked Date Picker",
            category = ComponentCategory.SELECTION,
            description = "Inline docked date picker anchored to a text input field.",
            tags = listOf("date-picker", "docked", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DatePickerDocked() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_datepicker_range",
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
            id = "android_compose_components_minimaldropdownmenu",
            title = "Dropdown Menu",
            category = ComponentCategory.SELECTION,
            description = "Basic dropdown menu anchored to an icon button.",
            tags = listOf("menu", "dropdown", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { MinimalDropdownMenu() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_longbasicdropdownmenu",
            title = "Scrollable Dropdown Menu",
            category = ComponentCategory.SELECTION,
            description = "Dropdown menu with a long scrollable list of items.",
            tags = listOf("menu", "scrollable", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { LongBasicDropdownMenu() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_dropdownmenuwithdetails",
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
            id = "android_compose_components_sliderminimal",
            title = "Continuous Slider",
            category = ComponentCategory.SELECTION,
            description = "Continuous slider for selecting a numeric value along a bar.",
            tags = listOf("slider", "continuous", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SliderMinimalExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_slideradvanced",
            title = "Discrete Slider (Steps)",
            category = ComponentCategory.SELECTION,
            description = "Slider with discrete steps and custom thumb.",
            tags = listOf("slider", "discrete", "steps", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SliderAdvancedExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_rangeslider",
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
            id = "android_compose_components_switchminimal",
            title = "Minimal Switch",
            category = ComponentCategory.SELECTION,
            description = "Standard binary toggle switch for turning settings on or off.",
            tags = listOf("switch", "toggle", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { SwitchMinimalExample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_switchwithicon",
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
            id = "android_compose_components_dial",
            title = "Dial Time Picker",
            category = ComponentCategory.SELECTION,
            description = "Time picker featuring an interactive circular clock dial.",
            tags = listOf("time-picker", "dial", "selection"),
            codeSnippet = "",
            composable = { CenteredBox { DialExample(onConfirm = {}, onDismiss = {}) } }
        ),
        ComponentSnippet(
            id = "android_compose_components_input",
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
        ComponentSnippet(
            id = "android_compose_components_radiobuttonsingleselection",
            title = "Radio Button Single Selection",
            category = ComponentCategory.SELECTION,
            description = "Single-selection radio button group with selectable rows.",
            tags = listOf("radio", "selection", "inputs"),
            composable = { CenteredBox { RadioButtonSingleSelection() } }
        )
    )
}
