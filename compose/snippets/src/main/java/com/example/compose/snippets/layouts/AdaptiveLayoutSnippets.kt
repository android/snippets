/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
// [START android_compose_adaptive_layouts_basic]
@Composable
fun MyApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    // Perform logic on the size class to decide whether to show the top app bar.
    val showTopAppBar = windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT

    // MyScreen knows nothing about window sizes, and performs logic based on a Boolean flag.
    MyScreen(
        showTopAppBar = showTopAppBar,
        /* ... */
    )
}
// [END android_compose_adaptive_layouts_basic]
@Composable
fun MyScreen(showTopAppBar: Boolean) {
    // your content here
}

// [START android_compose_layouts_adaptive_pane]
@Composable
fun AdaptivePane(
    showOnePane: Boolean,
    /* ... */
) {
    if (showOnePane) {
        OnePane(/* ... */)
    } else {
        TwoPane(/* ... */)
    }
}
// [END android_compose_layouts_adaptive_pane]

@Composable
private fun WindowSizeClassSnippet() {
    // [START android_compose_windowsizeclass]
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    // [END android_compose_windowsizeclass]
}

@Composable
fun OnePane() {
    // your content here
}
@Composable
fun TwoPane() {
    // your content here
}
private object ManualMeasurementsSnippets {
    // [START android_compose_layouts_manual_measurements]
    @Composable
    fun Card(/* ... */) {
        BoxWithConstraints {
            if (maxWidth < 400.dp) {
                Column {
                    Image(/* ... */)
                    Title(/* ... */)
                }
            } else {
                Row {
                    Column {
                        Title(/* ... */)
                        Description(/* ... */)
                    }
                    Image(/* ... */)
                }
            }
        }
    }
    // [END android_compose_layouts_manual_measurements]
    @Composable
    private fun Image() {
    }
    @Composable
    private fun Description() {
    }
    @Composable
    private fun Title() {
    }
}

private object ManualMeasurementSnippets2 {
    // [START android_compose_layouts_manual_measurements_2]
    @Composable
    fun Card(
        imageUrl: String,
        title: String,
        description: String
    ) {
        BoxWithConstraints {
            if (maxWidth < 400.dp) {
                Column {
                    Image(imageUrl)
                    Title(title)
                }
            } else {
                Row {
                    Column {
                        Title(title)
                        Description(description)
                    }
                    Image(imageUrl)
                }
            }
        }
    }
    // [END android_compose_layouts_manual_measurements_2]
    @Composable
    private fun Image(imageUrl: String) {
    }
    @Composable
    private fun Description(description: String) {
    }
    @Composable
    private fun Title(title: String) {
    }
}

private object ManualMeasurementSnippets3 {
    // [START android_compose_layouts_manual_measurements_3]
    @Composable
    fun Card(
        imageUrl: String,
        title: String,
        description: String
    ) {
        var showMore by remember { mutableStateOf(false) }

        BoxWithConstraints {
            if (maxWidth < 400.dp) {
                Column {
                    Image(imageUrl)
                    Title(title)
                }
            } else {
                Row {
                    Column {
                        Title(title)
                        Description(
                            description = description,
                            showMore = showMore,
                            onShowMoreToggled = { newValue ->
                                showMore = newValue
                            }
                        )
                    }
                    Image(imageUrl)
                }
            }
        }
    }
    // [END android_compose_layouts_manual_measurements_3]
    @Composable
    private fun Image(imageUrl: String) {
    }
    @Composable
    private fun Description(
        description: String,
        showMore: Boolean,
        onShowMoreToggled: (Boolean) -> Unit
    ) {
    }
    @Composable
    private fun Title(title: String) {
    }
}
