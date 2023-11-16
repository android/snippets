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

package com.example.compose.snippets.adaptivelayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.rememberListDetailPaneScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleListDetailPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part02]
    val state = rememberListDetailPaneScaffoldState()
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part02]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part01]
    var selectedItem: MyItem? by rememberSaveable { mutableStateOf(null) }
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part01]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part03]
    ListDetailPaneScaffold(
        scaffoldState = state,
        // [START_EXCLUDE]
        listPane = {},
        detailPane = {},
        // [END_EXCLUDE]
    )
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part03]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part04]
    ListDetailPaneScaffold(
        scaffoldState = state,
        listPane = {
            MyList(
                onItemClick = { id ->
                    // Set current item
                    selectedItem = id
                    // Switch focus to detail pane
                    state.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            )
        },
        // [START_EXCLUDE]
        detailPane = {},
        // [END_EXCLUDE]
    )
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part04]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part05]
    ListDetailPaneScaffold(
        scaffoldState = state,
        listPane =
        // [START_EXCLUDE]
        {},
        // [END_EXCLUDE]
        detailPane = {
            selectedItem?.let { item ->
                MyDetails(item)
            }
        },
    )
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part05]
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
fun SampleListDetailPaneScaffoldFull() {
// [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_full]
    // Create the ListDetailPaneScaffoldState
    val state = rememberListDetailPaneScaffoldState()

    // Currently selected item
    var selectedItem: MyItem? by rememberSaveable { mutableStateOf(null) }

    ListDetailPaneScaffold(
        scaffoldState = state,
        listPane = {
            MyList(
                onItemClick = { id ->
                    // Set current item
                    selectedItem = id
                    // Display the detail pane
                    state.navigateTo(ListDetailPaneScaffoldRole.Detail)
                },
            )
        },
        detailPane = {
            // Show the detail pane content if selected item is available
            selectedItem?.let { item ->
                MyDetails(item)
            }
        },
    )
// [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_full]
}

@Composable
fun MyList(
    onItemClick: (MyItem) -> Unit,
) {
    Card {
        LazyColumn {
            shortStrings.forEachIndexed { id, string ->
                item {
                    ListItem(
                        modifier = Modifier
                            .background(Color.Magenta)
                            .clickable {
                                onItemClick(MyItem(id))
                            },
                        headlineContent = {
                            Text(
                                text = string,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MyDetails(item: MyItem) {
    val text = shortStrings[item.id]
    Card {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Details page for $text",
                fontSize = 24.sp,
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "TODO: Add great details here"
            )
        }
    }
}

class MyItem(val id: Int)

val shortStrings = listOf(
    "Android",
    "Petit four",
    "Cupcake",
    "Donut",
    "Eclair",
    "Froyo",
    "Gingerbread",
    "Honeycomb",
    "Ice cream sandwich",
    "Jelly bean",
    "Kitkat",
    "Lollipop",
    "Marshmallow",
    "Nougat",
    "Oreo",
    "Pie",
)
