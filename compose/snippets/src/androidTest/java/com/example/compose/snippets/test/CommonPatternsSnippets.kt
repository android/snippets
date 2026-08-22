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

package com.example.compose.snippets.test

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.compose.snippets.R
import org.junit.Rule
import org.junit.Test

// [START android_compose_testing_common_patterns_custom_semantics_key]
// Creates a semantics property of type Long.
val PickedDateKey = SemanticsPropertyKey<Long>("PickedDate")
var SemanticsPropertyReceiver.pickedDate by PickedDateKey
// [END android_compose_testing_common_patterns_custom_semantics_key]

// [START android_compose_testing_common_patterns_componentactivity]
class MyComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            MyAppTheme {
                MainScreen(uiState = exampleUiState, /*...*/)
            }
        }
        val continueLabel = composeTestRule.activity.getString(R.string.next)
        composeTestRule.onNodeWithText(continueLabel).performClick()
    }
}
// [END android_compose_testing_common_patterns_componentactivity]

// [START android_compose_testing_common_patterns_state_restoration]
class MyStateRestorationTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onRecreation_stateIsRestored() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent { MainScreen() }

        // TODO: Run actions that modify the state

        // Trigger a recreation
        restorationTester.emulateSavedInstanceStateRestore()

        // TODO: Verify that state has been correctly restored.
    }
}
// [END android_compose_testing_common_patterns_state_restoration]

class CustomSemanticsUsageAndAssertion {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    fun CustomSemanticsUsage() {
        // [START android_compose_testing_common_patterns_custom_semantics_usage]
        val datePickerValue by remember { mutableStateOf(0L) }
        MyCustomDatePicker(
            modifier = Modifier.semantics { pickedDate = datePickerValue }
        )
        // [END android_compose_testing_common_patterns_custom_semantics_usage]
    }

    @Test
    fun customSemanticsAssertion() {
        // [START android_compose_testing_common_patterns_custom_semantics_assertion]
        composeTestRule
            .onNode(SemanticsMatcher.expectValue(PickedDateKey, 1445378400)) // 2015-10-21
            .assertExists()
        // [END android_compose_testing_common_patterns_custom_semantics_assertion]
    }
}

@Composable
private fun MyAppTheme(content: @Composable () -> Unit) {
    content()
}

@Composable
private fun MainScreen(uiState: Any? = null) {
    Text("MainScreen")
}

private val exampleUiState = Any()

@Composable
private fun MyCustomDatePicker(modifier: Modifier = Modifier) {
    Text("DatePicker", modifier = modifier)
}
