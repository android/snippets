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

import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.compose.snippets.R
import kotlinx.serialization.Serializable

@Serializable
private object A

@Serializable
private object B

private fun popBackToDestination(navController: NavController) {
    // [START android_compose_navigation_backstack_pop_back_stack]
    navController.popBackStack(R.id.destinationId, true)
    // [END android_compose_navigation_backstack_pop_back_stack]
}

private class FailedPopActivity : ComponentActivity() {
    private lateinit var navController: NavController

    private fun popBackOrFinish() {
        // [START android_compose_navigation_backstack_handle_failed_pop]
        // ...

        if (!navController.popBackStack()) {
            // Call finish() on your Activity.
            finish()
        }
        // [END android_compose_navigation_backstack_handle_failed_pop]
    }
}

private fun navigateAndSaveState(navController: NavController, route: Any) {
    // [START android_compose_navigation_backstack_save_state]
    navController.navigate(
        route = route,
        navOptions = navOptions {
            popUpTo<A> { saveState = true }
            restoreState = true
        }
    )
    // [END android_compose_navigation_backstack_save_state]
}

@Composable
private fun DestinationB() {
}

// [START android_compose_navigation_backstack_compose_example]
@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = A
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<A> {
            DestinationA(
                onNavigateToB = {
                    // Pop everything up to, and including, the A destination off
                    // the back stack, saving the back stack and the state of its
                    // destinations.
                    // Then restore any previous back stack state associated with
                    // the B destination.
                    // Finally navigate to the B destination.
                    navController.navigate(route = B) {
                        popUpTo<A> {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
            )
        }
        composable<B> { DestinationB(/* ... */) }
    }
}

@Composable
fun DestinationA(onNavigateToB: () -> Unit) {
    Button(onClick = onNavigateToB) {
        Text("Go to A")
    }
}
// [END android_compose_navigation_backstack_compose_example]

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
