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

package com.example.android.coroutines.testing.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UserRepository {
    suspend fun getAllUsers(): List<String>
    suspend fun register(name: String)
}

class FakeUserRepository : UserRepository {
    private var users = listOf<String>()

    override suspend fun getAllUsers(): List<String> = users

    override suspend fun register(name: String) {
        delay(100L) // Simulate work
        users = users + name
        println("Registered $name")
    }
}

// [START android_coroutine_test_user_state]
class UserState(
    private val userRepository: UserRepository,
    private val scope: CoroutineScope,
) {
    private val _users = MutableStateFlow(emptyList<String>())
    val users: StateFlow<List<String>> = _users.asStateFlow()

    fun registerUser(name: String) {
        scope.launch {
            userRepository.register(name)
            _users.update { userRepository.getAllUsers() }
        }
    }
}
// [END android_coroutine_test_user_state]
