package com.example.compose.snippets.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenusExamples() {
    var currentExample by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    // Display the current example and the button to close it.
    Box(modifier = Modifier.fillMaxSize()) {
        currentExample?.let{
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
                DropdownMenuItem(text = { Text("Refresh") }, onClick = { /* Handle refresh! */ })
                DropdownMenuItem(text = { Text("Settings") }, onClick = { /* Handle settings! */ })
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

    Box (modifier = Modifier.fillMaxWidth()) {
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
