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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridConfigurationScope
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope.Posture
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
// [START android_compose_grid_layout_table_in_grid]
@Composable
fun TableInGrid(
    modifier: Modifier = Modifier,
) {
    Grid(
        config = {
            column(GridTrackSize.Auto)
            column(GridTrackSize.Auto)
            column(GridTrackSize.Auto)
            column(GridTrackSize.Auto)
            column(GridTrackSize.Auto)
            row(GridTrackSize.Auto)
            row(GridTrackSize.Auto)
            row(GridTrackSize.Auto)
            gap(row = 8.dp, column = 16.dp)
        },
        modifier = modifier,
    ) {
        // Table Header
        Text(text = "", fontWeight = FontWeight.Bold)
        Text(text = "Layout", fontWeight = FontWeight.Bold)
        Text(text = "Lazy loading", fontWeight = FontWeight.Bold)
        Text(text = "Soft wrap", fontWeight = FontWeight.Bold)
        Text(text = "Dynamic layout update", fontWeight = FontWeight.Bold)

        // FlexBox Row
        Text(text = "FlexBox", fontWeight = FontWeight.Bold)
        Text(text = "1D")
        Text(text = "No")
        Text(text = "Yes")
        Text(text = "Yes")

        // Grid Row
        Text(text = "Grid", fontWeight = FontWeight.Bold)
        Text(text = "2D")
        Text(text = "No")
        Text(text = "No")
        Text(text = "Yes")
    }
}
// [END android_compose_grid_layout_table_in_grid]

// [START android_compose_layout_card_area_ids]
enum class CardArea {
    Image,
    Title,
    Subtitle,
    Description,
    ExtraText,
}
// [END android_compose_layout_card_area_ids]

@Composable
private fun WideClassicCardGridConfig() {
    // [START android_compose_layout_grid_wide_classic_card_grid_config]
    Grid(
        config = {
            // Define columns: left column for image, right for content
            column(GridTrackSize.Auto)
            column(minmax(0.dp, 1.fr))

            // Define row tracks for the vertical content stack
            row(GridTrackSize.Auto)
            row(GridTrackSize.Auto)
            row(GridTrackSize.Auto)
            row(GridTrackSize.Auto)

            // Map semantic identifiers to grid coordinates and spans
            area(CardArea.Image, row = 1, column = 1, rowSpan = 4)
            area(CardArea.Title, row = 1, column = 2)
            area(CardArea.Subtitle, row = 2, column = 2)
            area(CardArea.Description, row = 3, column = 2)
            area(CardArea.ExtraText, row = 4, column = 2)

            gap(row = 8.dp, column = 16.dp)
        }
    ) {
        // Child elements placed in Step 3
    }
    // [END android_compose_layout_grid_wide_classic_card_grid_config]
}

// [START android_compose_layout_grid_wide_classic_card]
@Composable
fun WideClassicCard(
    imageContent: @Composable () -> Unit,
    title: String,
    subtitle: String,
    description: String,
    extraText: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Grid(
            config = {
                // Define columns: left column for image, right for content
                column(GridTrackSize.Auto)
                column(minmax(0.dp, 1.fr))

                // Define row tracks for the vertical content stack
                row(GridTrackSize.Auto)
                row(GridTrackSize.Auto)
                row(GridTrackSize.Auto)
                row(GridTrackSize.Auto)

                // Map semantic identifiers to grid coordinates and spans
                area(CardArea.Image, row = 1, column = 1, rowSpan = 4)
                area(CardArea.Title, row = 1, column = 2)
                area(CardArea.Subtitle, row = 2, column = 2)
                area(CardArea.Description, row = 3, column = 2)
                area(CardArea.ExtraText, row = 4, column = 2)

                gap(row = 8.dp, column = 16.dp)
            },
        ) {
            Box(modifier = Modifier.gridItem(CardArea.Image)) {
                imageContent()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .gridItem(CardArea.Title)
                    .padding(top = 16.dp, end = 16.dp),
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .gridItem(CardArea.Subtitle)
                    .padding(end = 16.dp),
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .gridItem(CardArea.Description)
                    .padding(end = 16.dp),
            )

            Text(
                text = extraText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .gridItem(CardArea.ExtraText)
                    .padding(bottom = 16.dp, end = 16.dp),
            )
        }
    }
}
// [END android_compose_layout_grid_wide_classic_card]

