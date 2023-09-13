/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.compose.snippets

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.snippets.animations.AnimationExamplesScreen
import com.example.compose.snippets.components.AppBarExamples
import com.example.compose.snippets.components.ButtonExamples
import com.example.compose.snippets.components.ChipExamples
import com.example.compose.snippets.components.ComponentsScreen
import com.example.compose.snippets.components.DialogExamples
import com.example.compose.snippets.components.FloatingActionButtonExamples
import com.example.compose.snippets.components.ProgressIndicatorExamples
import com.example.compose.snippets.components.ScaffoldExample
import com.example.compose.snippets.components.SliderExamples
import com.example.compose.snippets.components.SwitchExamples
import com.example.compose.snippets.graphics.BitmapFromComposableSnippet
import com.example.compose.snippets.graphics.BrushExamplesScreen
import com.example.compose.snippets.images.ImageExamplesScreen
import com.example.compose.snippets.landing.LandingScreen
import com.example.compose.snippets.navigation.Destination
import com.example.compose.snippets.navigation.TopComponentsDestination
import com.example.compose.snippets.ui.theme.SnippetsTheme
import com.example.topcomponents.CardExamples

class SnippetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
        setContent {
            SnippetsTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "LandingScreen") {
                        composable("LandingScreen") {
                            LandingScreen { navController.navigate(it.route) }
                        }
                        Destination.values().forEach { destination ->
                            composable(destination.route) {
                                when (destination) {
                                    Destination.BrushExamples -> BrushExamplesScreen()
                                    Destination.ImageExamples -> ImageExamplesScreen()
                                    Destination.AnimationQuickGuideExamples -> AnimationExamplesScreen()
                                    Destination.ScreenshotExample -> BitmapFromComposableSnippet()
                                    Destination.ComponentsExamples -> ComponentsScreen {
                                        navController.navigate(
                                            it.route
                                        )
                                    }
                                }
                            }
                        }
                        TopComponentsDestination.values().forEach { destination ->
                            composable(destination.route) {
                                when (destination) {
                                    TopComponentsDestination.CardExamples -> CardExamples()
                                    TopComponentsDestination.SwitchExamples -> SwitchExamples()
                                    TopComponentsDestination.SliderExamples -> SliderExamples()
                                    TopComponentsDestination.DialogExamples -> DialogExamples()
                                    TopComponentsDestination.ChipExamples -> ChipExamples()
                                    TopComponentsDestination.FloatingActionButtonExamples -> FloatingActionButtonExamples()
                                    TopComponentsDestination.ButtonExamples -> ButtonExamples()
                                    TopComponentsDestination.ProgressIndicatorExamples -> ProgressIndicatorExamples()
                                    TopComponentsDestination.ScaffoldExample -> ScaffoldExample()
                                    TopComponentsDestination.AppBarExamples -> AppBarExamples()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
