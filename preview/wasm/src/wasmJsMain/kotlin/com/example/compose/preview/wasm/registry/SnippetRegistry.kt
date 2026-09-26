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
        "alert-dialog" to "android_compose_components_alertdialog",
        "always-enter-top-app-bar" to "android_compose_expressive_components_alwaysentertopappbar",
        "animated-icon-button" to "android_compose_expressive_components_animatediconbuttons",
        "animated-toggle-icon-button" to "android_compose_expressive_components_animatedtoggleiconbuttons",
        "app-bar" to "app-bar-examples",
        "assist-chip" to "android_compose_components_assistchip",
        "badge" to "android_compose_components_badge",
        "badge-interactive" to "android_compose_components_badgeinteractive",
        "button" to "android_compose_components_filledbutton",
        "button-examples" to "android_compose_components_filledbutton",
        "button-group" to "android_compose_components_buttongroup",
        "button-with-animated-shape" to "android_compose_components_buttonwithanimatedshape",
        "button-with-icon" to "android_compose_components_buttonwithicon",
        "button-with-icon-sample" to "android_compose_expressive_components_buttonwithicon",
        "card" to "card-examples",
        "center-aligned-top-app-bar" to "android_compose_components_centeralignedtopappbar",
        "center-aligned-top-app-bar-with-subtitle" to "android_compose_expressive_components_centeralignedtopappbarwithsubtitle",
        "checkbox" to "android_compose_components_checkbox_minimal",
        "chip" to "chip-examples",
        "circular-wavy-progress-indicator" to "android_compose_expressive_components_determinatecircularwavyindicator",
        "contained-loading-indicator" to "android_compose_components_containedloadingindicator",
        "continuous-slider" to "android_compose_components_sliderminimal",
        "date-picker-docked" to "android_compose_components_datepicker_docked",
        "date-picker-input-modal" to "android_compose_components_datepicker_inputmodal",
        "date-picker-modal" to "android_compose_components_datepicker_modal",
        "date-range-picker" to "android_compose_components_datepicker_range",
        "determinate-circular-expressive-indicator" to "android_compose_expressive_components_determinatecircularindicator",
        "determinate-circular-wavy-indicator" to "android_compose_expressive_components_determinatecircularwavyindicator",
        "determinate-linear-expressive-indicator" to "android_compose_expressive_components_determinatelinearindicator",
        "determinate-linear-wavy-indicator" to "android_compose_expressive_components_determinatelinearwavyindicator",
        "determinate-progress-indicator" to "android_compose_components_determinateindicator",
        "dial-time-picker" to "android_compose_components_dial",
        "dialog" to "dialog-examples",
        "dialog-with-image" to "android_compose_components_dialogwithimage",
        "discrete-slider" to "android_compose_components_slideradvanced",
        "dismissible-modal-wide-navigation-rail" to "android_compose_expressive_components_dismissiblemodalwidenavigationrailexample",
        "docked-search-bar" to "android_compose_components_customizable_searchbar",
        "dropdown-menu-with-details" to "android_compose_components_dropdownmenuwithdetails",
        "elevated-button" to "android_compose_components_elevatedbutton",
        "elevated-card" to "android_compose_components_elevatedcard",
        "elevated-toggle-button" to "android_compose_expressive_components_elevatedtogglebutton",
        "extended-fab" to "android_compose_components_extendedfab",
        "fab" to "android_compose_components_fab",
        "filled-button" to "android_compose_components_filledbutton",
        "filled-card" to "android_compose_components_filledcard",
        "filled-tonal-button" to "android_compose_components_filledtonalbutton",
        "filter-chip" to "android_compose_components_filterchip",
        "floating-toolbar" to "android_compose_components_floatingtoolbar",
        "grouped-menu" to "android_compose_expressive_components_groupedmenusample",
        "horizontal-divider" to "android_compose_components_horizontaldivider",
        "horizontal-items-navigation-bar" to "android_compose_expressive_components_horizontalitemsnavigationbarexample",
        "indeterminate-circular-expressive-indicator" to "android_compose_expressive_components_indeterminatecircularindicator",
        "indeterminate-circular-wavy-indicator" to "android_compose_expressive_components_indeterminatecircularwavyindicator",
        "indeterminate-linear-expressive-indicator" to "android_compose_expressive_components_indeterminatelinearindicator",
        "indeterminate-linear-wavy-indicator" to "android_compose_expressive_components_indeterminatelinearwavyindicator",
        "indeterminate-progress-indicator" to "android_compose_components_indeterminateindicator",
        "input-chip" to "android_compose_components_inputchip",
        "input-time-picker" to "android_compose_components_input",
        "large-button-with-icon" to "android_compose_expressive_components_largebuttonwithicon",
        "large-fab" to "android_compose_components_largefab",
        "large-flexible-top-app-bar" to "android_compose_expressive_components_exituntillcollapsedlargetopappbar",
        "large-toggle-button-with-icon" to "android_compose_expressive_components_largetogglebuttonwithicon",
        "large-top-app-bar" to "android_compose_components_largetopappbar",
        "linear-wavy-progress-indicator" to "android_compose_expressive_components_determinatelinearwavyindicator",
        "loading-indicator" to "android_compose_components_loadingindicator",
        "medium-button-with-icon" to "android_compose_expressive_components_mediumbuttonwithicon",
        "medium-fab" to "android_compose_expressive_components_mediumfab",
        "medium-flexible-top-app-bar" to "android_compose_expressive_components_exituntillcollapsedtopappbar",
        "medium-toggle-button-with-icon" to "android_compose_expressive_components_mediumtogglebuttonwithicon",
        "medium-top-app-bar" to "android_compose_components_mediumtopappbar",
        "menu" to "menu-examples",
        "minimal-dialog" to "android_compose_components_minimaldialog",
        "minimal-dropdown-menu" to "android_compose_components_minimaldropdownmenu",
        "minimal-switch" to "android_compose_components_switchminimal",
        "modal-navigation-drawer" to "android_compose_components_detaileddrawerexample",
        "modal-wide-navigation-rail" to "android_compose_expressive_components_modalwidenavigationrailexample",
        "momentary-icon-button" to "android_compose_components_momentaryiconbuttons",
        "multi-browse-carousel" to "android_compose_carousel_multi_browse_basic",
        "multi-choice-segmented-button" to "android_compose_components_multichoicesegmentedbutton",
        "navigation" to "navigation-examples",
        "navigation-bar" to "android_compose_components_navigationbarexample",
        "navigation-rail" to "android_compose_components_navigationrailexample",
        "outlined-button" to "android_compose_components_outlinedbutton",
        "outlined-card" to "android_compose_components_outlinedcard",
        "outlined-toggle-button" to "android_compose_expressive_components_outlinedtogglebutton",
        "parent-checkbox" to "android_compose_components_checkbox_parent",
        "partial-bottom-sheet" to "android_compose_components_partialbottomsheet",
        "plain-tooltip" to "android_compose_components_plaintooltipexample",
        "progress" to "progress-indicator",
        "pull-to-refresh" to "android_compose_components_pull_to_refresh_basic",
        "radio-button" to "android_compose_components_radiobuttonsingleselection",
        "radio-button-single" to "android_compose_components_radiobuttonsingleselection",
        "range-slider" to "android_compose_components_rangeslider",
        "rich-tooltip" to "android_compose_components_richtooltipexample",
        "scaffold" to "android_compose_components_scaffold",
        "scrollable-dropdown-menu" to "android_compose_components_longbasicdropdownmenu",
        "search-bar-simple" to "android_compose_components_simple_searchbar",
        "short-navigation-bar" to "android_compose_expressive_components_verticalitemsnavigationbarexample",
        "single-choice-segmented-button" to "android_compose_components_singlechoicesegmentedbutton",
        "slider" to "slider-examples",
        "small-fab" to "android_compose_components_smallfab",
        "small-top-app-bar" to "android_compose_components_smalltopappbar",
        "split-button" to "android_compose_components_splitbutton",
        "square-button" to "android_compose_components_squarebutton",
        "square-toggle-button" to "android_compose_expressive_components_squaretogglebutton",
        "suggestion-chip" to "android_compose_components_suggestionchip",
        "swipe-to-dismiss-box" to "swipe-to-dismiss",
        "swipe-to-dismiss-item" to "android_compose_components_swipeitemexample",
        "switch" to "switch-examples",
        "switch-with-icon" to "android_compose_components_switchwithicon",
        "text-button" to "android_compose_components_textbutton",
        "theme-builder" to "android_compose_components_themebuilder",
        "toggle-button" to "android_compose_expressive_components_filledtogglebutton",
        "toggle-button-with-icon" to "android_compose_expressive_components_togglebuttonwithicon",
        "toggle-icon-button" to "android_compose_components_togglebuttonexample",
        "tonal-toggle-button" to "android_compose_expressive_components_tonaltogglebutton",
        "tooltip" to "tooltip-examples",
        "uncontained-carousel" to "android_compose_carousel_uncontained_basic",
        "vertical-divider" to "android_compose_components_verticaldivider",
        "vertical-items-navigation-bar" to "android_compose_expressive_components_verticalitemsnavigationbarexample",
        "wide-navigation-rail" to "android_compose_expressive_components_widenavigationrailexample",
        "xlarge-button-with-icon" to "android_compose_expressive_components_xlargebuttonwithicon",
        "xlarge-toggle-button-with-icon" to "android_compose_expressive_components_xlargetogglebuttonwithicon",
        "xsmall-button-with-icon" to "android_compose_expressive_components_xmsallbuttonwithicon",
        "xsmall-toggle-button-with-icon" to "android_compose_expressive_components_xmsalltogglebuttonwithicon"
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
