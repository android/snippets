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

package com.example.compose.snippets.navigation.circular

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
private fun CircularNavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "a") {
        composable("a") {
            Button(onClick = { navController.navigate("b") }) {
                Text("Go to B")
            }
        }
        composable("b") {
            Button(onClick = { navController.navigate("c") }) {
                Text("Go to C")
            }
        }
        // [START android_compose_navigation_circular_pop_up_to]
        // When creating your `NavGraph` in your `NavHost`.
        composable("c") {
            DestinationC(
                onNavigateToA = {
                    navController.navigate("a") {
                        popUpTo("a") {
                            inclusive = true
                        }
                    }
                },
            )
        }
        // [END android_compose_navigation_circular_pop_up_to]
    }
}

@Composable
private fun DestinationC(onNavigateToA: () -> Unit) {
    Button(onClick = onNavigateToA) {
        Text("Go to A")
    }
}
