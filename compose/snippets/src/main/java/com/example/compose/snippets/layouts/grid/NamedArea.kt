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

@file:OptIn(
    ExperimentalGridApi::class, ExperimentalMediaQueryApi::class,
    ExperimentalComposeUiApi::class
)

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.derivedMediaQuery
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


// [START android_compose_layout_grid_area_ids]
/**
 * An enum representing the IDs for named areas within the grid.
 */
enum class GridAreaNames {
    Area1,
    Area2,
    Area3,
    Area4
}
// [END android_compose_layout_grid_area_ids]

/**
 * A sample demonstrating the use of named areas in a [Grid] layout.
 * This approach allows you to define the layout structure separately from the
 * content, making it easier to manage and update.
 */
@Preview(showBackground = true)
@Composable
fun NamedArea() {
    // [START android_compose_layout_grid_named_area]
    Grid(
        config = {
            // Define a single column that takes all available width.
            repeat(2) { column(0.5f) }

            // Define four rows, each taking 25% of the total height.
            repeat(4) { row(0.25f) }

            // Define named grid areas by associating an areaId with specific row and column indices.
            // Row and column indices are 1-based.
            area(areaId = GridAreaNames.Area1, row = 1, column = 1, columnSpan = 2)
            area(areaId = GridAreaNames.Area2, row = 2, column = 1, rowSpan = 3)
            area(areaId = GridAreaNames.Area3, rows = 2..3, columns = 2..2)
            area(areaId = GridAreaNames.Area4, row = 4, column = 2)

            gap(4.dp)
        },
        modifier = Modifier.size(360.dp)
    ) {
        PastelRedCard(
            "Area 1",
            // Use Modifier.gridItem(areaId) to place this composable into the
            // grid area defined with the matching ID in the config block.
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area1)
        )
        PastelGreenCard(
            "Area 2",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area2)
        )
        PastelBlueCard(
            "Area 3",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area3)
        )
        PastelYellowCard(
            "Area 4",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area4)
        )
    }
    // [END android_compose_layout_grid_named_area]
}

/**
 * Demonstrates a responsive grid where the configuration is hoisted and
 * selected based on the window size using media queries.
 */
@Preview(showBackground = true, widthDp = 120, heightDp = 240, name = "120.dp")
@Preview(showBackground = true, widthDp = 240, heightDp = 240, name = "240.dp")
// [START android_compose_layout_grid_hoisted_grid_configuration]
@Composable
fun HoistedGridConfiguration() {
    // [START_EXCLUDE silent]
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    PreviewMediaQuery {
        // [END_EXCLUDE]
        // Select the appropriate grid configuration based on the window width.
        val gridConfiguration by derivedMediaQuery {
            when {
                windowWidth < 240.dp -> FourCardsLayout.SingleColumn
                else -> FourCardsLayout.TwoColumns
            }
        }

        FourCardsInGrid(
            gridConfiguration = gridConfiguration,
            modifier = Modifier.fillMaxSize()
        )
        // [START_EXCLUDE silent]
    }
    // [END_EXCLUDE]
}
// [END android_compose_layout_grid_hoisted_grid_configuration]


// [START android_compose_layout_grid_grid_configuration_scope]
/**
 * A reusable composable that displays four colored cards in a grid.
 * The layout of the cards is determined by the provided [gridConfiguration].
 */
@Composable
fun FourCardsInGrid(
    modifier: Modifier = Modifier,
    gridConfiguration: GridConfigurationScope.() -> Unit = FourCardsLayout.SingleColumn,
) {
    Grid(
        config = gridConfiguration,
        modifier = modifier
    ) {
        PastelRedCard(
            "Area 1",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area1)
        )
        PastelGreenCard(
            "Area 2",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area2)
        )
        PastelBlueCard(
            "Area 3",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area3)
        )
        PastelYellowCard(
            "Area 4",
            modifier = Modifier.gridItem(areaId = GridAreaNames.Area4)
        )
    }
}
// [END android_compose_layout_grid_grid_configuration_scope]

// [START android_compose_layout_grid_two_grid_configurations]
/**
 * Contains different grid configurations for the [FourCardsInGrid] layout.
 */
object FourCardsLayout {
    /**
     * A single-column layout where each card takes the full width and 25% height.
     */
    val SingleColumn: GridConfigurationScope.() -> Unit = {
        column(1f)
        repeat(4) { row(0.25f) }

        area(areaId = GridAreaNames.Area1, row = 1, column = 1)
        area(areaId = GridAreaNames.Area2, row = 2, column = 1)
        area(areaId = GridAreaNames.Area3, row = 3, column = 1)
        area(areaId = GridAreaNames.Area4, row = 4, column = 1)

        gap(4.dp)
    }

    /**
     * A two-column layout with varying spans for different cards.
     */
    val TwoColumns: GridConfigurationScope.() -> Unit = {
        repeat(2) { column(0.5f) }
        repeat(4) { row(0.25f) }

        area(areaId = GridAreaNames.Area1, row = 1, column = 1, rowSpan = 2, columnSpan = 2)
        area(areaId = GridAreaNames.Area2, row = 3, column = 1)
        area(areaId = GridAreaNames.Area3, row = 3, column = 2)
        area(areaId = GridAreaNames.Area4, row = 4, column = 1, columnSpan = 2)

        gap(4.dp)
    }
}
// [END android_compose_layout_grid_two_grid_configurations]

// [START android_compose_layout_grid_area_ids]
enum class CardArea {
    Image,
    Title,
    Subtitle,
    Description,
    ExtraText,
}
// [END android_compose_layout_grid_area_ids]

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

/**
 * A helper composable to provide a default [UiMediaScope] for previews.
 */
@Composable
private fun PreviewMediaQuery(content: @Composable () -> Unit) {
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    val windowInfo = LocalWindowInfo.current
    val uiMediaScope = remember(windowInfo) {
        object : UiMediaScope {
            override val windowWidth: Dp = windowInfo.containerDpSize.width
            override val windowHeight: Dp = windowInfo.containerDpSize.height
            override val windowPosture: UiMediaScope.Posture = UiMediaScope.Posture.Flat
            override val viewingDistance: UiMediaScope.ViewingDistance =
                UiMediaScope.ViewingDistance.Medium
            override val hasMicrophone: Boolean = true
            override val hasCamera: Boolean = true
            override val pointerPrecision: UiMediaScope.PointerPrecision =
                UiMediaScope.PointerPrecision.Coarse
            override val keyboardKind: UiMediaScope.KeyboardKind = UiMediaScope.KeyboardKind.None
        }
    }
    CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope, content = content)
}

