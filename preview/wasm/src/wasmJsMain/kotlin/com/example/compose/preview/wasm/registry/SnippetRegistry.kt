/*
 * Copyright 2026 The Android Open Source Project
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
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.compose.preview.wasm.registry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.components.AlertDialogExample
import com.example.compose.snippets.components.AppIcons
import com.example.compose.snippets.components.AssistChipExample
import com.example.compose.snippets.components.BadgeExample
import com.example.compose.snippets.components.BadgeInteractiveExample
import com.example.compose.snippets.components.CarouselExample
import com.example.compose.snippets.components.CarouselExample_MultiBrowse
import com.example.compose.snippets.components.CenterAlignedTopAppBarExample
import com.example.compose.snippets.components.CheckboxMinimalExample
import com.example.compose.snippets.components.CheckboxParentExample
import com.example.compose.snippets.components.CustomizableSearchBarExample
import com.example.compose.snippets.components.DatePickerDocked
import com.example.compose.snippets.components.DatePickerModal
import com.example.compose.snippets.components.DatePickerModalInput
import com.example.compose.snippets.components.DateRangePickerModal
import com.example.compose.snippets.components.DialExample
import com.example.compose.snippets.components.DialogExamples
import com.example.compose.snippets.components.DropdownMenuWithDetails
import com.example.compose.snippets.components.ElevatedButtonExample
import com.example.compose.snippets.components.ElevatedCardExample
import com.example.compose.snippets.components.Example
import com.example.compose.snippets.components.ExtendedExample
import com.example.compose.snippets.components.FilledButtonExample
import com.example.compose.snippets.components.FilledCardExample
import com.example.compose.snippets.components.FilledTonalButtonExample
import com.example.compose.snippets.components.FilterChipExample
import com.example.compose.snippets.components.HorizontalDividerExample
import com.example.compose.snippets.components.IndeterminateCircularIndicator
import com.example.compose.snippets.components.InputChipExample
import com.example.compose.snippets.components.InputExample
import com.example.compose.snippets.components.LargeExample
import com.example.compose.snippets.components.LargeTopAppBarExample
import com.example.compose.snippets.components.LinearDeterminateIndicator
import com.example.compose.snippets.components.LongBasicDropdownMenu
import com.example.compose.snippets.components.MediumTopAppBarExample
import com.example.compose.snippets.components.MinimalDialog
import com.example.compose.snippets.components.MinimalDropdownMenu
import com.example.compose.snippets.components.MomentaryIconButtonExample
import com.example.compose.snippets.components.MultiChoiceSegmentedButton
import com.example.compose.snippets.components.NavigationBarExample
import com.example.compose.snippets.components.NavigationDrawerExamples
import com.example.compose.snippets.components.NavigationRailExample
import com.example.compose.snippets.components.OutlinedButtonExample
import com.example.compose.snippets.components.OutlinedCardExample
import com.example.compose.snippets.components.PartialBottomSheet
import com.example.compose.snippets.components.PlainTooltipExample
import com.example.compose.snippets.components.PullToRefreshBasicPreview
import com.example.compose.snippets.components.RadioButtonSingleSelection
import com.example.compose.snippets.components.RangeSliderExample
import com.example.compose.snippets.components.RichTooltipExample
import com.example.compose.snippets.components.ScaffoldExample
import com.example.compose.snippets.components.SearchBarExamples
import com.example.compose.snippets.components.SingleChoiceSegmentedButton
import com.example.compose.snippets.components.SliderAdvancedExample
import com.example.compose.snippets.components.SliderMinimalExample
import com.example.compose.snippets.components.SmallExample
import com.example.compose.snippets.components.SmallTopAppBarExample
import com.example.compose.snippets.components.SuggestionChipExample
import com.example.compose.snippets.components.SwipeToDismissBoxExamples
import com.example.compose.snippets.components.SwitchMinimalExample
import com.example.compose.snippets.components.SwitchWithIconExample
import com.example.compose.snippets.components.TextButtonExample
import com.example.compose.snippets.components.ToggleIconButtonExample
import com.example.compose.snippets.components.VerticalDividerExample

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Single source of truth mapping DAC snippet region tag IDs to their
 * interactive `@Composable` preview lambdas.
 *
 * Used by both:
 * - `:preview:wasm` (`WasmPreviewApp`) to render interactive snippets by `?id=<region_tag>`
 * - `:preview:generator` (`PreviewScreenshotTest`) to capture static `<region_tag>.png` screenshots
 */
