/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.flow.test

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TurbineTest {
    @Test
    fun usingTurbine() = runTest {
        // [START android_snippets_kotlin_flow_test_using_turbine]
        val dataSource = FakeDataSource()
        val repository = Repository(dataSource)

        repository.scores().test {
            // Make calls that will trigger value changes only within test{}
            dataSource.emit(1)
            assertEquals(10, awaitItem())

            dataSource.emit(2)
            awaitItem() // Ignore items if needed, can also use skip(n)

            dataSource.emit(3)
            assertEquals(30, awaitItem())
        }
        // [END android_snippets_kotlin_flow_test_using_turbine]
    }
}
