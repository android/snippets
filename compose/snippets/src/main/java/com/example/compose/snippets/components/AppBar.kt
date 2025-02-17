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

package com.example.compose.snippets.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppBarExamples(
    navigateBack: () -> Unit
) {
    var selection by remember { mutableStateOf("none") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (selection) {
            "bottomBar" -> BottomAppBarExample()
            "topBar" -> SmallTopAppBarExample()
            "topBarCenter" -> CenterAlignedTopAppBarExample()
            "topBarMedium" -> MediumTopAppBarExample()
            "topBarLarge" -> LargeTopAppBarExample()
            "topBarNavigation" -> TopBarNavigationExample { navigateBack() }
            "multiSelection" -> AppBarMultiSelectionExample()
            else -> AppBarOptions(
                toBottom = { selection = "bottomBar" },
                toTopBarSmall = { selection = "topBar" },
                toTopBarCenter = { selection = "topBarCenter" },
                toTopBarMedium = { selection = "topBarMedium" },
                toTopBarLarge = { selection = "topBarLarge" },
                toTopBarNavigation = { selection = "topBarNavigation" },
                toMultiSelection = { selection = "multiSelection" },
            )
        }
    }
}

@Composable
fun AppBarOptions(
    toBottom: () -> Unit,
    toTopBarSmall: () -> Unit,
    toTopBarCenter: () -> Unit,
    toTopBarMedium: () -> Unit,
    toTopBarLarge: () -> Unit,
    toTopBarNavigation: () -> Unit,
    toMultiSelection: () -> Unit,
) {
    Column() {
        Button({ toBottom() }) {
            Text("Bottom bar")
        }
        Button({ toTopBarSmall() }) {
            Text("Small top bar")
        }
        Button({ toTopBarCenter() }) {
            Text("Center aligned top bar")
        }
        Button({ toTopBarMedium() }) {
            Text("Medium top bar")
        }
        Button({ toTopBarLarge() }) {
            Text("Large top bar")
        }
        Button({ toTopBarNavigation() }) {
            Text("Top bar navigation example")
        }
        Button({ toMultiSelection() }) {
            Text("Top bar with multi selection list")
        }
    }
}

@Preview
// [START android_compose_components_bottomappbar]
@Composable
fun BottomAppBarExample() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        },
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Example of a scaffold with a bottom app bar."
        )
    }
}
// [END android_compose_components_bottomappbar]

@OptIn(ExperimentalMaterial3Api::class)
@Preview
// [START android_compose_components_smalltopappbar]
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Small Top App Bar")
                }
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}
// [END android_compose_components_smalltopappbar]

@OptIn(ExperimentalMaterial3Api::class)
@Preview
// [START android_compose_components_centeralignedtopappbar]
@Composable
fun CenterAlignedTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Centered Top App Bar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}
// [END android_compose_components_centeralignedtopappbar]

@OptIn(ExperimentalMaterial3Api::class)
@Preview
// [START android_compose_components_mediumtopappbar]
@Composable
fun MediumTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Medium Top App Bar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}
// [END android_compose_components_mediumtopappbar]

@OptIn(ExperimentalMaterial3Api::class)
@Preview
// [START android_compose_components_largetopappbar]
@Composable
fun LargeTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Large Top App Bar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}
// [END android_compose_components_largetopappbar]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_navigation]
@Composable
fun TopBarNavigationExample(
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Navigation example",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Text(
            "Click the back button to pop from the back stack.",
            modifier = Modifier.padding(innerPadding),
        )
    }
}
// [END android_compose_components_navigation]

@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    val range = 1..100

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(range.count()) { index ->
            Text(text = "- List item number ${index + 1}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_appbarselectionactions]
@Composable
fun AppBarSelectionActions(
    selectedItems: Set<Int>,
    modifier: Modifier = Modifier,
) {
    val hasSelection = selectedItems.isNotEmpty()
    val topBarText = if (hasSelection) {
        "Selected ${selectedItems.size} items"
    } else {
        "List of items"
    }

    TopAppBar(
        title = {
            Text(topBarText)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            if (hasSelection) {
                IconButton(onClick = {
                    /* click action */
                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share items"
                    )
                }
            }
        },
    )
}
// [END android_compose_components_appbarselectionactions]

@Preview
@Composable
private fun AppBarSelectionActionsPreview() {
    val selectedItems = setOf(1, 2, 3)

    AppBarSelectionActions(selectedItems)
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
// [START android_compose_components_appbarmultiselectionexample]
@Composable
private fun AppBarMultiSelectionExample(
    modifier: Modifier = Modifier,
) {
    val listItems by remember { mutableStateOf(listOf(1, 2, 3, 4, 5, 6)) }
    var selectedItems by rememberSaveable { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = { AppBarSelectionActions(selectedItems) }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            itemsIndexed(listItems) { _, index ->
                val isItemSelected = selectedItems.contains(index)
                ListItemSelectable(
                    selected = isItemSelected,
                    Modifier
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                /* click action */
                            },
                            onLongClick = {
                                if (isItemSelected) selectedItems -= index else selectedItems += index
                            }
                        )
                )
            }
        }
    }
}
// [END android_compose_components_appbarmultiselectionexample]

// [START android_compose_components_listitemselectable]
@Composable
fun ListItemSelectable(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ListItem(
            headlineContent = { Text("Long press to select or deselect item") },
            leadingContent = {
                if (selected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Localized description",
                    )
                }
            }
        )
    }
}
// [END android_compose_components_listitemselectable]

@Preview
@Composable
private fun ListItemSelectablePreview() {
    ListItemSelectable(true)
}

@OptIn(ExperimentalFoundationApi::class)
// [START android_compose_components_lazylistmultiselection
@Composable
fun LazyListMultiSelection(
    listItems: List<Int>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    var selectedItems by rememberSaveable { mutableStateOf(setOf<Int>()) }

    LazyColumn(contentPadding = contentPadding) {
        itemsIndexed(listItems) { _, index ->
            val selected = selectedItems.contains(index)
            ListItemSelectable(
                selected = selected,
                Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            /* click action */
                        },
                        onLongClick = {
                            if (selected) selectedItems -= index else selectedItems += index
                        }
                    )
            )
        }
    }
}
// [END android_compose_components_lazylistmultiselection

@Preview
@Composable
private fun LazyListMultiSelectionPreview() {
    val listItems = listOf(1, 2, 3)

    LazyListMultiSelection(
        listItems,
        modifier = Modifier
    )
}
