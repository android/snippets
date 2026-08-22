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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import org.junit.Rule
import org.junit.Test

class InteroperabilitySnippets {

    @get:Rule
    val composeTestRule = createComposeRule()

    // [START android_compose_testing_interop_espresso]
    @Test
    fun androidViewInteropTest() {
        // Check the initial state of a TextView that depends on a Compose state.
        Espresso.onView(withText("Hello Views")).check(matches(isDisplayed()))
        // Click on the Compose button that changes the state.
        composeTestRule.onNodeWithText("Click here").performClick()
        // Check the new value.
        Espresso.onView(withText("Hello Compose")).check(matches(isDisplayed()))
    }
    // [END android_compose_testing_interop_espresso]

    @Composable
    fun UiAutomatorTestTagsExample() {
        // [START android_compose_testing_interop_uiautomator_testtags]
        Scaffold(
            // Enables for all composables in the hierarchy.
            modifier = Modifier.semantics {
                testTagsAsResourceId = true
            }
        ){
            // Modifier.testTag is accessible from UiAutomator for composables nested here.
            LazyColumn(
                modifier = Modifier.testTag("myLazyColumn")
            ){
                // Content
            }
        }
        // [END android_compose_testing_interop_uiautomator_testtags]
    }

    @Test
    fun uiAutomatorFindObjectExample() {
        // [START android_compose_testing_interop_uiautomator_findobject]
        val device = UiDevice.getInstance(getInstrumentation())

        val lazyColumn: UiObject2 = device.findObject(By.res("myLazyColumn"))
        // Some interaction with the lazyColumn.
        // [END android_compose_testing_interop_uiautomator_findobject]
    }
}
