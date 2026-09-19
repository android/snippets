/*
 * Copyright 2025 The Android Open Source Project
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

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarArrangement
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun SongsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Songs Screen")
    }
}

@Composable
fun AlbumScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Album Screen")
    }
}

@Composable
fun PlaylistScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Playlist Screen")
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    SONGS("songs", "Songs", Icons.Default.MusicNote, "Songs"),
    ALBUM("album", "Album", Icons.Default.Album, "Album"),
    PLAYLISTS("playlist", "Playlist", Icons.Default.PlaylistAddCircle, "Playlist")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.SONGS -> SongsScreen()
                    Destination.ALBUM -> AlbumScreen()
                    Destination.PLAYLISTS -> PlaylistScreen()
                }
            }
        }
    }
}

@Preview()
// [START android_compose_components_navigationbarexample]
@Composable
fun NavigationBarExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}
// [END android_compose_components_navigationbarexample]

@Preview()
// [START android_compose_components_navigationrailexample]
@Composable
fun NavigationRailExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = modifier) { contentPadding ->
        NavigationRail(modifier = Modifier.padding(contentPadding)) {
            Destination.entries.forEachIndexed { index, destination ->
                NavigationRailItem(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.contentDescription
                        )
                    },
                    label = { Text(destination.label) }
                )
            }
        }
        AppNavHost(navController, startDestination)
    }
}
// [END android_compose_components_navigationrailexample]

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
// [START android_compose_components_navigationtabexample]
@Composable
fun NavigationTabExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = modifier) { contentPadding ->
        PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
            Destination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        AppNavHost(navController, startDestination)
    }
}
// [END android_compose_components_navigationtabexample]

@Preview()
// [START android_compose_expressive_components_verticalitemsnavigationbarexample]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShortNavigationBarSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")

    ShortNavigationBar {
        items.forEachIndexed { index, item ->
            ShortNavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
            )
        }
    }
}
// [END android_compose_expressive_components_verticalitemsnavigationbarexample]

@Preview()
// [START android_compose_expressive_components_horizontalitemsnavigationbarexample]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShortNavigationBarWithHorizontalItemsSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")

    Column {
        Text(
            "Note: this is configuration is better displayed in medium screen sizes.",
            Modifier.padding(16.dp),
        )

        Spacer(Modifier.height(32.dp))

        ShortNavigationBar(arrangement = ShortNavigationBarArrangement.Centered) {
            items.forEachIndexed { index, item ->
                ShortNavigationBarItem(
                    iconPosition = NavigationItemIconPosition.Start,
                    icon = {
                        Icon(
                            if (selectedItem == index) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index },
                )
            }
        }
    }
}
// [END android_compose_expressive_components_horizontalitemsnavigationbarexample]

@Preview()
// [START android_compose_expressive_components_widenavigationrailexample]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Suppress("SourceLockedOrientationActivity")
@Composable
fun WideNavigationRailResponsiveSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Search", "Settings")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Star)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.StarBorder)
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    Row(Modifier.fillMaxWidth()) {
        WideNavigationRail(
            state = state,
            header = {
                IconButton(
                    modifier =
                        Modifier
                            .padding(start = 24.dp)
                            .semantics {
                                // The button must announce the expanded or collapsed state of the rail
                                // for accessibility.
                                stateDescription =
                                    if (state.currentValue == WideNavigationRailValue.Expanded) "Expanded"
                                    else "Collapsed"
                            },
                    onClick = {
                        scope.launch {
                            if (state.targetValue == WideNavigationRailValue.Expanded) state.collapse()
                            else state.expand()
                        }
                    },
                ) {
                    if (state.targetValue == WideNavigationRailValue.Expanded) {
                        Icon(Icons.AutoMirrored.Filled.MenuOpen, "Collapse rail")
                    } else {
                        Icon(Icons.Filled.Menu, "Expand rail")
                    }
                }
            },
        ) {
            items.forEachIndexed { index, item ->
                WideNavigationRailItem(
                    railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
                    icon = {
                        val imageVector =
                            if (selectedItem == index) {
                                selectedIcons[index]
                            } else {
                                unselectedIcons[index]
                            }
                        Icon(imageVector = imageVector, contentDescription = null)
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index },
                )
            }
        }

        val textString =
            if (state.currentValue == WideNavigationRailValue.Expanded) "expanded" else "collapsed"
        Column {
            Text(modifier = Modifier.padding(16.dp), text = "Is animating: " + state.isAnimating)
            Text(modifier = Modifier.padding(16.dp), text = "The rail is $textString.")
            Text(
                modifier = Modifier.padding(16.dp),
                text =
                    "Note: The orientation of this demo has been locked to portrait mode, because" +
                            " landscape mode may result in a compact height in certain devices. For" +
                            " any compact screen dimensions, use a Navigation Bar instead.",
            )
        }
    }

    // Lock the orientation for this demo as the navigation rail may look cut off in landscape in
    // smaller screens.
    val context = LocalContext.current
    DisposableEffect(context) {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
// [END android_compose_expressive_components_widenavigationrailexample]

@Preview()
// [START android_compose_expressive_components_modalwidenavigationrailexample]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Suppress("SourceLockedOrientationActivity")
@Composable
fun ModalWideNavigationRailSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Search", "Settings")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Star)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.StarBorder)
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    Row(Modifier.fillMaxWidth()) {
        ModalWideNavigationRail(
            state = state,
            // Note: the value of expandedHeaderTopPadding depends on the layout of your screen in
            // order to achieve the best alignment.
            expandedHeaderTopPadding = 64.dp,
            header = {
                IconButton(
                    modifier =
                        Modifier
                            .padding(start = 24.dp)
                            .semantics {
                                // The button must announce the expanded or collapsed state of the rail
                                // for accessibility.
                                stateDescription =
                                    if (state.currentValue == WideNavigationRailValue.Expanded) "Expanded"
                                    else "Collapsed"
                            },
                    onClick = {
                        scope.launch {
                            if (state.targetValue == WideNavigationRailValue.Expanded) state.collapse()
                            else state.expand()
                        }
                    },
                ) {
                    if (state.targetValue == WideNavigationRailValue.Expanded)
                        Icon(Icons.AutoMirrored.Filled.MenuOpen, "Collapse rail")
                    else Icon(Icons.Filled.Menu, "Expand rail")
                }
            },
        ) {
            items.forEachIndexed { index, item ->
                WideNavigationRailItem(
                    railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
                    icon = {
                        Icon(
                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index },
                )
            }
        }

        val textString =
            if (state.currentValue == WideNavigationRailValue.Expanded) "expanded" else "collapsed"
        Column {
            Text(modifier = Modifier.padding(16.dp), text = "The rail is $textString.")
            Text(
                modifier = Modifier.padding(16.dp),
                text =
                    "Note: The orientation of this demo has been locked to portrait mode, because" +
                            " landscape mode may result in a compact height in certain devices. For" +
                            " any compact screen dimensions, use a Navigation Bar instead.",
            )
        }

        // Lock the orientation for this demo as the navigation rail may look cut off in landscape
        // in smaller screens.
        val context = LocalContext.current
        DisposableEffect(context) {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            onDispose {
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}
// [END android_compose_expressive_components_modalwidenavigationrailexample]

@Preview()
// [START android_compose_expressive_components_dismissiblemodalwidenavigationrailexample]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DismissibleModalWideNavigationRailSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Search", "Settings")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.Settings)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Search, Icons.Outlined.Settings)
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    Row(Modifier.fillMaxSize()) {
        ModalWideNavigationRail(state = state, hideOnCollapse = true) {
            items.forEachIndexed { index, item ->
                WideNavigationRailItem(
                    railExpanded = true,
                    icon = {
                        Icon(
                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                            contentDescription = null,
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        scope.launch { state.collapse() }
                    },
                )
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val currentPage = items.get(selectedItem)
            Button(onClick = { scope.launch { state.expand() } }, Modifier.padding(32.dp)) {
                Text(text = "$currentPage Page\nOpen modal rail", textAlign = TextAlign.Center)
            }
        }
    }
}
// [END android_compose_expressive_components_dismissiblemodalwidenavigationrailexample]
