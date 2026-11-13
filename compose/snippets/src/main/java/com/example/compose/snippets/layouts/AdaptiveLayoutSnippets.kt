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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true).windowSizeClass
) {
    // Decide whether to show the top app bar based on window size class.
    val showTopAppBar = windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    // MyScreen logic is based on the showTopAppBar boolean flag.
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
object ManualMeasurementsSnippets {
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
        CardImagePlaceholder()
    }
    @Composable
    private fun Description() {
        CardDescription(SampleCardDescription)
    }
    @Composable
    private fun Title() {
        CardTitle(SampleCardTitle)
    }
}

object ManualMeasurementSnippets2 {
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
        CardImagePlaceholder()
    }
    @Composable
    private fun Description(description: String) {
        CardDescription(description)
    }
    @Composable
    private fun Title(title: String) {
        CardTitle(title)
    }
}

object ManualMeasurementSnippets3 {
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
        CardImagePlaceholder()
    }
    @Composable
    private fun Description(
        description: String,
        showMore: Boolean,
        onShowMoreToggled: (Boolean) -> Unit
    ) {
        Column {
            CardDescription(description, maxLines = if (showMore) Int.MAX_VALUE else 1)
            TextButton(onClick = { onShowMoreToggled(!showMore) }) {
                Text(if (showMore) "Show less" else "Show more")
            }
        }
    }
    @Composable
    private fun Title(title: String) {
        CardTitle(title)
    }
}

// Sample content for the Card snippets above, so their previews show how the layout changes with
// the available width.
const val SampleCardTitle = "Mountain lake"
const val SampleCardDescription =
    "A calm lake surrounded by pine forest, a short walk from the trailhead. Best visited early in the morning."

@Composable
private fun CardImagePlaceholder() {
    Box(
        Modifier
            .padding(8.dp)
            .size(80.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
    )
}

@Composable
private fun CardTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
}

@Composable
private fun CardDescription(description: String, maxLines: Int = Int.MAX_VALUE) {
    Text(
        description,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 8.dp).widthIn(max = 280.dp)
    )
}
