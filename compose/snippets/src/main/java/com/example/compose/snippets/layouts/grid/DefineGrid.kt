package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun DefineGrid() {
    // [START android_compose_layout_grid_define_grid]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
        }
    ) {
    }
    // [END android_compose_layout_grid_define_grid]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun DefineGridAndPlaceItems() {
    // [START android_compose_layout_grid_define_grid_and_place_items]
    Grid(
        config = {
            repeat(2) {
                column(160.dp)
            }
            repeat(3) {
                row(90.dp)
            }
        }
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_define_grid_and_place_items]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun DefineExplicitAndImplicitGridTracks() {
    // [START android_compose_layout_grid_define_explicit_and_implicit_grid_tracks]
    Grid(
        config = {
            column(160.dp)
            column(160.dp)

            row(90.dp)
            row(90.dp)
        }
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_define_explicit_and_implicit_grid_tracks]
}

@Preview(showBackground = true)
@Composable
private fun DefineGridAndPlaceItemsPreview() {
    DefineGridAndPlaceItems()
}

@Preview(showBackground = true)
@Composable
private fun DefineExplicitAndImplicitGridTracksPreview() {
    DefineExplicitAndImplicitGridTracks()
}