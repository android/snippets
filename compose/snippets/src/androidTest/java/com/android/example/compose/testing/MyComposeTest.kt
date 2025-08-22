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

package com.android.example.compose.testing

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

// [START android_snippets_compose_testing_common_patterns]
class MyComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            MyAppTheme {
                MainScreen(uiState = exampleUiState)
            }
        }
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}
// [END android_snippets_compose_testing_common_patterns]

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    content()
}

@Composable
fun MainScreen(uiState: ExampleUiState) {
    var showWelcome by remember { mutableStateOf(false) }
    if (showWelcome) {
        Text(text = "Welcome")
    } else {
        Text(text = "Hello ${uiState.name}")
        Text(text = "Continue", modifier = Modifier.clickable { showWelcome = true })
    }
}

data class ExampleUiState(val name: String)

val exampleUiState = ExampleUiState("world")
