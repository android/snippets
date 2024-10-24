/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.navigation

enum class Destination(val route: String, val title: String) {
    BrushExamples("brushExamples", "Brush Examples"),
    ImageExamples("imageExamples", "Image Examples"),
    AnimationQuickGuideExamples("animationExamples", "Animation Examples"),
    ComponentsExamples("topComponents", "Top Compose Components"),
    ScreenshotExample("screenshotExample", "Screenshot Examples"),
    ShapesExamples("shapesExamples", "Shapes Examples"),
    SharedElementExamples("sharedElement", "Shared elements"),
    PagerExamples("pagerExamples", "Pager examples")
}

// Enum class for compose components navigation screen.
enum class TopComponentsDestination(val route: String, val title: String) {
    CardExamples("cardExamples", "Card"),
    SwitchExamples("switchExamples", "Switch"),
    SliderExamples("sliderExamples", "Slider"),
    DialogExamples("dialogExamples", "Dialog"),
    ChipExamples("chipExamples", "Chip"),
    FloatingActionButtonExamples("floatingActionButtonExamples", "Floating Action Button"),
    ButtonExamples("buttonExamples", "Button"),
    ProgressIndicatorExamples("progressIndicatorExamples", "Progress Indicators"),
    ScaffoldExample("scaffoldExample", "Scaffold"),
    AppBarExamples("appBarExamples", "App bars"),
    CheckboxExamples("checkboxExamples", "Checkbox"),
    DividerExamples("dividerExamples", "Dividers"),
    BadgeExamples("badgeExamples", "Badges"),
    PartialBottomSheet("partialBottomSheets", "Partial Bottom Sheet"),
    TimePickerExamples("timePickerExamples", "Time Pickers"),
    DatePickerExamples("datePickerExamples", "Date Pickers"),
    CarouselExamples("carouselExamples", "Carousel"),
    MenusExample("menusExamples", "Menus"),
    TooltipExamples("tooltipExamples", "Tooltips"),
    NavigationDrawerExamples("navigationDrawerExamples", "Navigation drawer"),
    SegmentedButtonExamples("segmentedButtonExamples", "Segmented button")
}
