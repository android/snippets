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

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridFlow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun GridFlow() {
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
    // [END android_compose_layout_grid_flow]
}

@Preview(showBackground = true)
@Composable
fun GridItem() {
    // [START android_compose_layout_grid_item]
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
        Card3(modifier = Modifier.gridItem(row = 3))
    }
    // [END android_compose_layout_grid_item]
}

@Preview(showBackground = true)
@Composable
fun GridItemWithNegativeIndex() {
    // [START android_compose_layout_grid_item_with_negative_index]
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
        Card2(modifier = Modifier.gridItem(row = 2))
        Card3(modifier = Modifier.gridItem(row = -1, column = -1))
    }
    // [END android_compose_layout_grid_item_with_negative_index]
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
