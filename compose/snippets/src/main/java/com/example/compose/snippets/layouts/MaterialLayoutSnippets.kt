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

package com.example.compose.snippets.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// [START android_compose_layout_material_theme]
@Composable
fun MyApp() {
    MaterialTheme {
        // Material Components like Button, Card, Switch, etc.
    }
}
// [END android_compose_layout_material_theme]

@Composable
fun ButtonDemo() {
    // [START android_compose_layout_material_button]
    Button(
        onClick = { /* ... */ },
        // Uses ButtonDefaults.ContentPadding by default
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        )
    ) {
        // Inner content including an icon and a text label
        Icon(
            Icons.Filled.Favorite,
            contentDescription = "Favorite",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Like")
    }
    // [END android_compose_layout_material_button]
}

@Composable
fun ExtendedFabDemo() {
    // [START android_compose_layout_material_fab]
    ExtendedFloatingActionButton(
        onClick = { /* ... */ },
        icon = {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite"
            )
        },
        text = { Text("Like") }
    )
    // [END android_compose_layout_material_fab]
}

@Composable
fun ScaffoldDemo() {
    // [START android_compose_layout_material_scaffold]
    Scaffold(/* ... */) { contentPadding ->
        // Screen content
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
    }
    // [END android_compose_layout_material_scaffold]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldTopAppBarDemo() {
    // [START android_compose_layout_material_appbar]
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("My App")
            })
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_appbar]
}

@Composable
fun ScaffoldBottomBarDemo() {
    // [START android_compose_layout_material_bottombar]
    Scaffold(
        bottomBar = {
            BottomAppBar { /* Bottom app bar content */ }
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_bottombar]
}

@Composable
fun ScaffoldFabDemo() {
    // [START android_compose_layout_material_scaffold_fab]
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* ... */ }) {
                /* FAB content */
            }
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_scaffold_fab]
}

@Composable
fun ScaffoldFabPositionDemo() {
    // [START android_compose_layout_material_scaffold_fab_position]
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* ... */ }) {
                /* FAB content */
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_scaffold_fab_position]
}

@Composable
fun ScaffoldFabAndBottomBarDemo() {
    // [START android_compose_layout_material_scaffold_fab_docked]
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* ... */ }) {
                /* FAB content */
            }
        },
        bottomBar = {
            BottomAppBar { /* Bottom app bar content */ }
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_scaffold_fab_docked]
}

@Composable
fun ScaffoldSnackbarDemo() {
    // [START android_compose_layout_material_snackbar]
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.Image, contentDescription = "") },
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar")
                    }
                }
            )
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_snackbar]
}

@Composable
fun ScaffoldSnackbarResultDemo() {
    // [START android_compose_layout_material_snackbar_result]
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.Image, contentDescription = "") },
                onClick = {
                    scope.launch {
                        val result = snackbarHostState
                            .showSnackbar(
                                message = "Snackbar",
                                actionLabel = "Action",
                                // Defaults to SnackbarDuration.Short
                                duration = SnackbarDuration.Indefinite
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                /* Handle snackbar action performed */
                            }
                            SnackbarResult.Dismissed -> {
                                /* Handle snackbar dismissed */
                            }
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_snackbar_result]
}

@Composable
fun DrawerDemo() {
    // [START android_compose_layout_material_modal_drawer]
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                // ...other drawer items
            }
        }
    ) {
        // Screen content
    }
    // [END android_compose_layout_material_modal_drawer]
}

@Composable
fun DrawerGesturesDemo() {
    // [START android_compose_layout_material_modal_drawer_gestures]
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                // Drawer contents
            }
        },
        gesturesEnabled = false
    ) {
        // Screen content
    }
    // [END android_compose_layout_material_modal_drawer_gestures]
}

@Composable
fun DrawerStateDemo() {
    // [START android_compose_layout_material_modal_drawer_programmatic]
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { /* Drawer content */ }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show drawer") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
    }
    // [END android_compose_layout_material_modal_drawer_programmatic]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemo() {
    // [START android_compose_layout_material_bottom_sheet]
    ModalBottomSheet(onDismissRequest = { /* Executed when the sheet is dismissed */ }) {
        // Sheet content
    }
    // [END android_compose_layout_material_bottom_sheet]

    // [START android_compose_layout_material_bottom_sheet2]
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        // [END_EXCLUDE]

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
                }
            }
        }
    }
    // [END android_compose_layout_material_bottom_sheet2]
}
