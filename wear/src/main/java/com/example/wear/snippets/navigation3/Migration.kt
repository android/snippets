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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import kotlinx.serialization.Serializable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController

// [START android_wear_navigation3_migration_destinations_nav3]
@Serializable
sealed interface MigrationScreen : NavKey {
    @Serializable
    data object Landing : MigrationScreen

    @Serializable
    data object List : MigrationScreen
}
// [END android_wear_navigation3_migration_destinations_nav3]

@Composable
fun MigrationApp() {
    val backStack = rememberNavBackStack(MigrationScreen.Landing as NavKey)
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()

    // [START android_wear_navigation3_migration_setup_nav3]
    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryProvider = entryProvider {
            entry<MigrationScreen.Landing> {
                GreetingScreen(
                    onShowList = { backStack.add(MigrationScreen.List) }
                )
            }
            entry<MigrationScreen.List> {
                ListScreen()
            }
        }
    )
    // [END android_wear_navigation3_migration_setup_nav3]
}

// [START_EXCLUDE]
@Composable
fun GreetingScreen(onShowList: () -> Unit) {
    ScreenScaffold {
        Text("Greeting Screen")
    }
}

@Composable
fun ListScreen() {
    ScreenScaffold {
        Text("List Screen")
    }
}
// [END_EXCLUDE]

// [START android_wear_navigation3_migration_destinations_nav2]
sealed class Nav2Screen {
    data object Landing : Nav2Screen()
    data object List : Nav2Screen()
}
// [END android_wear_navigation3_migration_destinations_nav2]

@Composable
fun Nav2Init() {
    // [START android_wear_navigation3_migration_init_nav2]
    val navController = rememberSwipeDismissableNavController()
    // [END android_wear_navigation3_migration_init_nav2]
}

@Composable
fun Nav3Init() {
    // [START android_wear_navigation3_migration_init_nav3]
    val backStack = rememberNavBackStack(MigrationScreen.Landing as NavKey)
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()
    // [END android_wear_navigation3_migration_init_nav3]
}

@Composable
fun Nav2Setup() {
    val navController = rememberSwipeDismissableNavController()
    // [START android_wear_navigation3_migration_setup_nav2]
    SwipeDismissableNavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            GreetingScreen(
                onShowList = { navController.navigate("list") }
            )
        }
        composable("list") {
            ListScreen()
        }
    }
    // [END android_wear_navigation3_migration_setup_nav2]
}
