/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.navigation3.animations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.ContentGreen
import com.example.compose.snippets.navigation3.ContentMauve
import com.example.compose.snippets.navigation3.ContentOrange
import kotlinx.serialization.Serializable

// [START android_compose_navigation3_animations_1]
@Serializable
data object ScreenA : NavKey

@Serializable
data object ScreenB : NavKey

@Serializable
data object ScreenC : NavKey

class AnimatedNavDisplayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold { paddingValues ->

                val backStack = rememberNavBackStack(ScreenA)

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<ScreenA> {
                            ContentOrange("This is Screen A") {
                                Button(onClick = { backStack.add(ScreenB) }) {
                                    Text("Go to Screen B")
                                }
                            }
                        }
                        entry<ScreenB> {
                            ContentMauve("This is Screen B") {
                                Button(onClick = { backStack.add(ScreenC) }) {
                                    Text("Go to Screen C")
                                }
                            }
                        }
                        entry<ScreenC>(
                            metadata = NavDisplay.transitionSpec {
                                // Slide new content up, keeping the old content in place underneath
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(1000)
                                ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                            } + NavDisplay.popTransitionSpec {
                                // Slide old content down, revealing the new content in place underneath
                                EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(1000)
                                    )
                            } + NavDisplay.predictivePopTransitionSpec {
                                // Slide old content down, revealing the new content in place underneath
                                EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(1000)
                                    )
                            }
                        ) {
                            ContentGreen("This is Screen C")
                        }
                    },
                    transitionSpec = {
                        // Slide in from right when navigating forward
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                    predictivePopTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
// [END android_compose_navigation3_animations_1]
