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
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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