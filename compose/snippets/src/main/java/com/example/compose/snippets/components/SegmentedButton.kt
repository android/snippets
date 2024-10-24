/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedButtonExamples() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        SingleChoiceSegmentedButton()
        Spacer(modifier = Modifier.height(16.dp))
        MultiChoiceSegmentedButton()
    }
}

// [START android_compose_components_singlechoicesegmentedbutton]
@Composable
fun SingleChoiceSegmentedButton(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Day", "Month", "Week")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}
// [END android_compose_components_singlechoicesegmentedbutton]

@Preview
@Composable
private fun SingleChoiceSegmentedButtonPreview() {
    SingleChoiceSegmentedButton()
}

// [START android_compose_components_multichoicesegmentedbutton]
@Composable
fun MultiChoiceSegmentedButton(modifier: Modifier = Modifier) {
    val selectedOptions = remember {
        mutableStateListOf(false, false, false)
    }
    val options = listOf("Walk", "Ride", "Drive")

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                checked = selectedOptions[index],
                onCheckedChange = {
                    selectedOptions[index] = !selectedOptions[index]
                },
                icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) },
                label = {
                    when (label) {
                        "Walk" -> Icon(
                            imageVector =
                            Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = "Directions Walk"
                        )
                        "Ride" -> Icon(
                            imageVector =
                            Icons.Default.DirectionsBus,
                            contentDescription = "Directions Bus"
                        )
                        "Drive" -> Icon(
                            imageVector =
                            Icons.Default.DirectionsCar,
                            contentDescription = "Directions Car"
                        )
                    }
                }
            )
        }
    }
}
// [END android_compose_components_multichoicesegmentedbutton]

@Preview
@Composable
private fun MultiChoiceSegmentedButtonPreview() {
    MultiChoiceSegmentedButton()
}