object SnippetRegistry {
    val snippets: Map<String, @Composable () -> Unit> = mapOf(
        "android_compose_components_singlechoicesegmentedbutton" to { CenteredBox { SingleChoiceSegmentedButton() } },
        "android_compose_components_multichoicesegmentedbutton" to { CenteredBox { MultiChoiceSegmentedButton() } },
        "android_compose_components_filledbutton" to { CenteredBox { FilledButtonExample(onClick = {}) } },
        "android_compose_components_filledtonalbutton" to { CenteredBox { FilledTonalButtonExample(onClick = {}) } },
        "android_compose_components_elevatedbutton" to { CenteredBox { ElevatedButtonExample(onClick = {}) } },
        "android_compose_components_outlinedbutton" to { CenteredBox { OutlinedButtonExample(onClick = {}) } },
        "android_compose_components_textbutton" to { CenteredBox { TextButtonExample(onClick = {}) } },
        "android_compose_components_fab" to { CenteredBox { Example(onClick = {}) } },
        "android_compose_components_extendedfab" to { CenteredBox { ExtendedExample(onClick = {}) } },
        "android_compose_components_smallfab" to { CenteredBox { SmallExample(onClick = {}) } },
        "android_compose_components_largefab" to { CenteredBox { LargeExample(onClick = {}) } },
        "android_compose_components_togglebuttonexample" to { CenteredBox { ToggleIconButtonExample() } },
        "android_compose_components_momentaryiconbuttons" to { CenteredBox { MomentaryIconButtonExample() } },
        "android_compose_components_partialbottomsheet" to { CenteredBox { PartialBottomSheet() } },
        "android_compose_components_filledcard" to { CenteredBox { FilledCardExample() } },
        "android_compose_components_elevatedcard" to { CenteredBox { ElevatedCardExample() } },
        "android_compose_components_outlinedcard" to { CenteredBox { OutlinedCardExample() } },
        "android_compose_carousel_multi_browse_basic" to { CenteredBox { CarouselExample_MultiBrowse() } },
        "android_compose_carousel_uncontained_basic" to { CenteredBox { CarouselExample() } },
        "android_compose_components_alertdialog" to { CenteredBox { AlertDialogExample(onDismissRequest = {}, onConfirmation = {}, dialogTitle = "Alert", dialogText = "Example dialog message", icon = AppIcons.Info) } },
        "android_compose_components_minimaldialog" to { CenteredBox { MinimalDialog(onDismissRequest = {}) } },
        "android_compose_components_dialogwithimage" to { CenteredBox { DialogExamples() } },
        "android_compose_components_horizontaldivider" to { CenteredBox { HorizontalDividerExample() } },
        "android_compose_components_verticaldivider" to { CenteredBox { VerticalDividerExample() } },
        "android_compose_components_scaffold" to { ScaffoldExample() },
        "android_compose_components_badge" to { CenteredBox { BadgeExample() } },
        "android_compose_components_badgeinteractive" to { CenteredBox { BadgeInteractiveExample() } },
        "android_compose_components_indeterminateindicator" to { CenteredBox { IndeterminateCircularIndicator() } },
        "android_compose_components_determinateindicator" to { CenteredBox { LinearDeterminateIndicator() } },
        "android_compose_components_plaintooltipexample" to { CenteredBox { PlainTooltipExample() } },
        "android_compose_components_richtooltipexample" to { CenteredBox { RichTooltipExample() } },
        "android_compose_components_pull_to_refresh_basic" to { CenteredBox { PullToRefreshBasicPreview() } },
        "android_compose_components_customizable_searchbar" to { CenteredBox { CustomizableSearchBarExample() } },
        "android_compose_components_simple_searchbar" to { CenteredBox { SearchBarExamples() } },
        "android_compose_components_swipeitemexample" to { CenteredBox { SwipeToDismissBoxExamples() } },
        "android_compose_components_centeralignedtopappbar" to { CenterAlignedTopAppBarExample() },
        "android_compose_components_smalltopappbar" to { SmallTopAppBarExample() },
        "android_compose_components_mediumtopappbar" to { MediumTopAppBarExample() },
        "android_compose_components_largetopappbar" to { LargeTopAppBarExample() },
        "android_compose_components_navigationbarexample" to { NavigationBarExample() },
        "android_compose_components_navigationrailexample" to { NavigationRailExample() },
        "android_compose_components_detaileddrawerexample" to { CenteredBox { NavigationDrawerExamples() } },
        "android_compose_components_checkbox_minimal" to { CenteredBox { CheckboxMinimalExample() } },
        "android_compose_components_checkbox_parent" to { CenteredBox { CheckboxParentExample() } },
        "android_compose_components_assistchip" to { CenteredBox { AssistChipExample() } },
        "android_compose_components_filterchip" to { CenteredBox { FilterChipExample() } },
        "android_compose_components_inputchip" to { CenteredBox { InputChipExample(text = "Input Chip", onDismiss = {}) } },
        "android_compose_components_suggestionchip" to { CenteredBox { SuggestionChipExample() } },
        "android_compose_components_datepicker_modal" to { CenteredBox { DatePickerModal(onDateSelected = {}, onDismiss = {}) } },
        "android_compose_components_datepicker_inputmodal" to { CenteredBox { DatePickerModalInput(onDateSelected = {}, onDismiss = {}) } },
        "android_compose_components_datepicker_docked" to { CenteredBox { DatePickerDocked() } },
        "android_compose_components_datepicker_range" to { CenteredBox { DateRangePickerModal(onDateRangeSelected = {}, onDismiss = {}) } },
        "android_compose_components_minimaldropdownmenu" to { CenteredBox { MinimalDropdownMenu() } },
        "android_compose_components_longbasicdropdownmenu" to { CenteredBox { LongBasicDropdownMenu() } },
        "android_compose_components_dropdownmenuwithdetails" to { CenteredBox { DropdownMenuWithDetails() } },
        "android_compose_components_sliderminimal" to { CenteredBox { SliderMinimalExample() } },
        "android_compose_components_slideradvanced" to { CenteredBox { SliderAdvancedExample() } },
        "android_compose_components_rangeslider" to { CenteredBox { RangeSliderExample() } },
        "android_compose_components_switchminimal" to { CenteredBox { SwitchMinimalExample() } },
        "android_compose_components_switchwithicon" to { CenteredBox { SwitchWithIconExample() } },
        "android_compose_components_dial" to { CenteredBox { DialExample(onConfirm = {}, onDismiss = {}) } },
        "android_compose_components_input" to { CenteredBox { InputExample(onConfirm = {}, onDismiss = {}) } },
        "android_compose_components_radiobuttonsingleselection" to { CenteredBox { RadioButtonSingleSelection() } }
    )

    fun getById(id: String): (@Composable () -> Unit)? = snippets[id]
}
