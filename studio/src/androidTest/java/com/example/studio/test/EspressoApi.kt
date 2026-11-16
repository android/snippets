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

// [START android_studio_test_espresso_api_screen_orientation_rule]
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import org.junit.Rule
import org.junit.Test

class MyConfigurationTest {

    // 1. Define the Compose test rule
    @get:Rule
    val composeTestRule = createComposeRule()

    // 2. Define the Espresso Device rule for a consistent starting state
    @get:Rule
    val screenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)
}
// [END android_studio_test_espresso_api_screen_orientation_rule]

class SetLandscapeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val screenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    // [START android_studio_test_espresso_api_set_landscape]
    @Test
    fun myRotationTest() {
        // ...
        // Sets the device to landscape orientation during test execution.
        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)
        // ...
    }
    // [END android_studio_test_espresso_api_set_landscape]
}

class RotationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val screenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    // [START android_studio_test_espresso_api_rotation_test]
    @Test
    fun myRotationTest() {
        // ...
        // Sets the device to landscape orientation during test execution.
        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)
        composeTestRule.onNodeWithTag("NavRail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("BottomBar").assertDoesNotExist()
    }
    // [END android_studio_test_espresso_api_rotation_test]
}
