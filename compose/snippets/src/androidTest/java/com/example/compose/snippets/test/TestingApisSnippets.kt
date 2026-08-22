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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.SemanticsMatcher
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
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

class TestingApisSnippets {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val matcher = hasText("Button")

    @Test
    fun findersExamples() {
        // [START android_compose_testing_apis_onnode]
        composeTestRule
            .onNode(hasText("Button")) // Equivalent to onNodeWithText("Button")
        // [END android_compose_testing_apis_onnode]

        // [START android_compose_testing_apis_onallnodes]
        composeTestRule
            .onAllNodes(hasText("Button")) // Equivalent to onAllNodesWithText("Button")
        // [END android_compose_testing_apis_onallnodes]

        // [START android_compose_testing_apis_printtolog]
        composeTestRule.onRoot().printToLog("TAG")
        // [END android_compose_testing_apis_printtolog]

        // [START android_compose_testing_apis_printtolog_unmerged]
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG")
        // [END android_compose_testing_apis_printtolog_unmerged]

        // [START android_compose_testing_apis_unmerged_assert]
        composeTestRule
            .onNodeWithText("World", useUnmergedTree = true).assertIsDisplayed()
        // [END android_compose_testing_apis_unmerged_assert]
    }

    @Test
    fun assertionsExamples() {
        // [START android_compose_testing_apis_assertions]
        // Single matcher:
        composeTestRule
            .onNode(matcher)
            .assert(hasText("Button")) // hasText is a SemanticsMatcher

        // Multiple matchers can use and / or
        composeTestRule
            .onNode(matcher).assert(hasText("Button") or hasText("Button2"))
        // [END android_compose_testing_apis_assertions]

        // [START android_compose_testing_apis_collection_assertions]
        // Check number of matched nodes
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertCountEquals(4)
        // At least one matches
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertAny(hasTestTag("Drummer"))
        // All of them match
        composeTestRule
            .onAllNodesWithContentDescription("Beatle").assertAll(hasClickAction())
        // [END android_compose_testing_apis_collection_assertions]
    }

    @Test
    fun matchersAndSelectorsExamples() {
        // [START android_compose_testing_apis_hierarchical_matcher]
        composeTestRule.onNode(hasParent(hasText("Button")))
            .assertIsDisplayed()
        // [END android_compose_testing_apis_hierarchical_matcher]

        // [START android_compose_testing_apis_selectors]
        composeTestRule.onNode(hasTestTag("Players"))
            .onChildren()
            .filter(hasClickAction())
            .assertCountEquals(4)
            .onFirst()
            .assert(hasText("John"))
        // [END android_compose_testing_apis_selectors]
    }

    @Composable
    private fun UnmergedButtonExample() {
        // [START android_compose_testing_apis_unmerged_button]
        MyButton {
            Text("Hello")
            Text("World")
        }
        // [END android_compose_testing_apis_unmerged_button]
    }

    @Composable
    private fun MyButton(content: @Composable RowScope.() -> Unit) {
        Button(onClick = {}) {
            Row(content = content)
        }
    }
}
