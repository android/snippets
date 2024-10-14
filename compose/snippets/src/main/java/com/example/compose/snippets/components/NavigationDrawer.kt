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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

const val HELP_TEXT = "Swipe from left to open navigation drawer with nested items."

@Composable
fun NavigationDrawerExamples() {
    NestedNavigationDrawerExample(
        screenContent = {
            Text(HELP_TEXT)
        }
    )
}

@Preview
@Composable
private fun NavigationDrawerExamplesPreview() {
    NavigationDrawerExamples()
}

// [START android_compose_components_navigationdrawergroupitem]
@Composable
fun NavigationDrawerGroupItem(
    label: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    NavigationDrawerItem(
        label = label,
        selected = isExpanded,
        onClick = { isExpanded = !isExpanded },
        icon = {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
    )
    AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            content()
        }
    }
}
// [END android_compose_components_navigationdrawergroupitem]

// [START android_compose_components_nestednavigationdrawerexample]
@Composable
fun NestedNavigationDrawerExample(
    screenContent: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer Title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /* Do something... */ }
                )
                NavigationDrawerGroupItem(
                    label = { Text("Drawer Group Item") },
                    content = {
                        NavigationDrawerItem(
                            label = { Text(text = "Drawer Item") },
                            selected = false,
                            onClick = { /* Do something... */ }
                        )
                        NavigationDrawerGroupItem(
                            label = { Text("Nested Group Item") },
                            content = {
                                NavigationDrawerItem(
                                    label = { Text(text = "Inner Drawer Item") },
                                    selected = false,
                                    onClick = { /* Do something... */ }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Inner Drawer Item") },
                                    selected = false,
                                    onClick = { /* Do something... */ }
                                )
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Drawer Item") },
                            selected = false,
                            onClick = { /* Do something... */ }
                        )
                    }
                )
            }
        }
    ) {
        screenContent()
    }
}
// [END android_compose_components_nestednavigationdrawerexample]

@Preview
@Composable
private fun NestedNavigationDrawerExamplePreview() {
    NestedNavigationDrawerExample{ Text(HELP_TEXT) }
}
