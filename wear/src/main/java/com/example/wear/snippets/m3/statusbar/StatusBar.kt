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

package com.example.wear.snippets.m3.statusbar

import android.view.View
import android.view.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.suppressStatusBar
import androidx.wear.compose.material3.timeTextCurvedText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices

// [START android_wear_status_bar_sample_app]
@Composable
fun GsbSampleApp() {
    // AppScaffold coordinates the system status bar across the app
    // and falls back to rendering TimeText on Wear OS 7 and earlier.
    AppScaffold {
        val columnState = rememberTransformingLazyColumnState()

        // ScreenScaffold includes status bar top inset clearance in
        // contentPadding and hides/shows the status bar during list scrolling.
        ScreenScaffold(scrollState = columnState) { contentPadding ->
            TransformingLazyColumn(
                state = columnState,
                contentPadding = contentPadding,
            ) {
                item {
                    ListHeader { Text("Hello Status Bar") }
                }
                items(count = 15) { index ->
                    Button(onClick = { /* Handle click */ }) {
                        Text("Item #${index + 1}")
                    }
                }
            }
        }
    }
}
// [END android_wear_status_bar_sample_app]

@Composable
fun StaticScreenStatusBar() {
    // [START android_wear_status_bar_static_screen]
    AppScaffold {
        ScreenScaffold { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                Text("Static Screen Content")
            }
        }
    }
    // [END android_wear_status_bar_static_screen]
}

@Composable
fun ScreenLevelImmersiveStatusBar() {
    // [START android_wear_status_bar_screen_immersive]
    AppScaffold {
        ScreenScaffold(modifier = Modifier.suppressStatusBar()) { contentPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                // Full-screen immersive content
            }
        }
    }
    // [END android_wear_status_bar_screen_immersive]
}

@Composable
fun AppLevelImmersiveStatusBar() {
    val navController = rememberSwipeDismissableNavController()
    // [START android_wear_status_bar_app_immersive]
    // Apply Modifier.suppressStatusBar() to AppScaffold to suppress the status bar
    // and collapse insets across all child screens.
    AppScaffold(modifier = Modifier.suppressStatusBar()) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "home",
        ) {
            // Nested screens inherit suppression and 0.dp padding
        }
    }
    // [END android_wear_status_bar_app_immersive]
}

@Composable
fun CustomTimeTextOverrides() {
    // [START android_wear_status_bar_custom_timetext]
    // Screen-level custom TimeText override:
    ScreenScaffold(
        timeText = { TimeText { timeTextCurvedText("Custom Status") } },
    ) {
        // Content
    }

    // App-level opt-out using in-app TimeText:
    AppScaffold(
        timeText = { TimeText() },
    ) {
        // Screens
    }
    // [END android_wear_status_bar_custom_timetext]
}

fun monitorStatusBarInsets(rootView: View) {
    // [START android_wear_status_bar_view_insets]
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
        val statusBarInsets = windowInsets.getInsets(
            WindowInsetsCompat.Type.statusBars()
        )

        if (statusBarInsets.top > 0) {
            // status bar is visible: remove legacy 12 o'clock UI and apply top inset padding
            view.updatePadding(top = statusBarInsets.top)
        } else {
            // status bar is not visible: fall back to legacy UI and padding
        }

        WindowInsetsCompat.CONSUMED
    }
    // [END android_wear_status_bar_view_insets]
}

fun hideAndShowStatusBar(window: Window) {
    // [START android_wear_status_bar_view_controller]
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)

    // Hide the status bar
    insetsController.hide(WindowInsetsCompat.Type.statusBars())

    // Show the status bar
    insetsController.show(WindowInsetsCompat.Type.statusBars())
    // [END android_wear_status_bar_view_controller]
}

@WearPreviewDevices
@Composable
fun GsbSampleAppPreview() {
    GsbSampleApp()
}
