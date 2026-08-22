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
import androidx.compose.ui.test.junit4.v2.createComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MigrateV2Snippets {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = MyViewModel()

    @Test
    fun waitForIdleExample() {
        // [START android_compose_testing_migrate_v2_waitforidle]
        viewModel.loadData()

        // Explicitly run all queued tasks
        composeTestRule.waitForIdle()

        assertEquals(Success, viewModel.state.value)
        // [END android_compose_testing_migrate_v2_waitforidle]
    }

    @Test
    fun runOnIdleExample() {
        // [START android_compose_testing_migrate_v2_runonidle]
        viewModel.loadData()

        // Run the assertion after the UI is idle
        composeTestRule.runOnIdle {
            assertEquals(Success, viewModel.state.value)
        }
        // [END android_compose_testing_migrate_v2_runonidle]
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
    @Test
    fun runCurrentExample() {
        // [START android_compose_testing_migrate_v2_runcurrent]
        composeTestRule.mainClock.scheduler.runCurrent()
        // [END android_compose_testing_migrate_v2_runcurrent]
    }

    private class MyViewModel {
        private val _state = MutableStateFlow(Success)
        val state: StateFlow<Any> = _state
        fun loadData() {}
    }

    private companion object {
        val Success = Any()
    }
}
