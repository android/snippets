package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridFlow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
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

@OptIn(ExperimentalGridApi::class)
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

@OptIn(ExperimentalGridApi::class)
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

@OptIn(ExperimentalGridApi::class)
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

@OptIn(ExperimentalGridApi::class)
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

@Preview(showBackground = true)
@Composable
private fun GridFlowPreview() {
    GridFlow()
}

@Composable
@Preview(showBackground = true)
private fun GridItemPreview() {
    GridItem()
}

@Composable
@Preview(showBackground = true)
private fun GridItemWithNegativeIndexPreview() {
    GridItemWithNegativeIndex()
}

@Composable
@Preview(showBackground = true)
private fun MixedWithAutoPlacementPreview() {
    MixedWithAutoPlacement()
}

@Composable
@Preview(showBackground = true)
private fun CounterIntuitiveKeyboardTraversalPreview() {
    CounterIntuitiveKeyboardTraversal()
}
