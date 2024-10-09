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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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

    // Display the current example and the button to close it.
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
    }

    // Display the list of available examples.
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { currentExample = { BasicDropdownMenu() } }) {
            Text("Action Bar Menu")
        }
        Button(onClick = { currentExample = { TextFieldDropdownMenu() } }) {
            Text("Options Menu")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_basicdropdownmenu]
@Composable
fun BasicDropdownMenu() {
    var shouldDisplayMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = "Basic menu example") },
        actions = {
            IconButton(onClick = { shouldDisplayMenu = !shouldDisplayMenu }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(expanded = shouldDisplayMenu, onDismissRequest = { shouldDisplayMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Refresh") },
                    onClick = { /* Handle refresh! */ }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = { /* Handle settings! */ }
                )
            }
        }
    )
}
// [END android_compose_components_basicdropdownmenu]

@Preview
@Composable
private fun BasicDropdownMenuPreview() {
    BasicDropdownMenu()
}

// [START android_compose_components_textfielddropdownmenu]
@Composable
fun TextFieldDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val options = listOf("Option 1", "Option 2", "Option 3")

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Select an option") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, "Dropdown arrow")
                }
            },
            readOnly = true
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedText = option
                        expanded = false
                    }
                )
            }
        }
    }
}
// [END android_compose_components_textfielddropdownmenu]

@Preview
@Composable
private fun TextFieldMenuPreview() {
    TextFieldDropdownMenu()
}
