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

package com.example.compose.snippets.navigation.dialogdestinations

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

// [START android_compose_navigation_dialog_destinations_dialog]
@Serializable
object Home
@Serializable
object Settings
@Composable
fun HomeScreen(onNavigateToSettings: () -> Unit) {
    Column {
        Text("Home")
        Button(onClick = onNavigateToSettings) {
            Text("Open settings")
        }
    }
}

// This screen will be displayed as a dialog.
@Composable
fun SettingsScreen() {
    Text("Settings")
    // ...
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Home) {
        composable<Home> { HomeScreen(onNavigateToSettings = { navController.navigate(route = Settings) }) }
        dialog<Settings> { SettingsScreen() }
    }
}
// [END android_compose_navigation_dialog_destinations_dialog]
