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

@file:Suppress("unused")

package com.example.compose.snippets.kotlin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt
import kotlin.properties.ReadWriteProperty
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// [START android_compose_kotlin_default_arguments]
fun drawSquare(
    sideLength: Int,
    thickness: Int = 2,
    edgeColor: Color = Color.Black
) {
}
// [END android_compose_kotlin_default_arguments]

fun functionCall() {
    // [START android_compose_kotlin_function_call]
    drawSquare(sideLength = 30, thickness = 5, edgeColor = Color.Red)
    // [END android_compose_kotlin_function_call]
}

@Composable
fun FunctionCallCompose() {
    // [START android_compose_kotlin_function_call_composable_default]
    Text(text = "Hello, Android!")
    // [END android_compose_kotlin_function_call_composable_default]

    // [START android_compose_kotlin_function_call_composable_named_parameters]
    Text(
        text = "Hello, Android!",
        color = Color.Unspecified,
        fontSize = TextUnit.Unspecified,
        letterSpacing = TextUnit.Unspecified,
        overflow = TextOverflow.Clip
    )
    // [END android_compose_kotlin_function_call_composable_named_parameters]
}

@Composable
fun HighOrderFunctions(myClickFunction: () -> Unit) {
    // [START android_compose_kotlin_high_order_function]
    Button(
        // ...
        onClick = myClickFunction
    )
    // [START_EXCLUDE]
    {}
    // [END_EXCLUDE]
    // [END android_compose_kotlin_high_order_function]

    // [START android_compose_kotlin_high_order_function_lambda]
    Button(
        // ...
        onClick = {
            // do something
            // do something else
        }
    ) { /* ... */ }
    // [END android_compose_kotlin_high_order_function_lambda]
}

@Composable
fun TrailingLambdas() {
    // [START android_compose_kotlin_trailing_lambda]
    Column(
        modifier = Modifier.padding(16.dp),
        content = {
            Text("Some text")
            Text("Some more text")
            Text("Last text")
        }
    )
    // [END android_compose_kotlin_trailing_lambda]

    // [START android_compose_kotlin_trailing_lambda_content]
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Some text")
        Text("Some more text")
        Text("Last text")
    }
    // [END android_compose_kotlin_trailing_lambda_content]

    // [START android_compose_kotlin_one_parameter]
    Column {
        Text("Some text")
        Text("Some more text")
        Text("Last text")
    }
    // [END android_compose_kotlin_one_parameter]
}

