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
 * Snippet definitions for App Bars, Navigation Bars, Navigation Rails, and Drawers.
 */
object NavigationSnippets {
    val snippets: List<ComponentSnippet> = listOf(
        // ==========================================
        // Navigation: App Bars
        // ==========================================
        ComponentSnippet(
            id = "android_compose_components_centeralignedtopappbar",
            title = "Center-Aligned Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Top app bar with centered headline title and action icons.",
            tags = listOf("app-bar", "top-bar", "navigation"),
            codeSnippet = "",
            composable = { CenterAlignedTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "android_compose_components_smalltopappbar",
            title = "Small Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Standard compact top app bar with start-aligned title.",
            tags = listOf("app-bar", "top-bar", "navigation"),
            codeSnippet = "",
            composable = { SmallTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "android_compose_components_mediumtopappbar",
            title = "Medium Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Medium top app bar featuring a larger title area that collapses on scroll.",
            tags = listOf("app-bar", "medium", "navigation"),
            codeSnippet = "",
            composable = { MediumTopAppBarExample() }
        ),
        ComponentSnippet(
            id = "android_compose_components_largetopappbar",
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
            id = "android_compose_components_navigationbarexample",
            title = "Navigation Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Bottom navigation bar providing access to 3 to 5 top-level destinations.",
            tags = listOf("navigation-bar", "bottom-navigation", "navigation"),
            codeSnippet = "",
            composable = { NavigationBarExample() }
        ),
        ComponentSnippet(
            id = "android_compose_components_navigationrailexample",
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
        ComponentSnippet(
            id = "short-navigation-bar",
            title = "Short Navigation Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive compact ShortNavigationBar layout.",
            tags = listOf("navigation-bar", "short", "expressive", "navigation"),
            codeSnippet = "",
            composable = { ShortNavigationBarSample() }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_widenavigationrailexample",
            title = "Wide Navigation Rail",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive WideNavigationRail responsive layout.",
            tags = listOf("navigation-rail", "wide", "responsive", "navigation"),
            codeSnippet = "",
            composable = { WideNavigationRailResponsiveSample() }
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
        // Additional Catalog Components & Samples
        // ==========================================
        ComponentSnippet(
            id = "android_compose_expressive_components_alwaysentertopappbar",
            title = "Always-Enter Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive top app bar with enterAlways scroll behavior.",
            tags = listOf("app-bar", "navigation", "expressive"),
            composable = { CenteredBox { EnterAlwaysTopAppBar() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_centeralignedtopappbarwithsubtitle",
            title = "Center-Aligned Top App Bar with Subtitle",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive center-aligned top app bar with title and subtitle headers.",
            tags = listOf("app-bar", "navigation", "expressive", "subtitle"),
            composable = { CenteredBox { SimpleCenterAlignedTopAppBarWithSubtitle() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_exituntillcollapsedtopappbar",
            title = "Medium Flexible Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive medium flexible top app bar with exitUntilCollapsed behavior.",
            tags = listOf("app-bar", "navigation", "expressive", "medium"),
            composable = { CenteredBox { ExitUntilCollapsedCenterAlignedMediumFlexibleTopAppBar() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_exituntillcollapsedlargetopappbar",
            title = "Large Flexible Top App Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive large flexible top app bar with prominent collapsing headline.",
            tags = listOf("app-bar", "navigation", "expressive", "large"),
            composable = { CenteredBox { ExitUntilCollapsedCenterAlignedLargeFlexibleTopAppBar() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_horizontalitemsnavigationbarexample",
            title = "Horizontal Items Navigation Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Short navigation bar displaying items with horizontally arranged icon and label.",
            tags = listOf("navigation", "bar", "horizontal"),
            composable = { CenteredBox { ShortNavigationBarWithHorizontalItemsSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_verticalitemsnavigationbarexample",
            title = "Vertical Items Navigation Bar",
            category = ComponentCategory.NAVIGATION,
            description = "Short navigation bar displaying items with vertically arranged icon and label.",
            tags = listOf("navigation", "bar", "vertical"),
            composable = { CenteredBox { ShortNavigationBarSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_modalwidenavigationrailexample",
            title = "Modal Wide Navigation Rail",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive wide navigation rail in modal drawer configuration.",
            tags = listOf("navigation", "rail", "modal", "wide"),
            composable = { CenteredBox { ModalWideNavigationRailSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_expressive_components_dismissiblemodalwidenavigationrailexample",
            title = "Dismissible Modal Wide Navigation Rail",
            category = ComponentCategory.NAVIGATION,
            description = "Material 3 Expressive dismissible modal wide navigation rail.",
            tags = listOf("navigation", "rail", "dismissible", "wide"),
            composable = { CenteredBox { DismissibleModalWideNavigationRailSample() } }
        ),
        ComponentSnippet(
            id = "android_compose_components_detaileddrawerexample",
            title = "Modal Navigation Drawer",
            category = ComponentCategory.NAVIGATION,
            description = "Modal navigation drawer with standard navigation items and headline.",
            tags = listOf("navigation", "drawer", "modal"),
            composable = { CenteredBox { NavigationDrawerExamples() } }
        )
    )
}
