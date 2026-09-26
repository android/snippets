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
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class
)

package com.example.compose.preview.wasm.registry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.components.*

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
 * Single source of truth mapping `[START <region_tag>]` snippet IDs to their
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
        "android_compose_components_buttonwithanimatedshape" to { CenteredBox { ButtonWithAnimatedShapeExample(onClick = {}) } },
        "android_compose_components_squarebutton" to { CenteredBox { SquareButtonExample(onClick = {}) } },
        "android_compose_components_buttonwithicon" to { CenteredBox { ButtonWithIconSample() } },
        "android_compose_components_splitbutton" to { CenteredBox { SplitButtonExample(onClick = {}) } },
        "android_compose_components_buttongroup" to { CenteredBox { ButtonGroupExample() } },
        "android_compose_expressive_components_filledtogglebutton" to { CenteredBox { ToggleButtonSample() } },
        "android_compose_expressive_components_elevatedtogglebutton" to { CenteredBox { ElevatedToggleButtonSample() } },
        "android_compose_expressive_components_tonaltogglebutton" to { CenteredBox { TonalToggleButtonSample() } },
        "android_compose_expressive_components_outlinedtogglebutton" to { CenteredBox { OutlinedToggleButtonSample() } },
        "android_compose_components_fab" to { CenteredBox { Example(onClick = {}) } },
        "android_compose_components_extendedfab" to { CenteredBox { ExtendedExample(onClick = {}) } },
        "android_compose_components_floatingtoolbar" to { CenteredBox { FloatingToolbarExample() } },
        "android_compose_components_smallfab" to { CenteredBox { SmallExample(onClick = {}) } },
        "android_compose_components_largefab" to { CenteredBox { LargeExample(onClick = {}) } },
        "android_compose_components_themebuilder" to { ThemeBuilderPreview() },
        "android_compose_expressive_components_buttonwithicon" to { CenteredBox { ButtonWithIconSample() } },
        "android_compose_expressive_components_togglebuttonwithicon" to { CenteredBox { ToggleButtonWithIconSample() } },
        "android_compose_expressive_components_xmsallbuttonwithicon" to { CenteredBox { XSmallButtonWithIconSample() } },
        "android_compose_expressive_components_xmsalltogglebuttonwithicon" to { CenteredBox { XSmallToggleButtonWithIconSample() } },
        "android_compose_expressive_components_mediumbuttonwithicon" to { CenteredBox { MediumButtonWithIconSample() } },
        "android_compose_expressive_components_mediumtogglebuttonwithicon" to { CenteredBox { MediumToggleButtonWithIconSample() } },
        "android_compose_expressive_components_largebuttonwithicon" to { CenteredBox { LargeButtonWithIconSample() } },
        "android_compose_expressive_components_largetogglebuttonwithicon" to { CenteredBox { LargeToggleButtonWithIconSample() } },
        "android_compose_expressive_components_xlargebuttonwithicon" to { CenteredBox { XLargeButtonWithIconSample() } },
        "android_compose_expressive_components_xlargetogglebuttonwithicon" to { CenteredBox { XLargeToggleButtonWithIconSample() } },
        "android_compose_expressive_components_squaretogglebutton" to { CenteredBox { SquareToggleButtonSample() } },
        "android_compose_expressive_components_mediumfab" to { CenteredBox { MediumFloatingActionButtonSample() } },
        "android_compose_components_togglebuttonexample" to { CenteredBox { ToggleIconButtonExample() } },
        "android_compose_components_momentaryiconbuttons" to { CenteredBox { MomentaryIconButtonExample() } },
        "android_compose_expressive_components_animatediconbuttons" to { CenteredBox { IconButtonWithAnimatedShapeSample() } },
        "android_compose_expressive_components_animatedtoggleiconbuttons" to { CenteredBox { IconToggleButtonWithAnimatedShapeSample() } },
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
        "android_compose_components_loadingindicator" to { CenteredBox { LoadingIndicatorExample() } },
        "android_compose_components_containedloadingindicator" to { CenteredBox { ContainedLoadingIndicatorExample() } },
        "android_compose_components_plaintooltipexample" to { CenteredBox { PlainTooltipExample() } },
        "android_compose_components_richtooltipexample" to { CenteredBox { RichTooltipExample() } },
        "android_compose_components_pull_to_refresh_basic" to { CenteredBox { PullToRefreshBasicPreview() } },
        "android_compose_expressive_components_determinatelinearwavyindicator" to { CenteredBox { LinearWavyProgressIndicatorSample() } },
        "android_compose_expressive_components_indeterminatelinearwavyindicator" to { CenteredBox { IndeterminateLinearWavyProgressIndicatorSample() } },
        "android_compose_expressive_components_determinatecircularwavyindicator" to { CenteredBox { CircularWavyProgressIndicatorSample() } },
        "android_compose_expressive_components_indeterminatecircularwavyindicator" to { CenteredBox { IndeterminateCircularWavyProgressIndicatorSample() } },
        "android_compose_expressive_components_determinatelinearindicator" to { CenteredBox { LinearProgressIndicatorSample() } },
        "android_compose_expressive_components_indeterminatelinearindicator" to { CenteredBox { IndeterminateLinearProgressIndicatorSample() } },
        "android_compose_expressive_components_determinatecircularindicator" to { CenteredBox { CircularProgressIndicatorSample() } },
        "android_compose_expressive_components_indeterminatecircularindicator" to { CenteredBox { IndeterminateCircularProgressIndicatorSample() } },
        "android_compose_components_customizable_searchbar" to { CenteredBox { CustomizableSearchBarExample() } },
        "android_compose_expressive_components_groupedmenusample" to { CenteredBox { GroupedMenuSample() } },
        "android_compose_components_simple_searchbar" to { CenteredBox { SearchBarExamples() } },
        "android_compose_components_swipeitemexample" to { CenteredBox { SwipeToDismissBoxExamples() } },
        "android_compose_components_centeralignedtopappbar" to { CenterAlignedTopAppBarExample() },
        "android_compose_components_smalltopappbar" to { SmallTopAppBarExample() },
        "android_compose_components_mediumtopappbar" to { MediumTopAppBarExample() },
        "android_compose_components_largetopappbar" to { LargeTopAppBarExample() },
        "android_compose_components_navigationbarexample" to { NavigationBarExample() },
        "android_compose_components_navigationrailexample" to { NavigationRailExample() },
        "android_compose_expressive_components_widenavigationrailexample" to { WideNavigationRailResponsiveSample() },
        "android_compose_expressive_components_alwaysentertopappbar" to { CenteredBox { EnterAlwaysTopAppBar() } },
        "android_compose_expressive_components_centeralignedtopappbarwithsubtitle" to { CenteredBox { SimpleCenterAlignedTopAppBarWithSubtitle() } },
        "android_compose_expressive_components_exituntillcollapsedtopappbar" to { CenteredBox { ExitUntilCollapsedCenterAlignedMediumFlexibleTopAppBar() } },
        "android_compose_expressive_components_exituntillcollapsedlargetopappbar" to { CenteredBox { ExitUntilCollapsedCenterAlignedLargeFlexibleTopAppBar() } },
        "android_compose_expressive_components_horizontalitemsnavigationbarexample" to { CenteredBox { ShortNavigationBarWithHorizontalItemsSample() } },
        "android_compose_expressive_components_verticalitemsnavigationbarexample" to { CenteredBox { ShortNavigationBarSample() } },
        "android_compose_expressive_components_modalwidenavigationrailexample" to { CenteredBox { ModalWideNavigationRailSample() } },
        "android_compose_expressive_components_dismissiblemodalwidenavigationrailexample" to { CenteredBox { DismissibleModalWideNavigationRailSample() } },
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
