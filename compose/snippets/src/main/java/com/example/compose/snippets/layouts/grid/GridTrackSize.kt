package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridTrackSizeInDp() {
    // [START android_compose_layout_grid_track_size_in_dp]
    Grid(
        config = {
            column(180.dp)
            column(360.dp)

            row(90.dp)
            row(180.dp)
            row(360.dp)
        }
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_track_size_in_dp]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridTrackSizeInPercentage() {
    // [START android_compose_layout_grid_track_size_in_percentage]
    Grid(
        config = {
            column(0.25f)
            column(0.75f)
            row(0.5f)
            row(0.25f)
            row(0.25f)
        },
        modifier = Modifier.size(width = 480.dp, height = 320.dp)
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_track_size_in_percentage]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridTrackSizeInFr() {
    // [START android_compose_layout_grid_track_size_in_fr]
    Grid(
        config = {
            column(120.dp)
            column(1f)
            row(120.dp)
            row(1.fr)
            row(3.fr)
        },
        modifier = Modifier.size(width = 300.dp, height = 480.dp)
    ) {
        Card1()
        Card2()
        Card3()
        Card4()
        Card5()
        Card6()
    }
    // [END android_compose_layout_grid_track_size_in_fr]
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun IntrinsicGridTrackSize() {
    // [START android_compose_layout_grid_intrinsic_grid_track_size]
    Grid(
        config = {
            column(GridTrackSize.MinContent)
            column(GridTrackSize.MaxContent)
            row(1.0f)
        },
        modifier = Modifier.width(480.dp)
    ) {
        Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras imperdiet." )
        Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras imperdiet." )
    }
    // [END android_compose_layout_grid_intrinsic_grid_track_size]
}

@Preview(showBackground = true)
@Composable
private fun GridTrackSizeInDpPreview() {
    GridTrackSizeInDp()
}

@Preview(showBackground = true)
@Composable
private fun GridTrackSizeInPercentagePreview() {
    GridTrackSizeInPercentage()
}

@Preview(showBackground = true)
@Composable
private fun GridTrackSizeInFrPreview() {
    GridTrackSizeInFr()
}

@Preview(showBackground = true)
@Composable
private fun IntrinsicGridTrackSizePreview() {
    IntrinsicGridTrackSize()
}