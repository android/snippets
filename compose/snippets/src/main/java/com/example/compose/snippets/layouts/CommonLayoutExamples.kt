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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.util.MaterialColors
import kotlin.random.Random

/*
* Copyright 2022 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun Layout_Graph_Vertical() {
    val paddingModifier = Modifier.padding(4.dp)
    // [START android_compose_layout_vertical_graph]
    Column(
        modifier = paddingModifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialColors.Blue200)
        repeat(7) {
            val randomPercentage = Random.nextFloat()
            Spacer(
                modifier = itemModifier
                    .fillMaxWidth(randomPercentage)
                    .align(Alignment.End)
            )
        }
    }
    // [END android_compose_layout_vertical_graph]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun Layout_Graph_Horizontal() {
    val paddingModifier = Modifier.padding(4.dp)
    // [START android_compose_layout_horizontal_graph]
    Row(
        modifier = paddingModifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val itemModifier = Modifier
            .padding(4.dp)
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialColors.Blue200)
        repeat(7) { index ->
            val randomPercentage = Random.nextFloat()
            Spacer(
                modifier = itemModifier
                    .align(Alignment.Bottom)
                    .fillMaxHeight(randomPercentage)
            )
        }
    }
    // [END android_compose_layout_horizontal_graph]
}

@Composable
@Preview
fun Layout_StretchAll() {
    // [START android_compose_layout_stretch_all]
    Row(
        modifier = Modifier.padding(4.dp),
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
    // [END android_compose_layout_stretch_all]
}

@Composable
@Preview
fun Layout_StretchMiddleItem() {
    // [START android_compose_layout_stretch_middle]
    Row(
        modifier = Modifier
            .padding(4.dp)
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
    // [END android_compose_layout_stretch_middle]
}
