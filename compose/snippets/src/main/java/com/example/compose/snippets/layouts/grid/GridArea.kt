package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridArea() {
    // [START android_compose_layout_grid_area]
    Grid(
        config = {
            repeat(3) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            rowGap(8.dp)
            columnGap(8.dp)
        }
    ) {
        Card1(modifier = Modifier.gridItem(rowSpan = 2, columnSpan = 2))
        Card2()
        Card3()
        Card4(modifier = Modifier.gridItem(columnSpan = 3))
    }
    // [END android_compose_layout_grid_area]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun Alignment() {
    // [START android_compose_layout_grid_alignment]
    Grid(
        config = {
            repeat(3) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
            rowGap(8.dp)
            columnGap(8.dp)
        },
    ) {
        Text(
            text = "#1",
            modifier = Modifier.gridItem(
                rowSpan = 2,
                columnSpan = 2,
                alignment = Alignment.Center
            ),
        )
        Card2()
        Card3()
        Card4(modifier = Modifier.gridItem(columnSpan = 3))
    }
    // [END android_compose_layout_grid_alignment]
}

@Preview(showBackground = true)
@Composable
private fun GridAreaPreview() {
    GridArea()
}

@Preview(showBackground = true)
@Composable
private fun AlignmentPreview() {
    Alignment()
}
