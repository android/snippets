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