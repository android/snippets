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

package com.example.compose.snippets.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.compose.snippets.ui.theme.SnippetsTheme

// [START android_compose_layouts_adaptive_skill_previews]
@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
annotation class FormFactorPreviews

@PreviewTest
@FormFactorPreviews
@Composable
fun FeedScreenPreview() {
    SnippetsTheme {
        Box {
            Text("My Screen")
        }
    }
}
// [END android_compose_layouts_adaptive_skill_previews]

val navItems : NavigationSuiteScope.() -> Unit = {}

@Composable
fun AdaptiveNavigationArea(){

// [START android_compose_layouts_adaptive_skill_navigation_area]

    // Pass this variable to any composable that needs to control the navigation area visibility
    var isNavBarVisible by remember { mutableStateOf(true) }
    val scaffoldVisibilityState = rememberNavigationSuiteScaffoldState()

    NavigationSuiteScaffold(
        navigationSuiteItems = navItems,
        state = scaffoldVisibilityState
    ) {
        // Main content
    }

    LaunchedEffect(isNavBarVisible){
        if (isNavBarVisible) {
            scaffoldVisibilityState.show()
        } else {
            scaffoldVisibilityState.hide()
        }
    }
// [END android_compose_layouts_adaptive_skill_navigation_area]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridWithDynamicColumnWidth(){
// [START android_compose_layouts_adaptive_skill_dynamic_grid]
    Grid(
        config = {
            val maxWidthDp = constraints.maxWidth.toDp()
            val (cols, rows) = if (maxWidthDp < 800.dp){
                2 to 4
            } else{
                4 to 2
            }

            val gapSizeDp = 8.dp
            val cellSize = ((maxWidthDp - (gapSizeDp * (cols - 1))) / cols).coerceAtLeast(0.dp)
            repeat(cols) { column(cellSize) }
            repeat(rows) { row(cellSize) }
            gap(gapSizeDp)
        }
    ) { /** items **/ }
// [END android_compose_layouts_adaptive_skill_dynamic_grid]
}
