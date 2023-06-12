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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.util.MaterialColors

@Preview
@OptIn(ExperimentalLayoutApi::class)
// [START android_compose_flow_row_simple]
@Composable
private fun FlowRowSimpleUsageExample() {
    FlowRow(modifier = Modifier.padding(8.dp)) {
        ChipItem("Price: High to Low")
        ChipItem("Avg rating: 4+")
        ChipItem("Free breakfast")
        ChipItem("Free cancellation")
        ChipItem("£50 pn")
    }
}
// [END android_compose_flow_row_simple]

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
private fun FlowRow_MainAxis_VerticalArrangement() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .size(400.dp)
            .border(2.dp, Color.DarkGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        FlowItems()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_VerticalArrangement_Center() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .size(400.dp)
            .border(2.dp, Color.DarkGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        FlowItems()
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
private fun FlowRow_MainAxis_VerticalArrangement_Bottom() {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .size(400.dp)
            .border(2.dp, Color.DarkGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        FlowItems()
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
private fun FlowItemsDifferentHeights(modifier: Modifier = Modifier) {
    Item(
        modifier = modifier
            .width(50.dp)
            .height(48.dp),
        color = MaterialColors.Amber300
    )
    Item(
        modifier = modifier
            .width(70.dp)
            .height(100.dp),
        color = MaterialColors.Blue300
    )
    Item(
        modifier = modifier
            .width(96.dp)
            .height(120.dp),
        color = MaterialColors.Cyan300
    )
    Item(
        modifier = modifier
            .width(40.dp)
            .height(110.dp),
        color = MaterialColors.DeepPurple300
    )

    Item(
        modifier = modifier
            .width(150.dp)
            .height(90.dp),
        color = MaterialColors.Green300
    )
    Item(
        modifier = modifier
            .width(60.dp)
            .height(70.dp),
        color = MaterialColors.Red300
    )
    Item(
        modifier = modifier
            .width(102.dp)
            .height(30.dp),
        color = MaterialColors.Purple300
    )
    Item(
        modifier = modifier
            .width(42.dp)
            .height(90.dp),
        color = MaterialColors.Teal300
    )

    Item(
        modifier = modifier
            .width(50.dp)
            .height(40.dp),
        color = MaterialColors.Pink300
    )
    Item(
        modifier = modifier
            .width(120.dp)
            .height(30.dp),
        color = MaterialColors.Lime300
    )
    Item(
        modifier = modifier
            .width(110.dp)
            .height(50.dp),
        color = MaterialColors.Yellow300
    )
    Item(
        modifier = modifier
            .width(90.dp)
            .height(120.dp),
        color = MaterialColors.DeepPurple300
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun FlowLayout_Grid() {
    // [START android_compose_flow_layout_grid]
    val rows = 3
    val columns = 3
    FlowRow(
        modifier = Modifier.padding(4.dp),
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
        modifier = Modifier.padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 2
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Blue)
        repeat(6) { item ->
            // if the item is the third item, don't use weight modifier, but rather fillMaxWidth
            if ((item + 1) % 3 == 0) {
                Spacer(modifier = itemModifier.fillMaxWidth())
            } else {
                Spacer(modifier = itemModifier.weight(0.5f))
            }
        }
    }
    // [END android_compose_flow_layout_alternating_grid]
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun FlowLayout_FractionalSizing() {
    // [START android_compose_flow_layout_fractional_sizing]
    FlowRow(
        modifier = Modifier.padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 3
    ) {
        val itemModifier = Modifier
            .clip(RoundedCornerShape(8.dp))
        Box(modifier = itemModifier.height(200.dp).width(60.dp).background(Color.Red))
        Box(modifier = itemModifier.height(200.dp).fillMaxWidth(0.7f).background(Color.Blue))
        Box(modifier = itemModifier.height(200.dp).weight(1f).background(Color.Magenta))
    }
    // [END android_compose_flow_layout_fractional_sizing]
}
