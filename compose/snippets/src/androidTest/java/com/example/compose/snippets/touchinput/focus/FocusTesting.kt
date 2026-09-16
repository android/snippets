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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// [START android_compose_touchinput_focus_test_setup]
@RunWith(AndroidJUnit4::class)
class FocusNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()
// [END android_compose_touchinput_focus_test_setup]

    // [START android_compose_touchinput_focus_test_targets]
    @Test
    fun interactiveElement_isFocusTarget() {
        composeTestRule.setContent {
            AppTheme {
                CardListScreen()
            }
        }

        val firstCard = composeTestRule.onNodeWithTag("card_1")
        val secondCard = composeTestRule.onNodeWithTag("card_2")

        // Focus the first element
        firstCard.performClick()
        firstCard.assertIsFocused()
        secondCard.assertIsNotFocused()
    }
    // [END android_compose_touchinput_focus_test_targets]

    // [START android_compose_touchinput_focus_test_1d_traversal]
    @Test
    fun tabKey_navigatesInAppearanceOrder() {
        composeTestRule.setContent {
            AppTheme {
                CardListScreen()
            }
        }

        val firstCard = composeTestRule.onNodeWithTag("card_1")
        val secondCard = composeTestRule.onNodeWithTag("card_2")
        val thirdCard = composeTestRule.onNodeWithTag("card_3")

        firstCard.performClick()
        firstCard.assertIsFocused()

        // Press Tab -> Moves to second card
        firstCard.performKeyInput {
            pressKey(Key.Tab)
        }
        secondCard.assertIsFocused()
        firstCard.assertIsNotFocused()

        // Press Tab -> Moves to third card
        secondCard.performKeyInput {
            pressKey(Key.Tab)
        }
        thirdCard.assertIsFocused()

        // Press Shift + Tab -> Moves backward to second card
        thirdCard.performKeyInput {
            withKeyDown(Key.ShiftLeft) {
                pressKey(Key.Tab)
            }
        }
        secondCard.assertIsFocused()
    }
    // [END android_compose_touchinput_focus_test_1d_traversal]

    // [START android_compose_touchinput_focus_test_2d_traversal]
    @Test
    fun arrowKeys_moveFocusTwoDimensionallyWithoutWrap() {
        composeTestRule.setContent {
            AppTheme {
                GridLayoutScreen()
            }
        }

        val topLeftButton = composeTestRule.onNodeWithTag("btn_top_left")
        val bottomLeftButton = composeTestRule.onNodeWithTag("btn_bottom_left")

        topLeftButton.performClick()
        topLeftButton.assertIsFocused()

        // Down arrow moves focus downward to bottom-left button
        topLeftButton.performKeyInput {
            pressKey(Key.DirectionDown)
        }
        bottomLeftButton.assertIsFocused()

        // Directional keys do not wrap around: pressing Down on bottom element stays focused
        bottomLeftButton.performKeyInput {
            pressKey(Key.DirectionDown)
        }
        bottomLeftButton.assertIsFocused()
    }
    // [END android_compose_touchinput_focus_test_2d_traversal]

    // [START android_compose_touchinput_focus_test_text_fields]
    @Test
    fun textField_handlesTabAccordingToLineLimits() {
        composeTestRule.setContent {
            AppTheme {
                FormScreen()
            }
        }

        val singleLineNode = composeTestRule.onNodeWithTag("single_line_field")
        val multiLineNode = composeTestRule.onNodeWithTag("multi_line_field")
        val submitButtonNode = composeTestRule.onNodeWithTag("submit_button")

        // Single-line field advances focus on Tab
        singleLineNode.performClick()
        singleLineNode.assertIsFocused()
        singleLineNode.performKeyInput { pressKey(Key.Tab) }
        multiLineNode.assertIsFocused()

        // Multi-line field keeps focus and inserts '\t'
        multiLineNode.performKeyInput { pressKey(Key.Tab) }
        multiLineNode.assertIsFocused()
        submitButtonNode.assertIsNotFocused()

        // Shift + Tab escapes multi-line field to previous target
        multiLineNode.performKeyInput {
            withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) }
        }
        singleLineNode.assertIsFocused()
    }
    // [END android_compose_touchinput_focus_test_text_fields]
// [START_EXCLUDE]
}

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    content()
}

@Composable
private fun CardListScreen() {}

@Composable
private fun GridLayoutScreen() {}

@Composable
private fun FormScreen() {}
// [END_EXCLUDE]
