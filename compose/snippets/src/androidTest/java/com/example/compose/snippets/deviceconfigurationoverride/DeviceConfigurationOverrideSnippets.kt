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

package com.example.compose.snippets.deviceconfigurationoverride

import androidx.compose.material3.Text
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.FontWeightAdjustment
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.then
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.interop.MyScreen
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class DeviceConfigurationOverrideSnippetsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Ignore("Snippet test")
    @Test
    fun forcedSize() {
        // [START android_compose_deviceconfigurationoverride_forcedsize]
        composeTestRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(DpSize(1280.dp, 800.dp))
            ) {
                MyScreen() // Will be rendered in the space for 1280dp by 800dp without clipping.
            }
        }
        // [END android_compose_deviceconfigurationoverride_forcedsize]
    }

    @Ignore("Snippet test")
    @Test
    fun then() {
        // [START android_compose_deviceconfigurationoverride_then]
        composeTestRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.FontScale(1.5f) then
                    DeviceConfigurationOverride.FontWeightAdjustment(200)
            ) {
                Text(text = "text with increased scale and weight")
            }
        }
        // [END android_compose_deviceconfigurationoverride_then]
    }
}
