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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MenusExamples() {
    var currentExample by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        currentExample?.let {
            it()
            FloatingActionButton(
                onClick = { currentExample = null },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Close example", modifier = Modifier.padding(16.dp))
            }
            return
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { currentExample = { MinimalDropdownMenu() } }) {
                Text("Minimal dropdown menu")
            }
            Button(onClick = { currentExample = { LongBasicDropdownMenu() } }) {
                Text("Dropdown menu with many items")
            }
            Button(onClick = { currentExample = { DropdownMenuWithDetails() } }) {
                Text("Dropdown menu with sections and icons")
            }
            Button(onClick = { currentExample = { DropdownFilter() } }) {
                Text("Menu for applying a filter, attached to a filter chip")
            }
        }
    }
}

// [START android_compose_components_minimaldropdownmenu]
@Composable
fun MinimalDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Option 1") },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { /* Do something... */ }
            )
        }
    }
}
// [END android_compose_components_minimaldropdownmenu]

@Preview
@Composable
fun MinimalDropdownMenuPreview() {
    MinimalDropdownMenu()
}

// [START android_compose_components_longbasicdropdownmenu]
@Composable
fun LongBasicDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    // Placeholder list of 100 strings for demonstration
    val menuItemData = List(100) { "Option ${it + 1}" }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItemData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { /* Do something... */ }
                )
            }
        }
    }
}
// [END android_compose_components_longbasicdropdownmenu]

@Preview
@Composable
fun LongBasicDropdownMenuPreview() {
    LongBasicDropdownMenu()
}

// [START android_compose_components_dropdownmenuwithdetails]
@Composable
fun DropdownMenuWithDetails() {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // First section
            DropdownMenuItem(
                text = { Text("Profile") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = { /* Do something... */ }
            )

            HorizontalDivider()

            // Second section
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                leadingIcon = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
                trailingIcon = { Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null) },
                onClick = { /* Do something... */ }
            )

            HorizontalDivider()

            // Third section
            DropdownMenuItem(
                text = { Text("About") },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Help") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
                trailingIcon = { Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null) },
                onClick = { /* Do something... */ }
            )
        }
    }
}
// [END android_compose_components_dropdownmenuwithdetails]

@Preview
@Composable
fun DropdownMenuWithDetailsPreview() {
    DropdownMenuWithDetails()
}

@Composable
fun DropdownFilter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize(unbounded = true),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Tune, "Filters")
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Time") })
        DropdownFilterChip()
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Wheelchair accessible") })
    }
}

// [START android_compose_components_dropdownfilterchip]
@Composable
fun DropdownFilterChip(modifier: Modifier = Modifier) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedChipText by remember { mutableStateOf<String?>(null) }
    Box(modifier) {
        FilterChip(
            selected = selectedChipText != null,
            onClick = { isDropdownExpanded = !isDropdownExpanded },
            label = { Text(if (selectedChipText == null) "Type" else "$selectedChipText") },
            leadingIcon = { if (selectedChipText != null) Icon(Icons.Default.Check, null) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
        )
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            DropdownMenuItem(
                text = { Text("Running") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.DirectionsRun, null) },
                onClick = {
                    selectedChipText =
                        if (selectedChipText == "Running") null else "Running"
                }
            )
            DropdownMenuItem(
                text = { Text("Walking") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.DirectionsWalk, null) },
                onClick = {
                    selectedChipText =
                        if (selectedChipText == "Walking") null else "Walking"
                }
            )
            DropdownMenuItem(
                text = { Text("Hiking") },
                leadingIcon = { Icon(Icons.Default.Hiking, null) },
                onClick = {
                    selectedChipText =
                        if (selectedChipText == "Hiking") null else "Hiking"
                }
            )
            DropdownMenuItem(
                text = { Text("Cycling") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.DirectionsBike, null) },
                onClick = {
                    selectedChipText =
                        if (selectedChipText == "Cycling") null else "Cycling"
                }
            )
        }
    }
}
// [END android_compose_components_dropdownfilterchip]

@Preview
@Composable
private fun DropdownFilterPreview() {
    DropdownFilter()
}
