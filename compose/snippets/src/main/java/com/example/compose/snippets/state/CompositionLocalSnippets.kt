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

package com.example.compose.snippets.state

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.snippets.R

object CompositionLocalSnippets1 {
    private val colors = colors()

    // [START android_compose_state_compositionlocal1]
    @Composable
    fun MyApp() {
        // Theme information tends to be defined near the root of the application
        val colors = colors()
    }

    // Some composable deep in the hierarchy
    @Composable
    fun SomeTextLabel(labelText: String) {
        Text(
            text = labelText,
            color = colors.onPrimary // ← need to access colors here
        )
    }
    // [END android_compose_state_compositionlocal1]
}

object CompositionLocalSnippets2 {
    // [START android_compose_state_compositionlocal2]
    @Composable
    fun MyApp() {
        // Provides a Theme whose values are propagated down its `content`
        MaterialTheme {
            // New values for colors, typography, and shapes are available
            // in MaterialTheme's content lambda.

            // ... content here ...
        }
    }

    // Some composable deep in the hierarchy of MaterialTheme
    @Composable
    fun SomeTextLabel(labelText: String) {
        Text(
            text = labelText,
            // `primary` is obtained from MaterialTheme's
            // LocalColors CompositionLocal
            color = MaterialTheme.colors.primary
        )
    }
    // [END android_compose_state_compositionlocal2]
}

object CompositionLocalSnippets3 {
    // [START android_compose_state_compositionlocal3]
    @Composable
    fun CompositionLocalExample() {
        MaterialTheme { // MaterialTheme sets ContentAlpha.high as default
            Column {
                Text("Uses MaterialTheme's provided alpha")
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text("Medium value provided for LocalContentAlpha")
                    Text("This Text also uses the medium value")
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                        DescendantExample()
                    }
                }
            }
        }
    }

    @Composable
    fun DescendantExample() {
        // CompositionLocalProviders also work across composable functions
        Text("This Text uses the disabled alpha now")
    }
    // [END android_compose_state_compositionlocal3]
}

object CompositionLocalSnippets4 {
    // [START android_compose_state_compositionlocal4]
    @Composable
    fun FruitText(fruitSize: Int) {
        // Get `resources` from the current value of LocalContext
        val resources = LocalContext.current.resources
        val fruitText = remember(resources, fruitSize) {
            resources.getQuantityString(R.plurals.fruit_title, fruitSize)
        }
        Text(text = fruitText)
    }
    // [END android_compose_state_compositionlocal4]
}

object CompositionLocalSnippets5_6_7 {
    // [START android_compose_state_compositionlocal5]
    // LocalElevations.kt file

    data class Elevations(val card: Dp = 0.dp, val default: Dp = 0.dp)

    // Define a CompositionLocal global object with a default
    // This instance can be accessed by all composables in the app
    val LocalElevations = compositionLocalOf { Elevations() }
    // [END android_compose_state_compositionlocal5]

    // [START android_compose_state_compositionlocal6]
    // MyActivity.kt file

    class MyActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                // Calculate elevations based on the system theme
                val elevations = if (isSystemInDarkTheme()) {
                    Elevations(card = 1.dp, default = 1.dp)
                } else {
                    Elevations(card = 0.dp, default = 0.dp)
                }

                // Bind elevation as the value for LocalElevations
                CompositionLocalProvider(LocalElevations provides elevations) {
                    // ... Content goes here ...
                    // This part of Composition will see the `elevations` instance
                    // when accessing LocalElevations.current
                }
            }
        }
    }
    // [END android_compose_state_compositionlocal6]

    // [START android_compose_state_compositionlocal7]
    @Composable
    fun SomeComposable() {
        // Access the globally defined LocalElevations variable to get the
        // current Elevations in this part of the Composition
        Card(elevation = LocalElevations.current.card) {
            // Content
        }
    }
    // [END android_compose_state_compositionlocal7]
}

object CompositionLocalSnippets8 {
    // [START android_compose_state_compositionlocal8]
    @Composable
    fun MyComposable(myViewModel: MyViewModel = viewModel()) {
        // ...
        MyDescendant(myViewModel.data)
    }

    // Don't pass the whole object! Just what the descendant needs.
    // Also, don't  pass the ViewModel as an implicit dependency using
    // a CompositionLocal.
    @Composable
    fun MyDescendant(myViewModel: MyViewModel) { /* ... */ }

    // Pass only what the descendant needs
    @Composable
    fun MyDescendant(data: DataToDisplay) {
        // Display data
    }
    // [END android_compose_state_compositionlocal8]
}

object CompositionLocalSnippets9 {
    // [START android_compose_state_compositionlocal9]
    @Composable
    fun MyComposable(myViewModel: MyViewModel = viewModel()) {
        // ...
        MyDescendant(myViewModel)
    }

    @Composable
    fun MyDescendant(myViewModel: MyViewModel) {
        Button(onClick = { myViewModel.loadData() }) {
            Text("Load data")
        }
    }
    // [END android_compose_state_compositionlocal9]
}

object CompositionLocalSnippets10 {
    // [START android_compose_state_compositionlocal10]
    @Composable
    fun MyComposable(myViewModel: MyViewModel = viewModel()) {
        // ...
        ReusableLoadDataButton(
            onLoadClick = {
                myViewModel.loadData()
            }
        )
    }

    @Composable
    fun ReusableLoadDataButton(onLoadClick: () -> Unit) {
        Button(onClick = onLoadClick) {
            Text("Load data")
        }
    }
    // [END android_compose_state_compositionlocal10]
}

object CompositionLocalSnippets11 {
    // [START android_compose_state_compositionlocal11]
    @Composable
    fun MyComposable(myViewModel: MyViewModel = viewModel()) {
        // ...
        ReusablePartOfTheScreen(
            content = {
                Button(
                    onClick = {
                        myViewModel.loadData()
                    }
                ) {
                    Text("Confirm")
                }
            }
        )
    }

    @Composable
    fun ReusablePartOfTheScreen(content: @Composable () -> Unit) {
        Column {
            // ...
            content()
        }
    }
    // [END android_compose_state_compositionlocal11]
}

/***
 * Fakes
 */
fun colors(): Colors = lightColors()

data class DataToDisplay(val title: String)
class MyViewModel : ViewModel() {
    val data = DataToDisplay("")

    fun loadData() {}
}
