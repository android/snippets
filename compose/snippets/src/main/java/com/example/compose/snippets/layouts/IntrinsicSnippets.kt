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
* Copyright 2022 The Android Open Source Project
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

private object IntrinsicsSnippet1 {

    // [START android_compose_intrinsics_twotexts]
    @Composable
    fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
        Row(modifier = modifier) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .wrapContentWidth(Alignment.Start),
                text = text1
            )
            VerticalDivider(
                color = Color.Black,
                modifier = Modifier.fillMaxHeight().width(1.dp)
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .wrapContentWidth(Alignment.End),

                text = text2
            )
        }
    }
    // [END android_compose_intrinsics_twotexts]

    // @Preview
    @Composable
    fun TwoTextsPreview() {
        MaterialTheme {
            Surface {
                TwoTexts(text1 = "Hi", text2 = "there")
            }
        }
    }
}

private object IntrinsicsSnippet2 {

    // [START android_compose_intrinsics_twotexts2]
    @Composable
    fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
        Row(modifier = modifier.height(IntrinsicSize.Min)) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .wrapContentWidth(Alignment.Start),
                text = text1
            )
            VerticalDivider(
                color = Color.Black,
                modifier = Modifier.fillMaxHeight().width(1.dp)
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .wrapContentWidth(Alignment.End),

                text = text2
            )
        }
    }

    // @Preview
    @Composable
    fun TwoTextsPreview() {
        MaterialTheme {
            Surface {
                TwoTexts(text1 = "Hi", text2 = "there")
            }
        }
    }
    // [END android_compose_intrinsics_twotexts2]
}

private object IntrinsicsSnippet3 {
    // [START android_compose_intrinsics_custom_layout]
    @Composable
    fun MyCustomComposable(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            content = content,
            modifier = modifier,
            measurePolicy = object : MeasurePolicy {
                override fun MeasureScope.measure(
                    measurables: List<Measurable>,
                    constraints: Constraints
                ): MeasureResult {
                    val childConstraints = constraints.copy(minHeight = 0)
                    val placeables = measurables.map { measurable ->
                        measurable.measure(childConstraints)
                    }
                    val width = constraints.constrainWidth(
                        placeables.maxOfOrNull { it.width } ?: 0
                    )
                    val height = constraints.constrainHeight(
                        placeables.sumOf { it.height }
                    )
                    return layout(width, height) {
                        var yPosition = 0
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = 0, y = yPosition)
                            yPosition += placeable.height
                        }
                    }
                }

                override fun IntrinsicMeasureScope.minIntrinsicHeight(
                    measurables: List<IntrinsicMeasurable>,
                    width: Int
                ): Int {
                    return measurables.sumOf { measurable ->
                        measurable.minIntrinsicHeight(width)
                    }
                }

                // Other intrinsics related methods have a default value,
                // you can override only the methods that you need.
            }
        )
    }
    // [END android_compose_intrinsics_custom_layout]
}

private object IntrinsicsSnippet4 {
    // [START android_compose_intrinsics_custom_modifier]
    fun Modifier.myCustomModifier(padding: Dp = 8.dp) =
        this then object : LayoutModifier {
            override fun MeasureScope.measure(
                measurable: Measurable,
                constraints: Constraints
            ): MeasureResult {
                val paddingPx = padding.roundToPx()
                val horizontal = paddingPx * 2
                val vertical = paddingPx * 2
                val placeable = measurable.measure(
                    constraints.offset(-horizontal, -vertical)
                )
                val width = constraints.constrainWidth(placeable.width + horizontal)
                val height = constraints.constrainHeight(placeable.height + vertical)
                return layout(width, height) {
                    placeable.place(paddingPx, paddingPx)
                }
            }

            override fun IntrinsicMeasureScope.minIntrinsicWidth(
                measurable: IntrinsicMeasurable,
                height: Int
            ): Int {
                val paddingPx = padding.roundToPx()
                val childHeight = if (height == Constraints.Infinity) {
                    Constraints.Infinity
                } else {
                    (height - paddingPx * 2).coerceAtLeast(0)
                }
                return measurable.minIntrinsicWidth(childHeight) + paddingPx * 2
            }

            // Other intrinsics related methods have a default value,
            // you can override only the methods that you need.
        }
    // [END android_compose_intrinsics_custom_modifier]
}

@Preview
@Composable
private fun IntrinsicsCustomLayoutPreview() {
    MaterialTheme {
        Surface {
            IntrinsicsSnippet3.MyCustomComposable(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text("First line")
                with(IntrinsicsSnippet4) {
                    Text(
                        text = "Second line",
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .myCustomModifier(padding = 8.dp)
                    )
                }
            }
        }
    }
}
