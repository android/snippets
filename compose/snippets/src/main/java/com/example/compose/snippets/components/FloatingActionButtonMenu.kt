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

package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
// [START android_compose_expressive_components_floatingactionbuttonmenu]
@Composable
fun FloatingActionButtonMenuSample() {
    val listState = rememberLazyListState()
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Box {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            for (index in 0 until 100) {
                item { Text(text = "List item - $index", modifier = Modifier.padding(24.dp)) }
            }
        }

        val items =
            listOf(
                AppIcons.Message to "Reply",
                AppIcons.People to "Reply all",
                AppIcons.Contacts to "Forward",
                AppIcons.Snooze to "Snooze",
                AppIcons.Archive to "Archive",
                AppIcons.Label to "Label",
            )

        var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

        FloatingActionButtonMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = fabMenuExpanded,
            button = {
                ToggleFloatingActionButton(
                    modifier =
                        Modifier.semantics {
                            traversalIndex = -1f
                            stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                            contentDescription = "Toggle menu"
                        }
                            .animateFloatingActionButton(
                                visible = fabVisible || fabMenuExpanded,
                                alignment = Alignment.BottomEnd,
                            ),
                    checked = fabMenuExpanded,
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                ) {
                    val imageVector by remember {
                        derivedStateOf { if (checkedProgress > 0.5f) AppIcons.Close else AppIcons.Add }
                    }
                    Icon(
                        painter = rememberVectorPainter(imageVector),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ checkedProgress }),
                    )
                }
            },
        ) {
            items.forEach { item ->
                FloatingActionButtonMenuItem(
                    onClick = { fabMenuExpanded = false },
                    icon = { Icon(painter = rememberVectorPainter(item.first), contentDescription = null) },
                    text = { Text(text = item.second) },
                )
            }
        }
    }
}
// [END android_compose_expressive_components_floatingactionbuttonmenu]
