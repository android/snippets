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

package com.example.compose.snippets.navigation.backstack

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

private fun navigateToBottomNavRoute(navController: NavController, selectedBottomNavRoute: String) {
    // [START android_compose_navigation_multi_back_stacks_nav_options]
    // Use the navigate() method that takes a navOptions DSL Builder.
    navController.navigate(selectedBottomNavRoute) {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
    }
    // [END android_compose_navigation_multi_back_stacks_nav_options]
}
