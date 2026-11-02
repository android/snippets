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

package com.example.compose.snippets.architecture.di

import javax.inject.Inject
import javax.inject.Scope
import org.junit.Assert.assertEquals
import org.junit.Test

private object DaggerAndroidUnitTest {
    @Scope
    @Retention(value = AnnotationRetention.RUNTIME)
    annotation class ActivityScope

    class UserRepository

    private val fakeUserRepository = UserRepository()

    // [START android_di_dagger_android_unit_test]
    @ActivityScope
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }

    class LoginViewModelTest {

        @Test
        fun `Happy path`() {
            // You don't need Dagger to create an instance of LoginViewModel.
            // You can pass a fake or mock UserRepository.
            val viewModel = LoginViewModel(fakeUserRepository)
            // [START_EXCLUDE silent]
            /*
            // [END_EXCLUDE]
            assertEquals(...)
            // [START_EXCLUDE silent]
             */
            assertEquals(viewModel, viewModel)
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_android_unit_test]
}
