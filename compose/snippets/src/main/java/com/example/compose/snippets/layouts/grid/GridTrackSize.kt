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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun GridTrackSize() {
    // [START android_compose_layout_grid_track_size]
    Grid(
        config = {
            column(1f)

            row(100.dp)
            row(0.2f)
            row(1.fr)
            row(GridTrackSize.Auto)
        },
        modifier = Modifier.height(480.dp)
    ) {
        PastelRedCard("Fixed(100.dp)")
        // [START_EXCLUDE silent]
        EnableAlternativePattern{
            // [END_EXCLUDE]
            PastelGreenCard("Percentage(0.2f)")
            // [START_EXCLUDE silent]
        }
        // [END_EXCLUDE]
        PastelBlueCard("Flex(1.fr)")
        // [START_EXCLUDE silent]
        EnableAlternativePattern {
        // [END_EXCLUDE]
        PastelYellowCard("Auto")
        // [START_EXCLUDE silent]
        }
        // [END_EXCLUDE]

    }
    // [END android_compose_layout_grid_track_size]
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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
fun GridTrackSizeMinMax() {
    // [START android_compose_layout_grid_track_size_min_max]
    Grid(
        config = {
            column(1f)
            // The first row has a minimum height of 100.dp and can expand to 
            // the half of the remaining space.
            row(GridTrackSize.MinMax(100.dp, 1.fr))
            // The second row takes the half of the remaining space.
            row(1.fr)
            // The third row has a fixed height of 200.dp.
            row(200.dp)
        },
        modifier = Modifier.size(360.dp) // Total grid height is 360.dp
    ) {
        PastelRedCard("MinMax(100.dp, 1.fr)")
        // [START_EXCLUDE silent]
        EnableAlternativePattern{
            // [END_EXCLUDE]
            PastelGreenCard("Flex(1.fr)")
            // [START_EXCLUDE silent]
        }
        // [END_EXCLUDE]
        PastelBlueCard("Fixed(200.dp)")
    }
    // [END android_compose_layout_grid_track_size_min_max]
}

@Preview(showBackground = true)
@Composable
fun LazyColumnInGrid() {
    // [START android_compose_layout_grid_lazy_column_in_grid]
    val numberList = remember{ (0..100).toList() }
    Grid(
        config = {
            column(1f)
            // The first row's height is determined by the height of the Text composable.
            row(GridTrackSize.Auto)
            // The second row occupies the remaining space, allowing the LazyColumn to scroll.
            row(GridTrackSize.MinMax(0.dp, 1.fr))

            gap(8.dp)
        },
        modifier = Modifier.size(width = 170.dp, height = 240.dp)
    ) {
        Text("Lazy column in a Grid")
        // The LazyColumn is placed in the second row, filling the remaining space.
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(numberList) { number ->
                PastelGreenCard("Card $number")
            }
        }
    }
    // [END android_compose_layout_grid_lazy_column_in_grid]
}