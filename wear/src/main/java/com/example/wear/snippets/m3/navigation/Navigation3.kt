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

package com.example.wear.snippets.m3.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import kotlinx.serialization.Serializable

// [START android_wear_nav3_destinations]
@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Details(val itemId: String) : Screen
}
// [END android_wear_nav3_destinations]

// [START android_wear_nav3_setup]
@Composable
fun WearApp() {
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
}
// [END android_wear_nav3_setup]

@Composable
fun WearAppWithViewModel() {
    val backStack = rememberNavBackStack(Screen.Home)
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()

    // [START android_wear_nav3_viewmodel]
    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>()
        ),
        entryProvider = entryProvider {
            entry<Screen.Home> {
                // Any viewModel() requested here will be scoped to this NavEntry
                val viewModel: HomeViewModel = viewModel()
                HomeScreen(onNavigateToDetails = {})
            }
        }
    )
    // [END android_wear_nav3_viewmodel]
}

// Mock implementations to make it compile
@Composable
fun HomeScreen(onNavigateToDetails: (String) -> Unit) {
    // Mock
}

@Composable
fun DetailsScreen(itemId: String, onBack: () -> Unit) {
    // Mock
}

class HomeViewModel : ViewModel() {
    // Mock
}
