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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.android.coroutines.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

// Helper function to let code below compile
private fun ExampleRepository(): Repository = Repository(Dispatchers.IO)

// [START coroutine_test_repo_with_rule_blank]
class ExampleRepository(private val ioDispatcher: CoroutineDispatcher) { /* ... */ }

class RepositoryTestWithRule {
    private val repository = ExampleRepository(/* What TestDispatcher? */)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun someRepositoryTest() = runTest {
        // Test the repository...
        // [START_EXCLUDE]
        assert(repository != null)
        // [END_EXCLUDE]
    }
}
// [END coroutine_test_repo_with_rule_blank]

@RunWith(Enclosed::class)
class DispatchersOutsideTests {
    // [START coroutine_test_repo_with_rule]
    class RepositoryTestWithRule {
        @get:Rule
        val mainDispatcherRule = MainDispatcherRule()

        private val repository = ExampleRepository(mainDispatcherRule.testDispatcher)

        @Test
        fun someRepositoryTest() = runTest { // Takes scheduler from Main
            // Any TestDispatcher created here also takes the scheduler from Main
            val newTestDispatcher = StandardTestDispatcher()

            // Test the repository...
        }
    }
    // [END coroutine_test_repo_with_rule]

    // [START coroutine_test_repo_without_rule]
    class RepositoryTest {
        // Creates the single test scheduler
        private val testDispatcher = UnconfinedTestDispatcher()
        private val repository = ExampleRepository(testDispatcher)

        @Test
        fun someRepositoryTest() = runTest(testDispatcher.scheduler) {
            // Take the scheduler from the TestScope
            val newTestDispatcher = UnconfinedTestDispatcher(this.testScheduler)
            // Or take the scheduler from the first dispatcher, they’re the same
            val anotherTestDispatcher = UnconfinedTestDispatcher(testDispatcher.scheduler)

            // Test the repository...
        }
    }
    // [END coroutine_test_repo_without_rule]
}
