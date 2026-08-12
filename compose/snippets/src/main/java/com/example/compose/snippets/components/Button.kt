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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonExamples() {
    Column(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Filled button:")
        FilledButtonExample(onClick = { Log.d("Filled button", "Filled button clicked.") })
        Text("Filled tonal button:")
        FilledTonalButtonExample(onClick = { Log.d("Filled tonal button", "Filled tonal button clicked.") })
        Text("Elevated button:")
        ElevatedButtonExample(onClick = { Log.d("Elevated button", "Elevated button clicked.") })
        Text("Outlined button:")
        OutlinedButtonExample(onClick = { Log.d("Outlined button", "Outlined button clicked.") })
        Text("Text button")
        TextButtonExample(onClick = { Log.d("Text button", "Text button clicked.") })
        Text("Toggle button:")
        ToggleButtonSample()
        Text("Elevated toggle button:")
        ElevatedToggleButtonSample()
        Text("Tonal toggle button:")
        TonalToggleButtonSample()
        Text("Outlined toggle button:")
        OutlinedToggleButtonSample()
        Text("Button with icon:")
        ButtonWithIconSample()
        Text("Toggle button with icon:")
        ToggleButtonWithIconSample()
        Text("XSmall button with icon:")
        XSmallButtonWithIconSample()
        Text("XSmall toggle button with icon:")
        XSmallToggleButtonWithIconSample()
        Text("Medium button with icon:")
        MediumButtonWithIconSample()
        Text("Medium toggle button with icon:")
        MediumToggleButtonWithIconSample()
        Text("Large button with icon:")
        LargeButtonWithIconSample()
        Text("Large toggle button with icon:")
        LargeToggleButtonWithIconSample()
        Text("XLarge button with icon:")
        XLargeButtonWithIconSample()
        Text("XLarge toggle button with icon:")
        XLargeToggleButtonWithIconSample()
        Text("Square toggle button:")
        SquareToggleButtonSample()
    }
}

// [START android_compose_components_filledbutton]
@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Filled")
    }
}
// [END android_compose_components_filledbutton]

// [START android_compose_components_filledtonalbutton]
@Composable
fun FilledTonalButtonExample(onClick: () -> Unit) {
    FilledTonalButton(onClick = { onClick() }) {
        Text("Tonal")
    }
}
// [END android_compose_components_filledtonalbutton]

// [START android_compose_components_elevatedbutton]
@Composable
fun ElevatedButtonExample(onClick: () -> Unit) {
    ElevatedButton(onClick = { onClick() }) {
        Text("Elevated")
    }
}
// [END android_compose_components_elevatedbutton]

// [START android_compose_components_outlinedbutton]
@Composable
fun OutlinedButtonExample(onClick: () -> Unit) {
    OutlinedButton(onClick = { onClick() }) {
        Text("Outlined")
    }
}
// [END android_compose_components_outlinedbutton]

// [START android_compose_components_textbutton]
@Composable
fun TextButtonExample(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() }
    ) {
        Text("Text Button")
    }
}
// [END android_compose_components_textbutton]

// [START android_compose_expressive_components_filledtogglebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    ToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Button") }
}
// [END android_compose_expressive_components_filledtogglebutton]

// [START android_compose_expressive_components_elevatedtogglebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ElevatedToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    ElevatedToggleButton(checked = checked, onCheckedChange = { checked = it }) {
        Text("Elevated Button")
    }
}
// [END android_compose_expressive_components_elevatedtogglebutton]

// [START android_compose_expressive_components_tonaltogglebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TonalToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    TonalToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Tonal Button") }
}
// [END android_compose_expressive_components_tonaltogglebutton]

// [START android_compose_expressive_components_outlinedtogglebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OutlinedToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    OutlinedToggleButton(checked = checked, onCheckedChange = { checked = it }) {
        Text("Outlined Button")
    }
}
// [END android_compose_expressive_components_outlinedtogglebutton]

