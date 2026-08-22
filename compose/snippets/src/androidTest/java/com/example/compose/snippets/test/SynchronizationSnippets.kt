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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.test.waitUntilNodeCount
import org.junit.Rule
import org.junit.Test

class SynchronizationSnippets {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val milliseconds = 1000L
    private val timeoutMs = 5000L
    private val condition = true
    private val matcher = hasText("Button")
    private val count = 1
    private val idlingResource = object : IdlingResource {
        override val isIdleNow: Boolean = true
    }

    @Test
    fun disableAutoSyncExample() {
        // [START android_compose_testing_synchronization_autoadvance_false]
        composeTestRule.mainClock.autoAdvance = false
        // [END android_compose_testing_synchronization_autoadvance_false]

        // [START android_compose_testing_synchronization_advancetime]
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(milliseconds)
        // [END android_compose_testing_synchronization_advancetime]
    }

    @Test
    fun idlingResourcesExample() {
        // [START android_compose_testing_synchronization_idlingresource]
        composeTestRule.registerIdlingResource(idlingResource)
        composeTestRule.unregisterIdlingResource(idlingResource)
        // [END android_compose_testing_synchronization_idlingresource]
    }

    @Test
    fun manualSyncExample() {
        // [START android_compose_testing_synchronization_waitforidle]
        composeTestRule.mainClock.autoAdvance = true // Default
        composeTestRule.waitForIdle() // Advances the clock until Compose is idle.

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.waitForIdle() // Only waits for idling resources to become idle.
        // [END android_compose_testing_synchronization_waitforidle]

        // [START android_compose_testing_synchronization_advancetimeuntil]
        composeTestRule.mainClock.advanceTimeUntil(timeoutMs) { condition }
        // [END android_compose_testing_synchronization_advancetimeuntil]
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun waitForConditionsExample() {
        // [START android_compose_testing_synchronization_waituntil]
        composeTestRule.waitUntil(timeoutMs) { condition }
        // [END android_compose_testing_synchronization_waituntil]

        // [START android_compose_testing_synchronization_waituntil_helpers]
        composeTestRule.waitUntilAtLeastOneExists(matcher, timeoutMs)

        composeTestRule.waitUntilDoesNotExist(matcher, timeoutMs)

        composeTestRule.waitUntilExactlyOneExists(matcher, timeoutMs)

        composeTestRule.waitUntilNodeCount(matcher, count, timeoutMs)
        // [END android_compose_testing_synchronization_waituntil_helpers]
    }
}
