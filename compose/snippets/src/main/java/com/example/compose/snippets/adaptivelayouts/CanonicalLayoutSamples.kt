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

package com.example.compose.snippets.adaptivelayouts

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


// [START android_compose_canonical_layouts_sample_my_feed]
@Composable
fun MyFeed(names: List<String>) {
    LazyVerticalGrid(
        // GridCells.Adaptive automatically adapts column count based on available width
        columns = GridCells.Adaptive(minSize = 180.dp),
    ) {
        items(names) { name ->
            Text(name)
        }
    }
}
// [END android_compose_canonical_layouts_sample_my_feed]

// [START android_compose_canonical_layouts_list_detail_pane_scaffold]
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MyListDetailPaneScaffold() {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            // Listing Pane
        },
        detailPane = {
            // Details Pane
        }
    )
}
// [END android_compose_canonical_layouts_list_detail_pane_scaffold]

// [START android_compose_canonical_layouts_supporting_pane_scaffold]
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MySupportingPaneScaffold() {
    // Creates and remembers a navigator to control pane visibility and navigation
    val navigator = rememberSupportingPaneScaffoldNavigator()
    SupportingPaneScaffold(
        // Directive and value help control pane visibility based on screen size and state
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            // Main Pane for the primary content
        },
        supportingPane = {
            //Supporting Pane for supplementary content
        }
    )
}
// [END android_compose_canonical_layouts_supporting_pane_scaffold]
