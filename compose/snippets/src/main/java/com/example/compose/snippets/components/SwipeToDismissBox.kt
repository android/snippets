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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
        SwipeItemWithAnimationExample()
    }
}

// [START android_compose_components_todoitem]
data class TodoItem(
    val itemDescription: String,
    var isItemDone: Boolean = false
)
// [END android_compose_components_todoitem]

// [START android_compose_components_swipeitem]
@Composable
fun TodoListItem(
    todoItem: TodoItem,
    onToggleDone: (TodoItem) -> Unit,
    onRemove: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if(it == StartToEnd) onToggleDone(todoItem)
            else if(it == EndToStart) onRemove(todoItem)
            // Reset item when toggling done status
            it != StartToEnd
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        backgroundContent = {
            when(swipeToDismissBoxState.dismissDirection) {
                StartToEnd -> {
                    Icon(
                        if(todoItem.isItemDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = if (todoItem.isItemDone) "Done" else "Not done",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue)
                            .wrapContentSize(Alignment.CenterStart)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                EndToStart -> {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                Settled -> {}
            }
        }
    ) {
        ListItem(
            headlineContent = { Text(todoItem.itemDescription) },
            supportingContent = { Text("swipe me to update or remove.") }
        )
    }
}
// [END android_compose_components_swipeitem]

@Preview(showBackground = true)
// [START android_compose_components_swipeitemexample]
@Composable
private fun SwipeItemExample() {
    val todoItems = remember {
        mutableStateListOf(
            TodoItem("Pay bills"), TodoItem("Buy groceries"),
            TodoItem("Go to gym"), TodoItem("Get dinner")
        )
    }

    LazyColumn {
        items(
            items = todoItems,
            key = { it.itemDescription }
        ) { todoItem ->
            TodoListItem(
                todoItem = todoItem,
                onToggleDone = { todoItem ->
                    todoItem.isItemDone = !todoItem.isItemDone
                },
                onRemove = { todoItem ->
                    todoItems -= todoItem
                },
                modifier = Modifier.animateItem()
            )
        }
    }
}
// [END android_compose_components_swipeitemexample]

// [START android_compose_components_swipecarditem]
@Composable
fun TodoListItemWithAnimation(
    todoItem: TodoItem,
    onToggleDone: (TodoItem) -> Unit,
    onRemove: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if(it == StartToEnd) onToggleDone(todoItem)
            else if(it == EndToStart) onRemove(todoItem)
            // Reset item when toggling done status
            it != StartToEnd
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        backgroundContent = {
            when(swipeToDismissBoxState.dismissDirection) {
                StartToEnd -> {
                    Icon(
                        if(todoItem.isItemDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = if (todoItem.isItemDone) "Done" else "Not done",
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawRect(lerp(Color.LightGray, Color.Blue, swipeToDismissBoxState.progress))
                            }
                            .wrapContentSize(Alignment.CenterStart)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                EndToStart -> {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(lerp(Color.LightGray, Color.Red, swipeToDismissBoxState.progress))
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                Settled -> {}
            }
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
// [END android_compose_components_swipecarditem]

@Preview
// [START android_compose_components_swipecarditemexample]
@Composable
private fun SwipeItemWithAnimationExample() {
    val todoItems = remember {
        mutableStateListOf(
            TodoItem("Pay bills"), TodoItem("Buy groceries"),
            TodoItem("Go to gym"), TodoItem("Get dinner")
        )
    }

    LazyColumn {
        items(
            items = todoItems,
            key = { it.itemDescription }
        ) { todoItem ->
            TodoListItemWithAnimation(
                todoItem = todoItem,
                onToggleDone = { todoItem ->
                    todoItem.isItemDone = !todoItem.isItemDone
                },
                onRemove = { todoItem ->
                    todoItems -= todoItem
                },
                modifier = Modifier.animateItem()
            )
        }
    }
}
// [END android_compose_components_swipecarditemexample]
