package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.ui.text.style.TextOverflow

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

    Scaffold (modifier = modifier) { contentPadding ->
        NavigationRail (modifier = Modifier.padding(contentPadding)) {
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

    Scaffold (modifier = modifier) { contentPadding ->
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


