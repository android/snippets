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

import android.os.Parcelable
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleListDetailPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part02]
    val navigator = rememberListDetailPaneScaffoldNavigator<MyItem>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part02]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part03]
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        // [START_EXCLUDE]
        listPane = {},
        detailPane = {},
        // [END_EXCLUDE]
    )
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part03]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part04]
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                MyList(
                    onItemClick = { item ->
                        // Navigate to the detail pane with the passed item
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                    }
                )
            }
        },
        // [START_EXCLUDE]
        detailPane = {},
        // [END_EXCLUDE]
    )
    // [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part04]

    // [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_part05]
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane =
        // [START_EXCLUDE]
        {},
        // [END_EXCLUDE]
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.content?.let {
                    MyDetails(it)
                }
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
    val navigator = rememberListDetailPaneScaffoldNavigator<MyItem>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                MyList(
                    onItemClick = { item ->
                        // Navigate to the detail pane with the passed item
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                // Show the detail pane content if selected item is available
                navigator.currentDestination?.content?.let {
                    MyDetails(it)
                }
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

// [START android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_myitem]
@Parcelize
class MyItem(val id: Int) : Parcelable
// [END android_compose_adaptivelayouts_sample_list_detail_pane_scaffold_myitem]

val shortStrings = listOf(
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
