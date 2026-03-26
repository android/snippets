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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.ui.theme.PastelBlue
import com.example.compose.snippets.ui.theme.PastelGreen
import com.example.compose.snippets.ui.theme.PastelOrange
import com.example.compose.snippets.ui.theme.PastelPink
import com.example.compose.snippets.ui.theme.PastelRed
import com.example.compose.snippets.ui.theme.PastelYellow

@Preview(showBackground = true, heightDp = 480)
@Composable
fun GridExample() {
    Grid(
        config = {
            repeat(4) {
                column(0.25f)
            }
            row(0.33f)
            row(0.33f)
            row(0.34f)
            gap(8.dp)
        }
    ) {
        Card1(modifier = Modifier.gridItem(columnSpan = 3, rowSpan = 2))
        Card2()
        Card3()
        Card4()
        Card5(modifier = Modifier.gridItem(columnSpan = 3))
    }
}

@Composable
@Preview(showBackground = true)
fun SixCards() {
    // [START android_compose_layout_grid_six_cards]
    Grid(
        config = {
            repeat(2) {
                column(100.dp)
            }
            repeat(3) {
                row(100.dp)
            }
        }
    ) {
        Card1(containerColor = PastelRed)
        // [START_EXCLUDE]
        EnableAlternativePattern {
            // [END_EXCLUDE]
            Card2(containerColor = PastelGreen)
            Card3(containerColor = PastelBlue)
            // [START_EXCLUDE]
        }
        // [END_EXCLUDE]
        Card4(containerColor = PastelPink)
        Card5(containerColor = PastelOrange)
        // [START_EXCLUDE]
        EnableAlternativePattern {
            // [END_EXCLUDE]
            Card6(containerColor = PastelYellow)
            // [START_EXCLUDE]
        }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_grid_six_cards]
}

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


@Composable
@Preview(showBackground = true)
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


@Preview(showBackground = true)
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
