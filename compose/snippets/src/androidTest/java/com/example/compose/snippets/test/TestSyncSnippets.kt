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

import android.widget.Button
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Rule
import org.junit.Test

class TestSyncSnippets {

    @get:Rule val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    // [START android_compose_test_runWithoutImplicitWaitSample]
    @Test
    fun runWithoutImplicitWaitSample() = runComposeUiTest {
        setContent { MainScreen() }
        mainClock.autoAdvance = false

        // Trigger an animation
        onNodeWithText("Start Animation").performClick()

        // Step through the animation frame-by-frame
        while (hasPendingWork()) {
            mainClock.advanceTimeByFrame()
            waitForIdle()
            runOnUiThread {
                // Suppress implicit synchronization inside this block to avoid redundant
                // waits on each node query, making the frame assertions execute much faster.
                runWithoutImplicitWait {
                    val box1 = onNodeWithTag("Box1").fetchSemanticsNode()
                    val box2 = onNodeWithTag("Box2").fetchSemanticsNode()
                    val box3 = onNodeWithTag("Box3").fetchSemanticsNode()

                    // Assert the exact intermediate state of all three properties for this frame
                    assert(box1.boundsInRoot.right <= box2.boundsInRoot.left)
                    assert(box2.boundsInRoot.right <= box3.boundsInRoot.left)
                }
            }
        }
    }
    // [END android_compose_test_runWithoutImplicitWaitSample]

    // [START android_compose_test_testBidirectionalInteropUIUpdates_old]
    @Test
    fun testBidirectionalInteropUIUpdates_old() {
        val scenario = launchFragmentInContainer<InteropFragment>()
        composeTestRule.waitForIdle()
        scenario.onFragment { fragment ->
            fragment.legacyButton.performClick()
        }
        // Jump to Test Thread to verify state settles inside compose
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Legacy Clicks: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Increment Legacy TextView").performClick()
        composeTestRule.waitForIdle()
        // Jump back to Main Thread to verify target view state settles
        scenario.onFragment { fragment ->
            assert(fragment.legacyTextView.text.toString() == "Compose Clicks: 1")
        }
    }
    // [END android_compose_test_testBidirectionalInteropUIUpdates_old]

    // [START android_compose_test_testBidirectionalInteropUIUpdates_new]
    @Test
    fun testBidirectionalInteropUIUpdates_new() {
        val scenario = launchFragmentInContainer<InteropFragment>()
        composeTestRule.waitForIdle()
        scenario.onFragment { fragment ->
            fragment.legacyButton.performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Legacy Clicks: 1").assertIsDisplayed()
            composeTestRule.onNodeWithText("Increment Legacy TextView").performClick()
            composeTestRule.waitForIdle()
            assert(fragment.legacyTextView.text.toString() == "Compose Clicks: 1")
        }
    }
    // [END android_compose_test_testBidirectionalInteropUIUpdates_new]

    @Composable fun MainScreen() { }

    class InteropFragment: Fragment() {
        lateinit var legacyButton: Button
        lateinit var legacyTextView: TextView
    }
}