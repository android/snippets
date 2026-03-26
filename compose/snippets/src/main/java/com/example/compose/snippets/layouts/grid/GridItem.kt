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

@file:OptIn(ExperimentalGridApi::class)

package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridFlow
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun GridFlowColumn() {
    // [START android_compose_layout_grid_flow]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            gap(8.dp)
            flow = GridFlow.Column // Grid tries to place items to fill the column
        },
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_flow]
}

@Composable
fun GridFlowRow() {
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            gap(8.dp)
            flow = GridFlow.Row // Grid tries to place items to fill the row
        },
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
}

@Preview(showBackground = true, widthDp = 696, heightDp = 286)
@Composable
private fun GridFlowComparison() {
    Grid(
        config = {
            repeat(2) {
                column(320.dp)
                column(320.dp)
            }
            row(1f)
            gap(48.dp)
        }
    ) {
        GridFlowRow()
        GridFlowColumn()
    }
}

@Preview(showBackground = true, widthDp = 454)
@Composable
fun GridItemPosition() {
    GridWithIndices {
        // [START android_compose_layout_grid_item]
        Grid(
            config = {
                repeat(2) {
                    column(160.dp)
                }
                repeat(3) {
                    row(90.dp)
                }
                gap(8.dp)
            }
        ) {
            Card1()
            Card2(modifier = Modifier.gridItem(row = 2, column = 2))
            Card3(modifier = Modifier.gridItem(row = -1, column = -2))
        }
        // [END android_compose_layout_grid_item]
    }
}

@Preview(showBackground = true)
@Composable
fun GridItemWithNegativeIndex() {
    GridWithIndices {
        // [START android_compose_layout_grid_item_with_negative_index]
        Grid(
            config = {
                repeat(2) {
                    column(160.dp)
                }
                repeat(3) {
                    row(90.dp)
                }
                gap(8.dp)
            }
        ) {
            Card1()
            Card2(modifier = Modifier.gridItem(row = 2))
            Card3(modifier = Modifier.gridItem(row = -1, column = -1))
        }
        // [END android_compose_layout_grid_item_with_negative_index]
    }
}

@Preview(showBackground = true)
@Composable
fun MixedWithAutoPlacement() {
    // [START android_compose_layout_grid_mixed_with_auto_placement]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            rowGap(16.dp)
            columnGap(8.dp)
        }
    ) {
        Card1()
        Card2(modifier = Modifier.gridItem(row = 2, column = 2))
        Card3()
        Card4(modifier = Modifier.gridItem(row = 3, column = 1))
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_mixed_with_auto_placement]
}

@Preview(showBackground = true)
@Composable
fun CounterIntuitiveKeyboardTraversal() {
    // [START android_compose_layout_grid_counter_intuitive_keyboard_traversal]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            rowGap(8.dp)
            columnGap(8.dp)
        },
    ) {
        Card6(modifier = Modifier.gridItem(row = 3, column = 2))
        Card5(modifier = Modifier.gridItem(row = 3, column = 1))
        Card4(modifier = Modifier.gridItem(row = 2, column = 2))
        Card3(modifier = Modifier.gridItem(row = 2, column = 1))
        Card2(modifier = Modifier.gridItem(row = 1, column = 2))
        Card1(modifier = Modifier.gridItem(row = 1, column = 1))
    }
    // [END android_compose_layout_grid_counter_intuitive_keyboard_traversal]
}

@Composable
private fun GridWithIndices(
    margin: Dp = 16.dp,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    innerGrid: @Composable () -> Unit = {}
) {
    Grid(
        config = {
            column(GridTrackSize.MaxContent)
            column(160.dp)
            column(160.dp)

            row(GridTrackSize.Auto)
            row(90.dp)
            row(90.dp)
            row(90.dp)
            gap(8.dp)
        }
    ) {
        repeat(3) {
            val index = it + 1
            Text(
                "row = $index / row = -${3 - it}",
                style = style,
                modifier = Modifier
                    .gridItem(row = index + 1, alignment = Alignment.CenterStart)
                    .padding(end = margin)
            )
        }
        repeat(2) {
            val index = it + 1
            Text(
                "column = $index / column = -${2 - it}",
                style = style,
                modifier = Modifier
                    .gridItem(column = index + 1, alignment = Alignment.TopCenter)
                    .padding(bottom = margin)
            )
        }
        Box(modifier = Modifier.gridItem(row = 2, column = 2, rowSpan = 3, columnSpan = 2)) {
            innerGrid()
        }
    }
}