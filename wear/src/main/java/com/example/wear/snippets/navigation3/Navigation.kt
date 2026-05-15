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

package com.example.wear.snippets.navigation3

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import kotlinx.serialization.Serializable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator

// [START android_wear_navigation3_destinations]
@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Details(val itemId: String) : Screen
}
// [END android_wear_navigation3_destinations]

@Composable
fun WearApp() {
    // [START android_wear_navigation3_setup]
    // 1. Create the persistent back stack starting at the Home screen
    val backStack = rememberNavBackStack(Screen.Home)

    // 2. Initialize the Wear OS swipe-to-dismiss strategy
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()

    // 3. Render the NavDisplay
    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryProvider = entryProvider {
            // 4. Map keys to Composables
            entry<Screen.Home> {
                HomeScreen(
                    onNavigateToDetails = { id -> backStack.add(Screen.Details(id)) }
                )
            }
            entry<Screen.Details> { key ->
                DetailsScreen(
                    itemId = key.itemId,
                    onBack = { backStack.removeLast() }
                )
            }
        }
    )
    // [END android_wear_navigation3_setup]
}

// [START_EXCLUDE]
@Composable
fun HomeScreen(onNavigateToDetails: (String) -> Unit) {
    ScreenScaffold {
        Text("Home Screen")
    }
}

@Composable
fun DetailsScreen(itemId: String, onBack: () -> Unit) {
    ScreenScaffold {
        Text("Details Screen for item $itemId")
    }
}

class HomeViewModel : androidx.lifecycle.ViewModel()
// [END_EXCLUDE]

@Composable
fun WearAppWithViewModel() {
    val backStack = rememberNavBackStack(Screen.Home)
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()

    // [START android_wear_navigation3_viewmodel]
    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Home> {
                // Any viewModel() requested here will be scoped to this NavEntry
                val viewModel: HomeViewModel = viewModel()
                // [START_EXCLUDE silent]
                HomeScreen(onNavigateToDetails = {})
                // [END_EXCLUDE]
            }
        }
    )
    // [END android_wear_navigation3_viewmodel]
}