@Composable
fun ScopesAndReceivers() {
    // [START android_compose_kotlin_row_scope]
    Row {
        Text(
            text = "Hello world",
            // This Text is inside a RowScope so it has access to
            // Alignment.CenterVertically but not to
            // Alignment.CenterHorizontally, which would be available
            // in a ColumnScope.
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
    // [END android_compose_kotlin_row_scope]

    // [START android_compose_kotlin_receiver_scope]
    Box(
        modifier = Modifier.drawBehind {
            // This method accepts a lambda of type DrawScope.() -> Unit
            // therefore in this lambda we can access properties and functions
            // available from DrawScope, such as the `drawRectangle` function.
            drawRect(
                /*...*/
                /* [START_EXCLUDE] */color = Color.Red/* [END_EXCLUDE] */
            )
        }
    )
    // [END android_compose_kotlin_receiver_scope]
}

// [START android_compose_kotlin_delegating_class]
class DelegatingClass {
    var name: String by nameGetterFunction()

    // [START_EXCLUDE]
    private fun nameGetterFunction(): ReadWriteProperty<DelegatingClass, String> {
        TODO("Not yet implemented")
    }
    // [END_EXCLUDE]
}
// [END android_compose_kotlin_delegating_class]

fun delegateAccess() {
    // [START android_compose_kotlin_delegate_access]
    val myDC = DelegatingClass()
    println("The name property is: " + myDC.name)
    // [END android_compose_kotlin_delegate_access]
}

@Composable
fun DelegatedProperties() {
    // [START android_compose_kotlin_delegated_properties]
    var showDialog by remember { mutableStateOf(false) }

    // Updating the var automatically triggers a state change
    showDialog = true
    // [END android_compose_kotlin_delegated_properties]
}

// [START android_compose_kotlin_data_class]
data class Person(val name: String, val age: Int)
// [END android_compose_kotlin_data_class]

fun destructuring() {
    // [START android_compose_kotlin_destructuring]
    val mary = Person(name = "Mary", age = 35)

    // ...

    val (name, age) = mary
    // [END android_compose_kotlin_destructuring]
}

@Composable
fun DestructuringCompose() {
    // [START android_compose_kotlin_destructuring_compose]
    Row {

        val (image, title, subtitle) = createRefs()

        // The `createRefs` function returns a data object;
        // the first three components are extracted into the
        // image, title, and subtitle variables.

        // ...
    }
    // [END android_compose_kotlin_destructuring_compose]
}

data class Message(val message: Message?)

// [START android_compose_kotlin_dsl]
@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn {
        // Add a single item as a header
        item {
            Text("Message List")
        }

        // Add list of messages
        items(messages) { message ->
            Message(message)
        }
    }
}
// [END android_compose_kotlin_dsl]

@Composable
fun FunctionLiterals() {
    // [START android_compose_kotlin_receiver]
    Canvas(Modifier.size(120.dp)) {
        // Draw grey background, drawRect function is provided by the receiver
        drawRect(color = Color.Gray)

        // Inset content by 10 pixels on the left/right sides
        // and 12 by the top/bottom
        inset(10.0f, 12.0f) {
            val quadrantSize = size / 2.0f

            // Draw a rectangle within the inset bounds
            drawRect(
                size = quadrantSize,
                color = Color.Red
            )

            rotate(45.0f) {
                drawRect(size = quadrantSize, color = Color.Blue)
            }
        }
    }
    // [END android_compose_kotlin_receiver]
}

class MyViewModel : ViewModel() {
    fun loadData() {}
}

@Composable
fun Coroutines(scrollState: ScrollState, viewModel: MyViewModel) {
    // [START android_compose_kotlin_coroutines]
    // Create a CoroutineScope that follows this composable's lifecycle
    val composableScope = rememberCoroutineScope()
    Button(
        // ...
        onClick = {
            // Create a new coroutine that scrolls to the top of the list
            // and call the ViewModel to load data
            composableScope.launch {
                scrollState.animateScrollTo(0) // This is a suspend function
                viewModel.loadData()
            }
        }
    ) { /* ... */ }
    // [END android_compose_kotlin_coroutines]
}

@Composable
fun CoroutinesConcurrent(scrollState: ScrollState, viewModel: MyViewModel) {
    // [START android_compose_kotlin_coroutines_concurrent]
    // Create a CoroutineScope that follows this composable's lifecycle
    val composableScope = rememberCoroutineScope()
    Button( // ...
        onClick = {
            // Scroll to the top and load data in parallel by creating a new
            // coroutine per independent work to do
            composableScope.launch {
                scrollState.animateScrollTo(0)
            }
            composableScope.launch {
                viewModel.loadData()
            }
        }
    ) { /* ... */ }
    // [END android_compose_kotlin_coroutines_concurrent]
}

// [START android_compose_kotlin_coroutines_animate]
@Composable
fun MoveBoxWhereTapped() {
    // Creates an `Animatable` to animate Offset and `remember` it.
    val animatedOffset = remember {
        Animatable(Offset(0f, 0f), Offset.VectorConverter)
    }

    Box(
        // The pointerInput modifier takes a suspend block of code
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Create a new CoroutineScope to be able to create new
                // coroutines inside a suspend function
                coroutineScope {
                    while (true) {
                        // Wait for the user to tap on the screen
                        val offset = awaitPointerEventScope {
                            awaitFirstDown().position
                        }
                        // Launch a new coroutine to asynchronously animate to
                        // where the user tapped on the screen
                        launch {
                            // Animate to the pressed position
                            animatedOffset.animateTo(offset)
                        }
                    }
                }
            }
    ) {
        Text("Tap anywhere", Modifier.align(Alignment.Center))
        Box(
            Modifier
                .offset {
                    // Use the animated offset as the offset of this Box
                    IntOffset(
                        animatedOffset.value.x.roundToInt(),
                        animatedOffset.value.y.roundToInt()
                    )
                }
                .size(40.dp)
                .background(Color(0xff3c1361), CircleShape)
        )
    }
    // [END android_compose_kotlin_coroutines_animate]
}