// [START android_compose_expressive_components_buttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonWithIconSample() {
    Button(
        onClick = { /* Do something! */ },
        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Favorite,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MinHeight)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MinHeight)))
        Text("Like")
    }
}
// [END android_compose_expressive_components_buttonwithicon]

// [START android_compose_expressive_components_togglebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToggleButtonWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    ToggleButton(checked = checked, onCheckedChange = { checked = it }) {
        Icon(
            if (checked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize),
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Like")
    }
}
// [END android_compose_expressive_components_togglebuttonwithicon]

// [START android_compose_expressive_components_xmsallbuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun XSmallButtonWithIconSample() {
    val size = ButtonDefaults.ExtraSmallContainerHeight
    Button(
        onClick = { /* Do something! */ },
        modifier = Modifier.heightIn(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label")
    }
}
// [END android_compose_expressive_components_xmsallbuttonwithicon]

// [START android_compose_expressive_components_xmsalltogglebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun XSmallToggleButtonWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    val size = ButtonDefaults.ExtraSmallContainerHeight
    ToggleButton(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.heightIn(size),
        shapes = ToggleButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
    ) {
        Icon(
            if (checked) Icons.Filled.Edit else Icons.Outlined.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label")
    }
}
// [END android_compose_expressive_components_xmsalltogglebuttonwithicon]

// [START android_compose_expressive_components_mediumbuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediumButtonWithIconSample() {
    val size = ButtonDefaults.MediumContainerHeight
    Button(
        onClick = { /* Do something! */ },
        modifier = Modifier.heightIn(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_mediumbuttonwithicon]

// [START android_compose_expressive_components_mediumtogglebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediumToggleButtonWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    val size = ButtonDefaults.MediumContainerHeight
    ToggleButton(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.heightIn(size),
        shapes = ToggleButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
    ) {
        Icon(
            if (checked) Icons.Filled.Edit else Icons.Outlined.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_mediumtogglebuttonwithicon]

// [START android_compose_expressive_components_largebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LargeButtonWithIconSample() {
    val size = ButtonDefaults.LargeContainerHeight
    Button(
        onClick = { /* Do something! */ },
        modifier = Modifier.heightIn(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_largebuttonwithicon]

// [START android_compose_expressive_components_largetogglebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LargeToggleButtonWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    val size = ButtonDefaults.LargeContainerHeight
    ToggleButton(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.heightIn(size),
        shapes = ToggleButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
    ) {
        Icon(
            if (checked) Icons.Filled.Edit else Icons.Outlined.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_largetogglebuttonwithicon]

// [START android_compose_expressive_components_xlargebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun XLargeButtonWithIconSample() {
    val size = ButtonDefaults.ExtraLargeContainerHeight
    Button(
        onClick = { /* Do something! */ },
        modifier = Modifier.heightIn(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_xlargebuttonwithicon]

// [START android_compose_expressive_components_xlargetogglebuttonwithicon]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun XLargeToggleButtonWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    val size = ButtonDefaults.ExtraLargeContainerHeight
    ToggleButton(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.heightIn(size),
        shapes = ToggleButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
    ) {
        Icon(
            if (checked) Icons.Filled.Edit else Icons.Outlined.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Label", style = ButtonDefaults.textStyleFor(size))
    }
}
// [END android_compose_expressive_components_xlargetogglebuttonwithicon]

// [START android_compose_expressive_components_squaretogglebutton]
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SquareToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    val shapes =
        ToggleButtonShapes(
            shape = ToggleButtonDefaults.squareShape,
            pressedShape = ToggleButtonDefaults.pressedShape,
            checkedShape = ToggleButtonDefaults.roundShape,
        )
    ToggleButton(checked = checked, onCheckedChange = { checked = it }, shapes = shapes) {
        Text("Button")
    }
}
// [END android_compose_expressive_components_squaretogglebutton]