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

package com.example.compose.snippets.touchinput.focus

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
class CatalogItem(val value: Int) : Parcelable {
    companion object {
        fun createCatalog(items: Int, startValue: Int = 1): List<CatalogItem> {
            val lastValue = startValue + items
            return (startValue..lastValue).map { CatalogItem(it) }
        }
    }
}

@Composable
fun FocusRestorationInListDetailScreen(
    modifier: Modifier = Modifier
) {

    val catalogData = remember {
        CatalogItem.createCatalog(32)
    }

    FocusRestorationInListDetail(catalogData, modifier)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
// [START android_compose_touchinput_focus_restoration_listdetail]
@Composable
fun FocusRestorationInListDetail(catalogData: List<CatalogItem>, modifier: Modifier = Modifier) {
    val threePaneScaffoldNavigator = rememberListDetailPaneScaffoldNavigator<CatalogItem>()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // Remember the last selected item in the list pane
    // to specify the list item to be focused when users back from the details pane.
    var lastSelectedCatalogItem by remember { mutableStateOf(catalogData.first()) }

    // Flag indicating that it is safe to request focus on the list pane.
    var isListPaneVisible by remember { mutableStateOf(false) }

    BackHandler(threePaneScaffoldNavigator.canNavigateBack(BackNavigationBehavior.PopLatest)) {
        threePaneScaffoldNavigator.navigateBack(BackNavigationBehavior.PopLatest)
    }

    ListDetailPaneScaffold(
        value = threePaneScaffoldNavigator.scaffoldValue,
        directive = threePaneScaffoldNavigator.scaffoldDirective,
        listPane = {
            AnimatedPane {
                // ListPane implements the list pane.
                // showDetails function is called when the user select a list item.
                ListPane(
                    catalogData = catalogData,
                    state = listState,
                    initialFocusItem = lastSelectedCatalogItem,
                    showDetails = { catalogItem ->
                        // Update lastSelectedCatalogItem with the catalogItem object
                        // associated with the clicked list item.
                        lastSelectedCatalogItem = catalogItem

                        // Save the focused child in the ListPane
                        // so that the component can restore focus
                        // when users moving focus to ListPane in two-pane layout.
                        focusRequester.saveFocusedChild()

                        // Show the details of the catalogItem value in the detail pane.
                        threePaneScaffoldNavigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            catalogItem
                        )
                    },
                    modifier = Modifier
                        // Associate focusRequester value with the ListPane composable.
                        .focusRequester(focusRequester)
                        // Set true to isListPaneVisible variable as ListPane composable is visible.
                        .onPlaced { isListPaneVisible = true }
                )
                DisposableEffect(Unit) {
                    // ListPane is removed from the composition when the app is in single pane layout.
                    // Set isSafeToRequestFocus to false so that ListPane gets focused
                    // when it is displayed by users' back action.
                    onDispose { isListPaneVisible = false }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val catalogItem = threePaneScaffoldNavigator.currentDestination?.content
                if(catalogItem !=null){
                    DetailsPane(catalogItem)
                }
            }
        },
        modifier = modifier
    )

    LaunchedEffect(isListPaneVisible) {
        if (isListPaneVisible) {
            val catalogItemIndex = catalogData.indexOf(lastSelectedCatalogItem)
            if(catalogItemIndex >= 0) {
                // Ensure the ListItem for the last selected item is visible
                listState.animateScrollToItem(catalogItemIndex)
            }
            focusRequester.requestFocus()
        }
    }
}
// [END android_compose_touchinput_focus_restoration_listdetail]


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ListPane(
    catalogData: List<CatalogItem>,
    showDetails: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    initialFocusItem: CatalogItem? = null,
) {
    val initialFocus = remember { FocusRequester() }
    val columnModifier = if (catalogData.isEmpty()) {
        modifier.focusRestorer {
            initialFocus
        }
    } else {
        modifier.focusRestorer()
    }

    LazyColumn(
        modifier = columnModifier,
        state = state,
    ) {
        items(catalogData) {
            val itemModifier = if (it == initialFocusItem) {
                Modifier.focusRequester(initialFocus)
            } else {
                Modifier
            }
            ListItem(
                headlineContent = {
                    Text("Item ${it.value}")
                },
                modifier = itemModifier.clickable { showDetails(it) },
            )
        }
    }
}

@Composable
private fun DetailsPane(
    catalogItem: CatalogItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Item ${catalogItem.value}", style = MaterialTheme.typography.displayMedium)
        Button(
            onClick = {},
            modifier = Modifier.initialFocus()
        ) {
            Text("Click me")
        }
    }

}

fun FocusRequester.tryRequestFocus(): Result<Unit> {
    try {
        requestFocus()
    } catch (e: IllegalStateException) {
        return Result.failure(e)
    }
    return Result.success(Unit)
}

@Composable
fun Modifier.initialFocus(focusRequester: FocusRequester = remember { FocusRequester() }): Modifier {
    var isSafe by remember{ mutableStateOf(false) }

    LaunchedEffect(isSafe) {
        if (isSafe) {
            focusRequester.tryRequestFocus()
        }
    }
    return this.focusRequester(focusRequester).onPlaced { isSafe = true }
}