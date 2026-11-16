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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.v2.runComposeUiTest
import com.example.compose.snippets.designsystems.MyButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RunComposeUiTest {

    @OptIn(ExperimentalTestApi::class)
// [START android_compose_test_runComposeUiTest]
    @Test
    fun testWithCoroutines() = runComposeUiTest {
        setContent {
            var status by remember { mutableStateOf("Loading...") }
            LaunchedEffect(Unit) {
                delay(1000)
                status = "Done!"
            }
            Text(text = status)
        }

        onNodeWithText("Loading...").assertIsDisplayed()
        mainClock.advanceTimeBy(1000 + 16 /* Frame buffer */)
        onNodeWithText("Done!").assertIsDisplayed()
    }
}
// [END android_compose_test_runComposeUiTest]

class Test2 {
// [START android_compose_test_v2_apis]
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWithCoroutines() {
        composeTestRule.setContent {
            var status by remember { mutableStateOf("Loading...") }
            LaunchedEffect(Unit) {
                delay(1000)
                status = "Done!"
            }
            Text(text = status)
        }

        // NOT RECOMMENDED
        // Fails: runTest creates a new, separate scheduler.
        // Advancing time here does NOT advance the compose clock.
        // To fix this without migrating, you would need to share the scheduler
        // by passing 'composeTestRule.mainClock.scheduler' to runTest.
        runTest {
            composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
            advanceTimeBy(1000)
            composeTestRule.onNodeWithText("Done!").assertIsDisplayed()
        }
    }
// [END android_compose_test_v2_apis]

    @Test
    fun testWithCoreAPIs() {
        // [START android_compose_test_select_single_node]
        // Example
        val node = composeTestRule
            .onNode(hasText("Button")) // Equivalent to onNodeWithText("Button")
        // [END android_compose_test_select_single_node]

        // [START android_compose_test_select_multiple_nodes]
        // Example
        val nodes = composeTestRule
            .onAllNodes(hasText("Button")) // Equivalent to onAllNodesWithText("Button")
        // [END android_compose_test_select_multiple_nodes]

        // [START android_compose_test_on_node_with_text]
        composeTestRule
            .onNodeWithText("World", useUnmergedTree = true).assertIsDisplayed()
        // [END android_compose_test_on_node_with_text]

        // [START android_compose_test_print_to_log]
        composeTestRule.onRoot().printToLog("TAG")
        // [END android_compose_test_print_to_log]

        // [START android_compose_test_print_to_log_unmerged]
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG")
        // [END android_compose_test_print_to_log_unmerged]

        val matcher = hasText("")
        // [START android_compose_test_assertions]
        // Single matcher:
        composeTestRule
            .onNode(matcher)
            .assert(hasText("Button")) // hasText is a SemanticsMatcher

        // Multiple matchers can use and / or
        composeTestRule
            .onNode(matcher).assert(hasText("Button") or hasText("Button2"))
        // [END android_compose_test_assertions]

        // [START android_compose_test_assertions_collections]
        // Check number of matched nodes
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertCountEquals(4)
        // At least one matches
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertAny(hasTestTag("Drummer"))
        // All of them match
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertAll(hasClickAction())
        // [END android_compose_test_assertions_collections]

        // [START android_compose_test_actions]
        composeTestRule.onNode(/* [START_EXCLUDE] */ hasText("")/* [END_EXCLUDE] */).performClick()
        // [END android_compose_test_actions]

        // [START android_compose_test_matchers]
        composeTestRule.onNode(hasParent(hasText("Button")))
            .assertIsDisplayed()
        // [END android_compose_test_matchers]

        // [START android_compose_test_selectors]
        composeTestRule.onNode(hasTestTag("Players"))
            .onChildren()
            .filter(hasClickAction())
            .assertCountEquals(4)
            .onFirst()
            .assert(hasText("John"))
        // [END android_compose_test_selectors]

    }

    @Composable
    private fun MyButton(
        content: @Composable RowScope.() -> Unit
    ) {
    }

    @Composable
    private fun TestButton(){
        // [START android_compose_test_unmerged_tree]
        MyButton {
            Text("Hello")
            Text("World")
        }
        // [END android_compose_test_unmerged_tree]
    }
}
