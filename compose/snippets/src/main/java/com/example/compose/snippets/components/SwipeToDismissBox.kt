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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SwipeToDismissBoxExamples() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Swipe to dismiss with change of background", fontWeight = FontWeight.Bold)
        SwipeItemExample()
        Text("Swipe to dismiss with a cross-fade animation", fontWeight = FontWeight.Bold)
        SwipeCardItemExample()
    }
}

// [START android_compose_components_todoitem]
data class TodoItem(
    var isItemDone: Boolean,
    var itemDescription: String
)
// [END android_compose_components_todoitem]

// [START android_compose_components_swipeitem]
@Composable
fun SwipeItem(
    todoItem: TodoItem,
    startToEndAction: (TodoItem) -> Unit,
    endToStartAction: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (TodoItem) -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    startToEndAction(todoItem)
                    // Do not dismiss this item.
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    endToStartAction(todoItem)
                    true
                }
                SwipeToDismissBoxValue.Settled -> {
                    false
                }
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        backgroundContent = {
            Row(
                modifier = Modifier
                    .background(
                        when (swipeToDismissBoxState.dismissDirection) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                Color.Blue
                            }
                            SwipeToDismissBoxValue.EndToStart -> {
                                Color.Red
                            }
                            SwipeToDismissBoxValue.Settled -> {
                                Color.LightGray
                            }
                        }
                    )
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (swipeToDismissBoxState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        val icon = if (todoItem.isItemDone) {
                            Icons.Default.CheckBox
                        } else {
                            Icons.Default.CheckBoxOutlineBlank
                        }

                        val contentDescription = if (todoItem.isItemDone) "Done" else "Not done"

                        Icon(
                            icon,
                            contentDescription,
                            Modifier.padding(12.dp),
                            tint = Color.White
                        )
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        Spacer(modifier = Modifier)
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove item",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        }
    ) {
        content(todoItem)
    }
}
// [END android_compose_components_swipeitem]

@Preview(showBackground = true)
// [START android_compose_components_swipeitemexample]
@Composable
private fun SwipeItemExample() {
    val todoItems = remember {
        mutableStateListOf(
            TodoItem(isItemDone = false, itemDescription = "Pay bills"),
            TodoItem(isItemDone = false, itemDescription = "Buy groceries"),
            TodoItem(isItemDone = false, itemDescription = "Go to gym"),
            TodoItem(isItemDone = false, itemDescription = "Get dinner")
        )
    }

    LazyColumn {
        items(
            items = todoItems,
            key = { it.itemDescription }
        ) { todoItem ->
            SwipeItem(
                todoItem = todoItem,
                startToEndAction = {
                    todoItem.isItemDone = !todoItem.isItemDone
                },
                endToStartAction = {
                    todoItems -= todoItem
                }
            ) {
                ListItem(
                    headlineContent = { Text(text = todoItem.itemDescription) },
                    supportingContent = { Text(text = "swipe me to update or remove.") }
                )
            }
        }
    }
}
// [END android_compose_components_swipeitemexample]

// [START android_compose_components_swipecarditem]
@Composable
fun SwipeCardItem(
    todoItem: TodoItem,
    startToEndAction: (TodoItem) -> Unit,
    endToStartAction: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (TodoItem) -> Unit
) {
    // [START_EXCLUDE]
    val swipeToDismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.25f },
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    startToEndAction(todoItem)
                    // Do not dismiss this item.
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    endToStartAction(todoItem)
                    true
                }
                SwipeToDismissBoxValue.Settled -> {
                    false
                }
            }
        }
    )

    // [END_EXCLUDE]
    SwipeToDismissBox(
        modifier = Modifier,
        state = swipeToDismissState,
        backgroundContent = {
            // Cross-fade the background color as the drag gesture progresses.
            val color by animateColorAsState(
                when (swipeToDismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.StartToEnd ->
                        lerp(Color.LightGray, Color.Blue, swipeToDismissState.progress)

                    SwipeToDismissBoxValue.EndToStart ->
                        lerp(Color.LightGray, Color.Red, swipeToDismissState.progress)
                },
                label = "swipeable card item background color"
            )
            // [START_EXCLUDE]
            Row(
                modifier = Modifier
                    .background(color)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (swipeToDismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        val icon = if (todoItem.isItemDone) {
                            Icons.Default.CheckBox
                        } else {
                            Icons.Default.CheckBoxOutlineBlank
                        }

                        val contentDescription = if (todoItem.isItemDone) "Done" else "Not done"

                        Icon(icon, contentDescription, Modifier.padding(12.dp), tint = Color.White)
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        Spacer(modifier = Modifier)
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove item",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        }
    ) {
        content(todoItem)
    }
    // [END_EXCLUDE]
}
// [END android_compose_components_swipecarditem]

// [START android_compose_components_swipecarditemexample]
@Preview
@Composable
private fun SwipeCardItemExample() {
    val todoItems = remember {
        mutableStateListOf(
            TodoItem(isItemDone = false, itemDescription = "Pay bills"),
            TodoItem(isItemDone = false, itemDescription = "Buy groceries"),
            TodoItem(isItemDone = false, itemDescription = "Go to gym"),
            TodoItem(isItemDone = false, itemDescription = "Get dinner")
        )
    }

    LazyColumn {
        items(
            items = todoItems,
            key = { it.itemDescription }
        ) { todoItem ->
            SwipeCardItem(
                todoItem = todoItem,
                startToEndAction = {
                    todoItem.isItemDone = !todoItem.isItemDone
                },
                endToStartAction = {
                    todoItems -= todoItem
                }
            ) {
                OutlinedCard(shape = RectangleShape) {
                    ListItem(
                        headlineContent = { Text(todoItem.itemDescription) },
                        supportingContent = { Text("swipe me to update or remove.") }
                    )
                }
            }
        }
    }
}
// [END android_compose_components_swipecarditemexample]
