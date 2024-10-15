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

package com.example.compose.snippets.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// [START android_compose_layouts_list_listanimateditems]
@Composable
fun ListAnimatedItems(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(items, key = { it }) {
            ListItem(
                headlineContent = { Text(it) },
                modifier = Modifier
                    .animateItem()
                    .fillParentMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
            )
        }
    }
}
// [END android_compose_layouts_list_listanimateditems]

// [START android_compose_layouts_list_listanimateditemsexample]
@Composable
private fun ListAnimatedItemsExample(data: List<String>) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val displayedItems = remember { mutableStateOf(data) }

            // Buttons that change the value of displayedItems.
            AddRemoveButtons(data, displayedItems)
            OrderButtons(data, displayedItems)

            // List that displays the values of displayedItems.
            ListAnimatedItems(displayedItems.value)
        }
    }
}
// [END android_compose_layouts_list_listanimateditemsexample]

// [START android_compose_layouts_list_addremovebuttons]
@Composable
private fun AddRemoveButtons(
    data: List<String>,
    displayedItems: MutableState<List<String>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            enabled = displayedItems.value.size < data.size,
            onClick = {
                // Avoid duplicate items
                val remainingItems = data.filter { it !in displayedItems.value }
                if (remainingItems.isNotEmpty()) displayedItems.value += remainingItems.first()
            },
        ) {
            Text("Add Item")
        }
        Spacer(modifier = Modifier.padding(25.dp))
        Button(
            enabled = displayedItems.value.isNotEmpty(),
            onClick = {
                displayedItems.value = displayedItems.value.dropLast(1)
            },
        ) {
            Text("Delete Item")
        }
    }
}
// [END android_compose_layouts_list_addremovebuttons]

// [START android_compose_layouts_list_orderbuttons]
@Composable
private fun OrderButtons(
    data: List<String>,
    displayedItems: MutableState<List<String>>
) {
    val sortAlpha = Comparator { str: String, str2: String -> str.compareTo(str2) }
    val sortLength = Comparator { str1: String, str2: String -> str1.length - str2.length }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Reset", "Alphabetical", "Length")

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = {
                        selectedIndex = index
                        when (options[selectedIndex]) {
                            "Reset" -> displayedItems.value = data
                            "Alphabetical" ->
                                displayedItems.value =
                                    displayedItems.value.sortedWith(sortAlpha)
                            "Length" ->
                                displayedItems.value =
                                    displayedItems.value.sortedWith(sortLength)
                        }
                    },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }
    }
}
// [END android_compose_layouts_list_orderbuttons]

@Preview
@Composable
fun ListAnimatingItemsExamplePreview() {
    val list = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten")
    ListAnimatedItemsExample(list)
}
