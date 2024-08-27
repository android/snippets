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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.layouts.CustomLayoutsSnippet2.firstBaselineToTop

private object CustomLayoutsSnippet1 {
    /* Can't be compiled without returning layout() from Modifier.layout. See next snippet for
    possible changes.*/

    // [START android_compose_layouts_modifier_basic]
    fun Modifier.customLayoutModifier() =
        layout { measurable, constraints ->
            // [START_EXCLUDE]
            val placeable = measurable.measure(constraints)

            layout(placeable.width, placeable.width) {
                placeable.placeRelative(0, 0)
            }
            // [END_EXCLUDE]
        }
    // [END android_compose_layouts_modifier_basic]
}

private object CustomLayoutsSnippet2 {
    // [START android_compose_layouts_first_baseline]
    fun Modifier.firstBaselineToTop(
        firstBaselineToTop: Dp
    ) = layout { measurable, constraints ->
        // Measure the composable
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable gets placed
            placeable.placeRelative(0, placeableY)
        }
    }
    // [END android_compose_layouts_first_baseline]
}

// [START android_compose_layouts_first_baseline_usage]
@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    MyApplicationTheme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    MyApplicationTheme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}
// [END android_compose_layouts_first_baseline_usage]

private object CustomLayoutsSnippet4 {
    // [START android_compose_layouts_custom_layout_basic]
    @Composable
    fun MyBasicColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->
            // measure and position children given constraints logic here
            // [START_EXCLUDE]
            val placeables = measurables.map { measurable ->
                // Measure each children
                measurable.measure(constraints)
            }

            // Set the size of the layout as big as it can
            layout(constraints.maxWidth, constraints.maxHeight) {
                // Track the y co-ord we have placed children up to
                var yPosition = 0

                // Place children in the parent layout
                placeables.forEach { placeable ->
                    // Position item on the screen
                    placeable.placeRelative(x = 0, y = yPosition)

                    // Record the y co-ord placed up to
                    yPosition += placeable.height
                }
            }
            // [END_EXCLUDE]
        }
    }
    // [END android_compose_layouts_custom_layout_basic]
}

private object CustomLayoutsSnippet5and6 {
    // [START android_compose_layouts_custom_layout_more_details]
    @Composable
    fun MyBasicColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->
            // Don't constrain child views further, measure them with given constraints
            // List of measured children
            val placeables = measurables.map { measurable ->
                // Measure each children
                measurable.measure(constraints)
            }

            // Set the size of the layout as big as it can
            layout(constraints.maxWidth, constraints.maxHeight) {
                // Track the y co-ord we have placed children up to
                var yPosition = 0

                // Place children in the parent layout
                placeables.forEach { placeable ->
                    // Position item on the screen
                    placeable.placeRelative(x = 0, y = yPosition)

                    // Record the y co-ord placed up to
                    yPosition += placeable.height
                }
            }
        }
    }
    // [END android_compose_layouts_custom_layout_more_details]

    // Snippet 6
    // [START android_compose_layouts_custom_layout_usage]
    @Composable
    fun CallingComposable(modifier: Modifier = Modifier) {
        MyBasicColumn(modifier.padding(8.dp)) {
            Text("MyBasicColumn")
            Text("places items")
            Text("vertically.")
            Text("We've done it by hand!")
        }
    }
    // [END android_compose_layouts_custom_layout_usage]
}

/*
Fakes needed for snippets to build:
 */

@Composable
private fun MyApplicationTheme(content: @Composable () -> Unit) {
}
