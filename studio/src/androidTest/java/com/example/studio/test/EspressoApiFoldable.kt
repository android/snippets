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

package com.example.studio.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.device.DeviceInteraction.Companion.setClosedMode
import androidx.test.espresso.device.DeviceInteraction.Companion.setFlatMode
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.controller.DeviceMode.FLAT
import androidx.test.espresso.device.filter.RequiresDeviceMode
import org.junit.Rule
import org.junit.Test

class ClosedModeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // [START android_studio_test_espresso_api_closed_mode]
    @Test
    fun myUnfoldedTest() {
        onDevice().setClosedMode()
        composeTestRule.onNodeWithTag("BottomBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NavRail").assertDoesNotExist()
        // ...
    }
    // [END android_studio_test_espresso_api_closed_mode]
}

class FlatModeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // [START android_studio_test_espresso_api_flat_mode]
    @Test
    fun myUnfoldedTest() {
        onDevice().setClosedMode()
        // ...
        onDevice().setFlatMode()
        composeTestRule.onNodeWithTag("NavRail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("BottomBar").assertDoesNotExist()
    }
    // [END android_studio_test_espresso_api_flat_mode]
}

class RequiresDeviceModeTest {
    // [START android_studio_test_espresso_api_requires_device_mode]
    @Test
    @RequiresDeviceMode(mode = FLAT)
    fun myUnfoldedTest() {
        // ...
    }
    // [END android_studio_test_espresso_api_requires_device_mode]
}
