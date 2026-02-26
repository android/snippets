package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridColumnGapAndRowGap() {
    // [START android_compose_layout_grid_column_gap_and_row_gap]
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
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_column_gap_and_row_gap]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridGap() {
    // [START android_compose_layout_grid_gap]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            gap(8.dp) // Equivalent to columnGap(8.dp) and rowGap(8.dp)
        }
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_gap]
}

@Preview(showBackground = true)
@Composable
private fun GridColumnGapAndRowGapPreview() {
    GridColumnGapAndRowGap()
}

@Preview(showBackground = true)
@Composable
private fun GridGapPreview() {
    GridGap()
}