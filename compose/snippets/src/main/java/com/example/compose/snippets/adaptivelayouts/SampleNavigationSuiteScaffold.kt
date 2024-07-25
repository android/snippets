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

package com.example.compose.snippets.adaptivelayouts

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.compose.snippets.R

// [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_destinations]
enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    HOME(R.string.home, Icons.Default.Home, R.string.home),
    FAVORITES(R.string.favorites, Icons.Default.Favorite, R.string.favorites),
    SHOPPING(R.string.shopping, Icons.Default.ShoppingCart, R.string.shopping),
    PROFILE(R.string.profile, Icons.Default.AccountBox, R.string.profile),
}
// [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_destinations]

@Composable
fun SampleNavigationSuiteScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_remember]
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_remember]

    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_items]
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = stringResource(it.contentDescription)
                        )
                    },
                    label = { Text(stringResource(it.label)) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        // TODO: Destination content.
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_items]

    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_content]
    NavigationSuiteScaffold(
        navigationSuiteItems = { /*...*/ }
    ) {
        // Destination content.
        when (currentDestination) {
            AppDestinations.HOME -> HomeDestination()
            AppDestinations.FAVORITES -> FavoritesDestination()
            AppDestinations.SHOPPING -> ShoppingDestination()
            AppDestinations.PROFILE -> ProfileDestination()
        }
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_content]
}

@Composable
fun SampleNavigationSuiteScaffoldColors() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_container_color]
    NavigationSuiteScaffold(
        navigationSuiteItems = { /* ... */ },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        // Content...
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_container_color]

    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_suite_colors]
    NavigationSuiteScaffold(
        navigationSuiteItems = { /* ... */ },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Color.Transparent,
        )
    ) {
        // Content...
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_suite_colors]

    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_item_colors]
    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = stringResource(it.contentDescription)
                        )
                    },
                    label = { Text(stringResource(it.label)) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                    colors = myNavigationSuiteItemColors,
                )
            }
        },
    ) {
        // Content...
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_item_colors]
}

@Composable
fun SampleNavigationSuiteScaffoldCustomType() {
    // [START android_compose_adaptivelayouts_sample_navigation_suite_scaffold_layout_type]
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = { /* ... */ },
        layoutType = customNavSuiteType,
    ) {
        // Content...
    }
    // [END android_compose_adaptivelayouts_sample_navigation_suite_scaffold_layout_type]
}

@Composable
fun HomeDestination() {}

@Composable
fun FavoritesDestination() {}

@Composable
fun ShoppingDestination() {}

@Composable
fun ProfileDestination() {}
