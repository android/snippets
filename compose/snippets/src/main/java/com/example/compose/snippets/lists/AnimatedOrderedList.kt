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

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnimatedOrderedListViewModel : ViewModel() {
    private val _data = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten")
    private val _displayedItems: MutableStateFlow<List<String>> = MutableStateFlow(_data)
    val displayedItems: StateFlow<List<String>> = _displayedItems

    fun resetOrder() {
        _displayedItems.value = _data.filter { it in _displayedItems.value }
    }

    fun sortAlphabetically() {
        _displayedItems.value = _displayedItems.value.sortedBy { it }
    }

    fun sortByLength() {
        _displayedItems.value = _displayedItems.value.sortedBy { it.length }
    }

    fun addItem() {
        // Avoid duplicate items
        val remainingItems = _data.filter { it !in _displayedItems.value }
        if (remainingItems.isNotEmpty()) _displayedItems.value += remainingItems.first()
    }

    fun removeItem() {
        _displayedItems.value = _displayedItems.value.dropLast(1)
    }
}

@Composable
fun AnimatedOrderedListScreen(
    viewModel: AnimatedOrderedListViewModel,
    modifier: Modifier = Modifier,
) {
    val displayedItems by viewModel.displayedItems.collectAsStateWithLifecycle()

    ListAnimatedItemsExample(
        displayedItems,
        onAddItem = viewModel::addItem,
        onRemoveItem = viewModel::removeItem,
        resetOrder = viewModel::resetOrder,
        onSortAlphabetically = viewModel::sortAlphabetically,
        onSortByLength = viewModel::sortByLength,
        modifier = modifier
    )
}

// [START android_compose_layouts_list_listanimateditems]
@Composable
fun ListAnimatedItems(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        // Use a unique key per item, so that animations work as expected.
        items(items, key = { it }) {
            ListItem(
                headlineContent = { Text(it) },
                modifier = Modifier
                    .animateItem(
                        // Optionally add custom animation specs
                    )
                    .fillParentMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
            )
        }
    }
}
// [END android_compose_layouts_list_listanimateditems]

// [START android_compose_layouts_list_listanimateditemsexample]
@Composable
private fun ListAnimatedItemsExample(
    data: List<String>,
    modifier: Modifier = Modifier,
    onAddItem: () -> Unit = {},
    onRemoveItem: () -> Unit = {},
    resetOrder: () -> Unit = {},
    onSortAlphabetically: () -> Unit = {},
    onSortByLength: () -> Unit = {},
) {
    val canAddItem = data.size < 10
    val canRemoveItem = data.isNotEmpty()

    Scaffold(modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Buttons that change the value of displayedItems.
            AddRemoveButtons(canAddItem, canRemoveItem, onAddItem, onRemoveItem)
            OrderButtons(resetOrder, onSortAlphabetically, onSortByLength)

            // List that displays the values of displayedItems.
            ListAnimatedItems(data)
        }
    }
}
// [END android_compose_layouts_list_listanimateditemsexample]

// [START android_compose_layouts_list_addremovebuttons]
@Composable
private fun AddRemoveButtons(
    canAddItem: Boolean,
    canRemoveItem: Boolean,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(enabled = canAddItem, onClick = onAddItem) {
            Text("Add Item")
        }
        Spacer(modifier = Modifier.padding(25.dp))
        Button(enabled = canRemoveItem, onClick = onRemoveItem) {
            Text("Delete Item")
        }
    }
}
// [END android_compose_layouts_list_addremovebuttons]

// [START android_compose_layouts_list_orderbuttons]
@Composable
private fun OrderButtons(
    resetOrder: () -> Unit,
    orderAlphabetically: () -> Unit,
    orderByLength: () -> Unit
) {
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
                        Log.d("AnimatedOrderedList", "selectedIndex: $selectedIndex")
                        selectedIndex = index
                        when (options[selectedIndex]) {
                            "Reset" -> resetOrder()
                            "Alphabetical" -> orderAlphabetically()
                            "Length" -> orderByLength()
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
fun AnimatedOrderedListScreenPreview() {
    val viewModel = remember { AnimatedOrderedListViewModel() }
    AnimatedOrderedListScreen(viewModel)
}
