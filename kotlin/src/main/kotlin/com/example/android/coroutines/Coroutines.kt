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

package com.example.android.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Placeholder types
class LoginResponse
class LoginResponseParser {
    fun parse(i: java.io.InputStream): LoginResponse = LoginResponse()
}

private object CoroutinesIndex {
    // [START android_kotlin_coroutines_index_repository]
    sealed class Result<out R> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    class LoginRepository(private val responseParser: LoginResponseParser) {
        private val loginUrl = "https://example.com/login"

        // Function that makes the network request, blocking the current thread
        fun makeLoginRequest(
            jsonBody: String
        ): Result<LoginResponse> {
            val url = URL(loginUrl)
            (url.openConnection() as? HttpURLConnection)?.run {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                outputStream.write(jsonBody.toByteArray())
                return Result.Success(responseParser.parse(inputStream))
            }
            return Result.Error(Exception("Cannot open HttpURLConnection"))
        }
    }
    // [END android_kotlin_coroutines_index_repository]

    // [START android_kotlin_coroutines_index_vm_blocking]
    class LoginViewModel(
        private val loginRepository: LoginRepository
    ) : ViewModel() {

        fun login(username: String, token: String) {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            loginRepository.makeLoginRequest(jsonBody)
        }
    }
    // [END android_kotlin_coroutines_index_vm_blocking]

    private object BackgroundSnippet {
        // [START android_kotlin_coroutines_index_vm_background]
        class LoginViewModel(
            private val loginRepository: LoginRepository
        ) : ViewModel() {

            fun login(username: String, token: String) {
                // Create a new coroutine to move the execution off the UI thread
                viewModelScope.launch(Dispatchers.IO) {
                    val jsonBody = "{ username: \"$username\", token: \"$token\"}"
                    loginRepository.makeLoginRequest(jsonBody)
                }
            }
        }
        // [END android_kotlin_coroutines_index_vm_background]
    }

    class LoginRepositoryWithContext(private val responseParser: LoginResponseParser) {
        // [START android_kotlin_coroutines_index_repository_withcontext]
        suspend fun makeLoginRequest(
            jsonBody: String
        ): Result<LoginResponse> {

            // Move the execution of the coroutine to the I/O dispatcher
            return withContext(Dispatchers.IO) {
                // Blocking network request code
                Result.Success(LoginResponse())
            }
        }
        // [END android_kotlin_coroutines_index_repository_withcontext]
    }

    class LoginViewModelWithContext(
        private val loginRepository: LoginRepositoryWithContext
    ) : ViewModel() {

        // [START android_kotlin_coroutines_index_vm_mainsafe]
        fun login(username: String, token: String) {

            // Create a new coroutine on the UI thread
            viewModelScope.launch {
                val jsonBody = "{ username: \"$username\", token: \"$token\"}"

                // Make the network call and suspend execution until it finishes
                val result = loginRepository.makeLoginRequest(jsonBody)

                // Display result of the network request to the user
                when (result) {
                    is Result.Success<LoginResponse> -> { /* Happy path */ }
                    else -> { /* Show error in UI */ }
                }
            }
        }
        // [END android_kotlin_coroutines_index_vm_mainsafe]
    }

    class LoginViewModelException(
        private val loginRepository: LoginRepositoryWithContext
    ) : ViewModel() {

        // [START android_kotlin_coroutines_index_vm_exception]
        fun login(username: String, token: String) {
            viewModelScope.launch {
                val jsonBody = "{ username: \"$username\", token: \"$token\"}"
                val result = try {
                    loginRepository.makeLoginRequest(jsonBody)
                } catch (e: Exception) {
                    Result.Error(Exception("Network request failed"))
                }
                when (result) {
                    is Result.Success<LoginResponse> -> { /* Happy path */ }
                    else -> { /* Show error in UI */ }
                }
            }
        }
        // [END android_kotlin_coroutines_index_vm_exception]
    }
}
