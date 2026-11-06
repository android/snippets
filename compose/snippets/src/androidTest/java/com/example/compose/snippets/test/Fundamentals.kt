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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FundamentalsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun continueButton_displaysWelcome_espresso() {
        // [START android_testing_fundamentals_espresso]
        // When the Continue button is clicked.
        onView(withText("Continue"))
            .perform(click())

        // Then the Welcome screen is displayed.
        onView(withText("Welcome"))
            .check(matches(isDisplayed()))
        // [END android_testing_fundamentals_espresso]
    }

    @Test
    fun continueButton_displaysWelcome_compose() {
        // [START android_testing_fundamentals_compose]
        // When the Continue button is clicked.
        composeTestRule.onNodeWithText("Continue").performClick()

        // Then the Welcome screen is displayed.
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
        // [END android_testing_fundamentals_compose]
    }

    @Test
    fun viewModel_loadsData() {
        // [START android_testing_fundamentals_viewmodel]
        // Given an instance of MyViewModel.
        val viewModel = MyViewModel(myFakeDataRepository)

        // When data is loaded.
        viewModel.loadData()

        // Then it should be exposing data.
        assertTrue(viewModel.data != null)
        // [END android_testing_fundamentals_viewmodel]
    }
}

private class FakeDataRepository

private val myFakeDataRepository = FakeDataRepository()

private class MyViewModel(private val repository: FakeDataRepository) {
    var data: String? = null
        private set

    fun loadData() {
        data = repository.toString()
    }
}
