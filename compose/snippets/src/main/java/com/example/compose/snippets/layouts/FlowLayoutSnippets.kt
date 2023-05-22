/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.layouts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.util.MaterialColors
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
// [START android_compose_flow_row_simple]
@Composable
@Preview
fun FlowRowSimpleUsage() {
    FlowRow {
        Box { }
        Box { }
        Box { }
    }
}
// [END android_compose_flow_row_simple]

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRowSimpleUsageExample() {
    FlowRow(modifier = Modifier.padding(8.dp)) {
        ChipItem("Price: High to Low")
        ChipItem("Avg rating: 4+")
        ChipItem("Free breakfast")
        ChipItem("Free cancellation")
        ChipItem("£50 pn")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_Default_Arrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Start
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_Space_BetweenArrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_Center_Arrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_End_Arrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.End
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_SpaceAround_Arrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_Spacedby_Arrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_VerticalAlignment() {
    FlowRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        FlowItemsDifferentHeights()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MaxItems() {
    FlowRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 3
    ) {
        FlowItems()
    }
}

// Demo items for Flow row / flow column
@Composable
private fun FlowItems() {
    Item(
        modifier = Modifier.width(50.dp),
        color = MaterialColors.Amber300
    )
    Item(
        modifier = Modifier.width(70.dp),
        color = MaterialColors.Blue300
    )
    Item(
        modifier = Modifier.width(96.dp),
        color = MaterialColors.Cyan300
    )
    Item(
        modifier = Modifier.width(40.dp),
        color = MaterialColors.DeepPurple300
    )

    Item(
        modifier = Modifier.width(150.dp),
        color = MaterialColors.Green300
    )
    Item(
        modifier = Modifier.width(60.dp),
        color = MaterialColors.Red300
    )
    Item(
        modifier = Modifier.width(102.dp),
        color = MaterialColors.Purple300
    )
    Item(
        modifier = Modifier.width(42.dp),
        color = MaterialColors.Teal300
    )

    Item(
        modifier = Modifier.width(50.dp),
        color = MaterialColors.Pink300
    )
    Item(
        modifier = Modifier.width(120.dp),
        color = MaterialColors.Lime300
    )
    Item(
        modifier = Modifier.width(110.dp),
        color = MaterialColors.Yellow300
    )
    Item(
        modifier = Modifier.width(90.dp),
        color = MaterialColors.DeepPurple300
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipItem(text: String) {
    Chip(
        modifier = Modifier.padding(end = 4.dp),
        onClick = {},
        leadingIcon = {},
        border = BorderStroke(1.dp, Color(0xFF3B3A3C))
    ) {
        Text(text)
    }
}

@Composable
fun Item(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .height(48.dp)
    )
}

@Composable
private fun FlowItemsDifferentHeights() {
    Item(
        modifier = Modifier
            .width(50.dp)
            .height(48.dp),
        color = MaterialColors.Amber300
    )
    Item(
        modifier = Modifier
            .width(70.dp)
            .height(100.dp),
        color = MaterialColors.Blue300
    )
    Item(
        modifier = Modifier
            .width(96.dp)
            .height(120.dp),
        color = MaterialColors.Cyan300
    )
    Item(
        modifier = Modifier
            .width(40.dp)
            .height(110.dp),
        color = MaterialColors.DeepPurple300
    )

    Item(
        modifier = Modifier
            .width(150.dp)
            .height(90.dp),
        color = MaterialColors.Green300
    )
    Item(
        modifier = Modifier
            .width(60.dp)
            .height(70.dp),
        color = MaterialColors.Red300
    )
    Item(
        modifier = Modifier
            .width(102.dp)
            .height(30.dp),
        color = MaterialColors.Purple300
    )
    Item(
        modifier = Modifier
            .width(42.dp)
            .height(90.dp),
        color = MaterialColors.Teal300
    )

    Item(
        modifier = Modifier
            .width(50.dp)
            .height(40.dp),
        color = MaterialColors.Pink300
    )
    Item(
        modifier = Modifier
            .width(120.dp)
            .height(30.dp),
        color = MaterialColors.Lime300
    )
    Item(
        modifier = Modifier
            .width(110.dp)
            .height(50.dp),
        color = MaterialColors.Yellow300
    )
    Item(
        modifier = Modifier
            .width(90.dp)
            .height(120.dp),
        color = MaterialColors.DeepPurple300
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_StretchAll() {
    // [START android_compose_flow_layout_stretch_all]
    FlowRow(
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val itemModifier = Modifier
            .aspectRatio(1f)
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))

        Spacer(modifier = itemModifier.background(MaterialColors.Green200))
        Spacer(modifier = itemModifier.background(MaterialColors.Blue200))
        Spacer(modifier = itemModifier.background(MaterialColors.Pink200))
        Spacer(modifier = itemModifier.background(MaterialColors.Purple200))
    }
    // [END android_compose_flow_layout_stretch_all]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_StretchMiddleItem() {
    // [START android_compose_flow_layout_stretch_middle]
    FlowRow(
        modifier = Modifier
            .padding(start = 4.dp, top = 16.dp, end = 4.dp)
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val itemModifier = Modifier
            .fillMaxHeight()
            .width(48.dp)
            .clip(RoundedCornerShape(8.dp))

        Spacer(modifier = itemModifier.background(MaterialColors.Green200))
        val middleStretchModifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
        Spacer(modifier = middleStretchModifier.background(MaterialColors.Blue200))
        Spacer(modifier = itemModifier.background(MaterialColors.Pink200))
    }
    // [END android_compose_flow_layout_stretch_all]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_Grid() {
    // [START android_compose_flow_layout_grid]
    val rows = 3
    val columns = 3
    FlowRow(
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = rows
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .height(80.dp)
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialColors.Blue200)
        repeat(rows * columns) {
            Spacer(modifier = itemModifier)
        }
    }
    // [END android_compose_flow_layout_grid]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_AlternatingGrid() {
    // [START android_compose_flow_layout_alternating_grid]
    FlowRow(
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 2
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
        repeat(6) { item ->
            // if the item is the third item, don't use weight modifier, but rather fillMaxWidth
            if ((item + 1) % 3 == 0) {
                Spacer(
                    modifier = itemModifier
                        .fillMaxWidth()
                        .background(MaterialColors.Blue200)
                )
            } else {
                Spacer(
                    modifier = itemModifier
                        .weight(0.5f)
                        .background(MaterialColors.Blue200)
                )
            }
        }
    }
    // [END android_compose_flow_layout_alternating_grid]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_Graph_Horizontal() {
    val paddingModifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp)
    // [START android_compose_flow_horizontal_graph]
    FlowRow(
        modifier = paddingModifier.height(300.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialColors.Blue200)
        repeat(7) { index ->
            val randomPercentage = Random.nextFloat()
            Spacer(modifier = itemModifier.fillMaxHeight(randomPercentage))
        }
    }
    // [END android_compose_flow_horizontal_graph]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_Graph_Vertical() {
    val paddingModifier = Modifier.padding(start = 4.dp, top = 16.dp, end = 4.dp)
    // [START android_compose_flow_vertical_graph]
    FlowColumn(
        modifier = paddingModifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialColors.Blue200)
        repeat(7) {
            val randomPercentage = Random.nextFloat()
            Spacer(modifier = itemModifier.fillMaxWidth(randomPercentage))
        }
    }
    // [END android_compose_flow_vertical_graph]
}
