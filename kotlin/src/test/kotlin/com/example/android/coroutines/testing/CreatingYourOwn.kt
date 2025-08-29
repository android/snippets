/*
 * Copyright 2022 The Android Open Source Project
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

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CreatingYourOwn {
    // [START coroutine_test_your_own_scope]
    class SimpleExampleTest {
        val testScope = TestScope() // Creates a StandardTestDispatcher

        @Test
        fun someTest() = testScope.runTest {
            // ...
        }
    }
    // [END coroutine_test_your_own_scope]

    // [START coroutine_test_your_own_everything]
    class ExampleTest {
        val testScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        @Test
        fun someTest() = testScope.runTest {
            // ...
        }
    }
    // [END coroutine_test_your_own_everything]
}
