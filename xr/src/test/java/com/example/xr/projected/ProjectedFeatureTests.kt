/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.projected

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.ProjectedDeviceController
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import androidx.xr.projected.testing.ProjectedTestRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
@OptIn(ExperimentalProjectedApi::class)
class ProjectedFeatureTests {
    // [START androidxr_projected_test_basic_setup]

    @get:Rule
    val projectedTestRule = ProjectedTestRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun testWithConnectedDevice() {
        val projectedContext = ProjectedContext.createProjectedDeviceContext(context)

        assertThat(ProjectedContext.isProjectedDeviceContext(projectedContext)).isTrue()
    }

    // [END androidxr_projected_test_basic_setup]

    // [START androidxr_projected_test_device_disconnection]
    @Test
    fun testDeviceDisconnection() {
        // manually disconnect the device via the rule
        projectedTestRule.isDeviceConnected = false

        assertThrows(IllegalStateException::class.java) {
            ProjectedContext.createProjectedDeviceContext(context)
        }
    }
    // [END androidxr_projected_test_device_disconnection]

    // [START androidxr_projected_test_device_capabilities]
    @Test
    fun testAppBehaviorWithoutDisplayCapabilities() = projectedTestRule.launchTestProjectedDeviceActivity { activity ->
        // disable display capability
        projectedTestRule.capabilities = setOf()

        runBlocking {
            // create the controller
            val controller = ProjectedDeviceController.create(activity)

            // verify the app recognizes the lack of visual UI support
            assertThat(controller.capabilities).doesNotContain(ProjectedDeviceController.Capability.CAPABILITY_VISUAL_UI)
        }
    }
    // [END androidxr_projected_test_device_capabilities]
}
