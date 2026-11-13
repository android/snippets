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

package com.example.snippets.connectivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Path

// The page declares these types at the top level with generic names. The private object keeps
// them out of the rest of the package without changing the names the page shows.
private object Connecting {

    // The response type the page's UserService returns. The page doesn't show it, so it only
    // carries the ID the service asks for.
    data class User(val id: String)

    // [START android_connectivity_connecting_user_service]
    interface UserService {
        @GET("/users/{id}")
        suspend fun getUser(@Path("id") id: String): User
    }
    // [END android_connectivity_connecting_user_service]

    // [START android_connectivity_connecting_user_repository]
    class UserRepository constructor(
        private val userService: UserService
    ) {
        suspend fun getUserById(id: String): User {
            return userService.getUser(id)
        }
    }
    // [END android_connectivity_connecting_user_repository]

    // [START android_connectivity_connecting_view_model]
    class MainViewModel constructor(
        savedStateHandle: SavedStateHandle,
        userRepository: UserRepository
    ) : ViewModel() {
        private val userId: String = savedStateHandle["uid"]
            ?: throw IllegalArgumentException("Missing user ID")

        private val _user = MutableLiveData<User>()
        val user = _user as LiveData<User>

        init {
            viewModelScope.launch {
                try {
                    // Calling the repository is safe as it moves execution off
                    // the main thread.
                    val user = userRepository.getUserById(userId)
                    _user.value = user
                } catch (error: Exception) {
                    // Show error message to user.
                }
            }
        }
    }
    // [END android_connectivity_connecting_view_model]
}
