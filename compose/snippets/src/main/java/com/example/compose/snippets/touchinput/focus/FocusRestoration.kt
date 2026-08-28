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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp

@Composable
private fun FocusRestorationLazySample(items: List<String>) {
    // [START android_compose_touchinput_focus_restore_lazy]
    LazyColumn(
        modifier = Modifier.focusRestorer()
    ) {
        items(items) { item ->
            Button(onClick = { /* Handle click */ }) {
                Text(item)
            }
        }
    }
    // [END android_compose_touchinput_focus_restore_lazy]
}

@Composable
private fun FocusRestorationGroupSample() {
    // [START android_compose_touchinput_focus_restore_group]
    Row(
        modifier = Modifier
            .focusGroup()
            .focusRestorer()
    ) {
        Button(onClick = { /* Action 1 */ }) {
            Text("First item")
        }
        Button(onClick = { /* Action 2 */ }) {
            Text("Second item")
        }
        Button(onClick = { /* Action 3 */ }) {
            Text("Third item")
        }
    }
    // [END android_compose_touchinput_focus_restore_group]
}

@Composable
private fun FocusRestorationFallbackSample(items: List<String>) {
    // [START android_compose_touchinput_focus_restore_fallback]
    val firstItemRequester = remember { FocusRequester() }

    LazyColumn(
        modifier = Modifier.focusRestorer { firstItemRequester }
    ) {
        itemsIndexed(items) { index, item ->
            val itemModifier = if (index == 0) {
                Modifier.focusRequester(firstItemRequester)
            } else {
                Modifier
            }
            Button(
                onClick = { /* Handle click */ },
                modifier = itemModifier
            ) {
                Text(item)
            }
        }
    }
    // [END android_compose_touchinput_focus_restore_fallback]
}

@Composable
private fun FocusRestorationNavigationRailSample(
    currentDestination: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // [START android_compose_touchinput_focus_restore_navigation_selected]
    val (homeRequester, searchRequester, settingsRequester) = remember { FocusRequester.createRefs() }

    val selectedRequester = when (currentDestination) {
        "home" -> homeRequester
        "search" -> searchRequester
        "settings" -> settingsRequester
        else -> homeRequester
    }

    NavigationRail(
        modifier = modifier
            .focusProperties {
                // Redirect focus to the selected item when focus enters the navigation rail
                onEnter = { selectedRequester.requestFocus() }
            }
            .focusGroup()
    ) {
        NavigationRailItem(
            selected = currentDestination == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            modifier = Modifier.focusRequester(homeRequester)
        )
        NavigationRailItem(
            selected = currentDestination == "search",
            onClick = { onNavigate("search") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            modifier = Modifier.focusRequester(searchRequester)
        )
        NavigationRailItem(
            selected = currentDestination == "settings",
            onClick = { onNavigate("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            modifier = Modifier.focusRequester(settingsRequester)
        )
    }
    // [END android_compose_touchinput_focus_restore_navigation_selected]
}

@Composable
private fun FocusRestorationDynamicListSample(
    items: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // [START android_compose_touchinput_focus_restore_dynamic_list]
    // Recreate FocusRequester instances when the underlying list changes
    val (containerRequester, firstItemRequester) = remember(items) { FocusRequester.createRefs() }

    LazyColumn(
        modifier = modifier
            .focusRequester(containerRequester)
            .focusProperties {
                // Save the focused child when focus exits the container
                onExit = {
                    containerRequester.saveFocusedChild()
                }
                // Restore the previously focused child when focus enters, or fall back to first item
                onEnter = {
                    if (!containerRequester.restoreFocusedChild()) {
                        firstItemRequester.requestFocus()
                    }
                }
            }
    ) {
        itemsIndexed(items) { index, item ->
            val itemModifier = if (index == 0) {
                Modifier.focusRequester(firstItemRequester)
            } else {
                Modifier
            }
            Card(
                onClick = {
                    // Manually save the focused child before triggering a screen transition
                    containerRequester.saveFocusedChild()
                    onItemClick(item)
                },
                modifier = itemModifier
            ) {
                Text(text = item, modifier = Modifier.padding(16.dp))
            }
        }
    }
    // [END android_compose_touchinput_focus_restore_dynamic_list]
}
