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

import org.junit.Assert.assertEquals
import org.junit.Test

private object TestDoublesSnippets {
    // [START android_testing_fundamentals_test_doubles_fake]
    object FakeUserRepository : UserRepository {
        fun getUsers() = listOf(UserAlice, UserBob)
    }

    val UserAlice = User("Alice")
    val UserBob = User("Bob")
    // [END android_testing_fundamentals_test_doubles_fake]

    // [START android_testing_fundamentals_test_doubles_viewmodel]
    @Test
    fun viewModelA_loadsUsers_showsFirstUser() {
        // Given a VM using fake data
        val viewModel = ViewModelA(FakeUserRepository) // Kicks off data load on init

        // Verify that the exposed data is correct
        assertEquals(viewModel.firstUserName, UserAlice.name)
    }
    // [END android_testing_fundamentals_test_doubles_viewmodel]
}

private interface UserRepository

private data class User(val name: String)

private class ViewModelA(repository: TestDoublesSnippets.FakeUserRepository) {
    val firstUserName: String = repository.getUsers().first().name
}
