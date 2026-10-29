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
import com.example.compose.snippets.R

private fun popBackToDestination(navController: NavController) {
    // [START android_compose_navigation_backstack_pop_back_stack]
    navController.popBackStack(R.id.destinationId, true)
    // [END android_compose_navigation_backstack_pop_back_stack]
}

private fun navigateWithPopUpTo(navController: NavController) {
    // [START android_compose_navigation_backstack_pop_up_to]
    // Pop everything up to the destination_a destination off the back stack before
    // navigating to the "destination_b" destination.
    navController.navigate("destination_b") {
        popUpTo("destination_a")
    }

    // Pop everything up to and including the "destination_a" destination off
    // the back stack before navigating to the "destination_b" destination.
    navController.navigate("destination_b") {
        popUpTo("destination_a") { inclusive = true }
    }

    // Navigate to the "search” destination only if we’re not already on
    // the "search" destination, avoiding multiple copies on the top of the
    // back stack.
    navController.navigate("search") {
        launchSingleTop = true
    }
    // [END android_compose_navigation_backstack_pop_up_to]
}
