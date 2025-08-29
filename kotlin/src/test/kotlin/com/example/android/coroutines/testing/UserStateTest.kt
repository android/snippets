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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.android.coroutines.testing

import com.example.android.coroutines.testing.scope.FakeUserRepository
import com.example.android.coroutines.testing.scope.UserState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

// [START android_coroutine_test_user_state_test]
class UserStateTest {
    @Test
    fun addUserTest() = runTest { // this: TestScope
        val repository = FakeUserRepository()
        val userState = UserState(repository, scope = this)

        userState.registerUser("Mona")
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        assertEquals(listOf("Mona"), userState.users.value)
    }
}
// [END android_coroutine_test_user_state_test]
