/*
 * Copyright 2023 The Android Open Source Project
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

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonExamples() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Split button (Expressive):")
        SplitButtonExample(onClick = { Log.d("Split button", "Split button clicked.") })
        Text("Connected button group (Expressive):")
        ButtonGroupExample()
        Text("Button with icon & expressive size:")
        ButtonWithIconExample(onClick = { Log.d("Button with icon", "Button clicked.") })
        Text("Square button (Expressive):")
        SquareButtonExample(onClick = { Log.d("Square button", "Square button clicked.") })
        Text("Button with animated shape (Expressive):")
        ButtonWithAnimatedShapeExample(onClick = { Log.d("Button", "Button clicked.") })
        Text("Filled button (Expressive):")
        FilledButtonExample(onClick = { Log.d("Filled button", "Filled button clicked.") })
        Text("Filled tonal button:")
        FilledTonalButtonExample(onClick = { Log.d("Filled tonal button", "Filled tonal button clicked.") })
        Text("Elevated button:")
        ElevatedButtonExample(onClick = { Log.d("Elevated button", "Elevated button clicked.") })
        Text("Outlined button:")
        OutlinedButtonExample(onClick = { Log.d("Outlined button", "Outlined button clicked.") })
        Text("Text button:")
        TextButtonExample(onClick = { Log.d("Text button", "Text button clicked.") })
    }
}

// [START android_compose_components_filledbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Filled")
    }
}
// [END android_compose_components_filledbutton]

// [START android_compose_components_filledtonalbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilledTonalButtonExample(onClick: () -> Unit) {
    FilledTonalButton(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Tonal")
    }
}
// [END android_compose_components_filledtonalbutton]

// [START android_compose_components_elevatedbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ElevatedButtonExample(onClick: () -> Unit) {
    ElevatedButton(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Elevated")
    }
}
// [END android_compose_components_elevatedbutton]

// [START android_compose_components_outlinedbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OutlinedButtonExample(onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Outlined")
    }
}
// [END android_compose_components_outlinedbutton]

// [START android_compose_components_textbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TextButtonExample(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Text Button")
    }
}
// [END android_compose_components_textbutton]

// [START android_compose_components_buttonwithanimatedshape]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonWithAnimatedShapeExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes()
    ) {
        Text("Animated Shape")
    }
}
// [END android_compose_components_buttonwithanimatedshape]

// [START android_compose_components_squarebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SquareButtonExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = ButtonDefaults.squareShape
    ) {
        Text("Square")
    }
}
// [END android_compose_components_squarebutton]

// [START android_compose_components_buttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonWithIconExample(onClick: () -> Unit) {
    val size = ButtonDefaults.MediumContainerHeight
    Button(
        onClick = { onClick() },
        modifier = Modifier.heightIn(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Edit",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Edit", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_components_buttonwithicon]

// [START android_compose_components_splitbutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitButtonExample(onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.LeadingButton(onClick = { onClick() }) {
                Icon(
                    Icons.Filled.Edit,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Edit",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Edit")
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = expanded,
                onCheckedChange = { expanded = it },
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize),
                    contentDescription = "Options",
                )
            }
        }
    )
}
// [END android_compose_components_splitbutton]

// [START android_compose_components_buttongroup]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonGroupExample() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Day", "Week", "Month")
    ButtonGroup {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
            ) {
                Text(label)
            }
        }
    }
}
// [END android_compose_components_buttongroup]
