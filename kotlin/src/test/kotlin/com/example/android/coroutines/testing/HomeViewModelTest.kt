/*
 * Copyright 2022 Google LLC
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

package com.example.android.coroutines.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

// [START coroutine_test_homeviewmodel_test]
class HomeViewModelTest {
    @Test
    fun settingMainDispatcher() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = HomeViewModel()
            viewModel.loadMessage() // Uses testDispatcher, runs its coroutine eagerly
            assertEquals("Greetings!", viewModel.message.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
// [END coroutine_test_homeviewmodel_test]
