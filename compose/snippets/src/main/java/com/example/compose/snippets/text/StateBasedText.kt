/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.then
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel

@Preview
@Composable
fun StateBasedTextSnippets() {
    Column() {
        // [START android_compose_state_text_0]
        TextField(
            state = rememberTextFieldState(initialText = "Hello"),
            label = { Text("Label") }
        )
        // [END android_compose_state_text_0]

        // [START android_compose_state_text_1]
        OutlinedTextField(
            state = rememberTextFieldState(),
            label = { Text("Label") }
        )
        // [END android_compose_state_text_1]
    }
}

@Preview
@Composable
fun StyleTextField() {
    // [START android_compose_state_text_2]
    TextField(
        state = rememberTextFieldState("Hello\nWorld\nInvisible"),
        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
        placeholder = { Text("") },
        textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
        label = { Text("Enter text") },
        modifier = Modifier.padding(20.dp)
    )
    // [END android_compose_state_text_2]
}

@Composable
fun ConfigureLineLimits() {
    // [START android_compose_state_text_3]
    TextField(
        state = rememberTextFieldState(),
        lineLimits = TextFieldLineLimits.SingleLine
    )
    // [END android_compose_state_text_3]
}

@Preview
@Composable
fun Multiline() {
    Spacer(modifier = Modifier.height(15.dp))
    // [START android_compose_state_text_4]
    TextField(
        state = rememberTextFieldState("Hello\nWorld\nHello\nWorld"),
        lineLimits = TextFieldLineLimits.MultiLine(1, 4)
    )
    // [END android_compose_state_text_4]
}

@Composable
fun StyleWithBrush() {
    // [START android_compose_state_text_5]
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
        )
    }
    TextField(
        state = rememberTextFieldState(), textStyle = TextStyle(brush = brush)
    )
    // [END android_compose_state_text_5]
}

@Composable
fun StateHoisting() {
    // [START android_compose_state_text_6]
    val usernameState = rememberTextFieldState()
    TextField(
        state = usernameState,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = { Text("Enter Username") }
    )
    // [END android_compose_state_text_6]
}

@Composable
fun TextFieldInitialState() {
    // [START android_compose_state_text_7]
    TextField(
        state = rememberTextFieldState(initialText = "Username"),
        lineLimits = TextFieldLineLimits.SingleLine,
    )
    // [END android_compose_state_text_7]
}

@Preview(showBackground = true)
@Composable
fun TextFieldBuffer() {
    // [START android_compose_state_text_8]
    val phoneNumberState = rememberTextFieldState("123456789")

    TextField(
        state = phoneNumberState,
        inputTransformation = InputTransformation { // TextFieldBuffer scope
            if (!asCharSequence().isDigitsOnly()) {
                revertAllChanges()
            }
        },
        outputTransformation = OutputTransformation {
            if (length > 0) insert(0, "(")
            if (length > 4) insert(4, ")")
            if (length > 8) insert(8, "-")
        }
    )
    // [END android_compose_state_text_8]
}

@Preview
@Composable
fun EditTextFieldState() {
    Text("Happy Cat")
    val usernameState = rememberTextFieldState("I love Android")
    editTFState(usernameState)
}

fun editTFState(textFieldState: TextFieldState){
    // [START android_compose_state_text_9]
    // textFieldState.text : I love Android
    // textFieldState.selection: TextRange(14, 14)
    textFieldState.edit { insert(14, "!") }
    // textFieldState.text : I love Android!
    // textFieldState.selection: TextRange(15, 15)
    textFieldState.edit { replace(7, 14, "Compose") }
    // textFieldState.text : I love Compose!
    // textFieldState.selection: TextRange(15, 15)
    textFieldState.edit { append("!!!") }
    // textFieldState.text : I love Compose!!!!
    // textFieldState.selection: TextRange(18, 18)
    textFieldState.edit { selectAll() }
    // textFieldState.text : I love Compose!!!!
    // textFieldState.selection: TextRange(0, 18)
    // [END android_compose_state_text_9]

    // [START android_compose_state_text_10]
    textFieldState.setTextAndPlaceCursorAtEnd("I really love Android")
    // textFieldState.text : I really love Android
    // textFieldState.selection : TextRange(21, 21)
    // [END android_compose_state_text_10]

    // [START android_compose_state_text_11]
    textFieldState.clearText()
    // textFieldState.text :
    // textFieldState.selection : TextRange(0, 0)
    // [END android_compose_state_text_11]
}

class TextFieldViewModel : ViewModel() {
    val usernameState = TextFieldState()
    fun validateUsername() {
    }
}
val textFieldViewModel = TextFieldViewModel()

@Composable
fun TextFieldKeyboardOptions() {
    // [START android_compose_state_text_13]
    TextField(
        state = textFieldViewModel.usernameState,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        onKeyboardAction = { performDefaultAction ->
            textFieldViewModel.validateUsername()
            performDefaultAction()
        }
    )
    // [END android_compose_state_text_13]
}

@Composable
fun TextFieldInputTransformation() {
    // [START android_compose_state_text_14]
    TextField(
        state = rememberTextFieldState(),
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = InputTransformation.maxLength(10)
    )
    // [END android_compose_state_text_14]
}

// [START android_compose_state_text_15]
class CustomInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
    }
}
// [END android_compose_state_text_15]

// [START android_compose_state_text_16]
class DigitOnlyInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().isDigitsOnly()) {
            revertAllChanges()
        }
    }
}
// [END android_compose_state_text_16]

@Composable
fun ChainInputTransformation() {
    // [START android_compose_state_text_17]
    TextField(
        state = rememberTextFieldState(),
        inputTransformation = InputTransformation.maxLength(6)
            .then(CustomInputTransformation()),
    )
    // [END android_compose_state_text_17]
}

// [START android_compose_state_text_18]
class CustomOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
    }
}
// [END android_compose_state_text_18]

// [START android_compose_state_text_19]
class PhoneNumberOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 0) insert(0, "(")
        if (length > 4) insert(4, ")")
        if (length > 8) insert(8, "-")
    }
}
// [END android_compose_state_text_19]

@Composable
fun TextFieldOutputTransformation() {
    // [START android_compose_state_text_20]
    TextField(
        state = rememberTextFieldState(),
        outputTransformation = PhoneNumberOutputTransformation()
    )
    // [END android_compose_state_text_20]
}