@Preview(showBackground = true)
@Composable
private fun WideClassicCardPreview() {
    WideClassicCard(
        imageContent = {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
        },
        title = "Title",
        subtitle = "Subtitle",
        description = "This is a description of the card. It explains what the card is about in more detail.",
        extraText = "Extra text",
        modifier = Modifier.padding(16.dp)
    )
}

// [START android_compose_grid_overlay_config_setup]
enum class Areas { A, B, C, OVERLAY }

val gridConfig: GridConfigurationScope.() -> Unit = {
    repeat(4) {
        column(GridTrackSize.Fixed(100.dp))
    }
    repeat(2) {
        row(GridTrackSize.Fixed(100.dp))
    }
    area(areaId = Areas.A, row = 1, column = 1, columnSpan = 2)
    area(areaId = Areas.B, row = 1, column = 3, columnSpan = 2)
    area(areaId = Areas.C, row = 2, column = 1, columnSpan = 2)
    // Define a named area spanning multiple columns that overlaps other cells
    area(areaId = Areas.OVERLAY, row = 2, column = 2, columnSpan = 3)
}

// [END android_compose_grid_overlay_config_setup]


@Composable
@Preview
private fun GridOverlayCard() {
// [START android_compose_grid_overlay]
    Grid(
        config = gridConfig,
    ) {
        TextCard("A", Modifier.gridItem(areaId = Areas.A))
        TextCard("B", Modifier.gridItem(areaId = Areas.B))

        // Base Layer C
        TextCard("C", Modifier.gridItem(areaId = Areas.C))

        // Overlay Layer (placed in the named area, overlapping C by sharing column 2)
        TextCard(
            "OVERLAY",
            Modifier.gridItem(areaId = Areas.OVERLAY),
            color = Color(0xDD4B608D)
        )
    }
// [END android_compose_grid_overlay]
}

// [START android_compose_grid_overlay_helper_text_card]
// Helper Component to render Grid content
@Composable
private fun TextCard(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF424242)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 24.sp)
    }
}
// [END android_compose_grid_overlay_helper_text_card]

// [START android_compose_grid_tabletop_config_setup]
enum class VideoPlayerArea { Player, Controller }

// Default: Single area layout with overlay (controls on top of player)
val defaultConfig: GridConfigurationScope.() -> Unit = {
    row(1f)
    column(1f)
    area(areaId = VideoPlayerArea.Player, row = 1, column = 1)
    area(areaId = VideoPlayerArea.Controller, row = 1, column = 1)
}

// Tabletop: Two rows splitting content across the fold
val tabletopConfig: GridConfigurationScope.() -> Unit = {
    row(0.5f)
    row(0.5f)
    column(1f)
    area(areaId = VideoPlayerArea.Player, row = 1, column = 1) // Top half above fold
    area(areaId = VideoPlayerArea.Controller, row = 2, column = 1) // Bottom half below fold
}
// [END android_compose_grid_tabletop_config_setup]

@OptIn(ExperimentalMediaQueryApi::class)
@Composable
@Preview
private fun GridTabletop() {
    // [START android_compose_grid_tabletop]
    val config = mediaQuery {
        when (windowPosture) {
            Posture.Tabletop -> tabletopConfig
            else -> defaultConfig
        }
    }

    Grid(config = config) {
        Box(
            modifier = Modifier
                .gridItem(areaId = VideoPlayerArea.Player)
        ) {
            // VideoPlayerContent
        }

        Box(
            modifier = Modifier
                .gridItem(areaId = VideoPlayerArea.Controller)
        ) {
            // PlaybackControlsContent
        }
    }
    // [END android_compose_grid_tabletop]
}
