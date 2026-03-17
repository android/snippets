/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.adaptivelayouts

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CanonicalLayoutSamplesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myFeed_displaysItems() {
        val testData = List(10) { "Item $it" }
        composeTestRule.setContent {
            MyFeed(names = testData)
        }

        testData.forEach {
            composeTestRule.onNodeWithText(it).assertExists()
        }
    }
}
