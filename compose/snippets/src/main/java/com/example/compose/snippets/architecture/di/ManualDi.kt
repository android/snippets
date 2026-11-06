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

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

private object RepositoriesExample {
    // [START android_di_manual_repositories]
    class UserRepository(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }

    class UserLocalDataSource { /* ... */ }
    class UserRemoteDataSource(
        private val loginService: LoginRetrofitService
    ) { /* ... */ }
    // [END android_di_manual_repositories]
}

private object LoginActivityNoContainerExample {
    class LoginViewModel(private val userRepository: UserRepository) : ViewModel()
    class LoginViewModelFactory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory

    // [START android_di_manual_login_activity_no_container]
    class ApiService {
        /* Your API implementation here */
    }

    class UserRepository(private val apiService: ApiService) {
        /* Your implementation here */
    }

    class LoginActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Satisfy the dependencies of LoginViewModel recursively,
            // then pass what the UI needs into setContent.
            val apiService = ApiService()
            val userRepository = UserRepository(apiService)

            setContent {
                LoginScreen(userRepository)
            }
        }
    }

    @Composable
    fun LoginScreen(userRepository: UserRepository) {
        val viewModel: LoginViewModel = viewModel(
            factory = LoginViewModelFactory(userRepository)
        )
        // ...
    }
    // [END android_di_manual_login_activity_no_container]
}

private object AppContainerBasicExample {
    class ApiService
    class UserRepository(private val apiService: ApiService)

    // [START android_di_manual_app_container_basic]
    // Container of objects shared across the whole app.
    class AppContainer {

        // apiService and userRepository aren't private and will be exposed.
        val apiService = ApiService()
        val userRepository = UserRepository(apiService)
    }
    // [END android_di_manual_app_container_basic]

    // [START android_di_manual_my_application]
    // Custom Application class that needs to be specified
    // in the AndroidManifest.xml file.
    class MyApplication : Application() {

        // Instance of AppContainer that will be used by all the Activities of the app.
        val appContainer = AppContainer()
    }
    // [END android_di_manual_my_application]
}

private object LoginActivityWithContainerExample {
    class UserRepository
    class AppContainer {
        val userRepository = UserRepository()
    }
    class MyApplication : Application() {
        val appContainer = AppContainer()
    }
    class LoginViewModel(private val userRepository: UserRepository) : ViewModel()
    class LoginViewModelFactory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory

    @Composable
    fun LoginScreen(userRepository: UserRepository) {}

    // [START android_di_manual_login_activity_with_container]
    class LoginActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val appContainer = (application as MyApplication).appContainer

            setContent {
                LoginScreen(appContainer.userRepository)
            }
        }
    }

    // Alternatively, read AppContainer from inside a composable:
    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        val appContainer = (context.applicationContext as MyApplication).appContainer
        val viewModel: LoginViewModel = viewModel(
            factory = LoginViewModelFactory(appContainer.userRepository)
        )
        // ...
    }
    // [END android_di_manual_login_activity_with_container]
}

private object FactoryExample {
    class UserRepository
    class LoginViewModel(private val userRepository: UserRepository)

    // [START android_di_manual_factory]
    // Definition of a Factory interface with a function to create objects of a type.
    interface Factory<T> {
        fun create(): T
    }

    // Factory for LoginViewModel.
    // Since LoginViewModel depends on UserRepository, in order to create instances of
    // LoginViewModel, you need an instance of UserRepository that you pass as a parameter.
    class LoginViewModelFactory(private val userRepository: UserRepository) : Factory<LoginViewModel> {
        override fun create(): LoginViewModel {
            return LoginViewModel(userRepository)
        }
    }
    // [END android_di_manual_factory]
}

private object AppContainerFactoryExample {
    class UserLocalDataSource
    class UserRemoteDataSource(private val loginService: LoginRetrofitService)
    class UserRepository(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    )
    class LoginViewModel(private val userRepository: UserRepository) : ViewModel()
    class LoginViewModelFactory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory
    class MyApplication : Application() {
        val appContainer = AppContainer()
    }

    // [START android_di_manual_app_container_factory]
    // AppContainer exposing the factory (unchanged from the snippet above).
    class AppContainer {
        // [START_EXCLUDE]
        private val localDataSource = UserLocalDataSource()
        private val remoteDataSource = UserRemoteDataSource(LoginRetrofitService())
        // [END_EXCLUDE]
        val userRepository = UserRepository(localDataSource, remoteDataSource)
        val loginViewModelFactory = LoginViewModelFactory(userRepository)
    }

    // Compose entry point + screen composable.
    class LoginActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val appContainer = (application as MyApplication).appContainer
            setContent {
                LoginScreen(appContainer.loginViewModelFactory)
            }
        }
    }

    @Composable
    fun LoginScreen(factory: LoginViewModelFactory) {
        val viewModel: LoginViewModel = viewModel(factory = factory)
        // ...
    }
    // [END android_di_manual_app_container_factory]
}

private object LoginFlowExample {
    class UserLocalDataSource
    class UserRemoteDataSource(private val loginService: LoginRetrofitService)
    class UserRepository(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    )
    class LoginUserData
    class LoginViewModel(private val userRepository: UserRepository) : ViewModel()
    class LoginViewModelFactory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory

    // [START android_di_manual_login_container]
    class LoginContainer(val userRepository: UserRepository) {

        val loginData = LoginUserData()

        val loginViewModelFactory = LoginViewModelFactory(userRepository)
    }

    // AppContainer contains LoginContainer now.
    class AppContainer {
        // [START_EXCLUDE]
        private val localDataSource = UserLocalDataSource()
        private val remoteDataSource = UserRemoteDataSource(LoginRetrofitService())
        // [END_EXCLUDE]
        val userRepository = UserRepository(localDataSource, remoteDataSource)

        // LoginContainer will be null when the user is NOT in the login flow.
        var loginContainer: LoginContainer? = null
    }
    // [END android_di_manual_login_container]

    // [START android_di_manual_login_flow]
    @Composable
    fun LoginFlow(appContainer: AppContainer) {
        val loginContainer = remember(appContainer) {
            LoginContainer(appContainer.userRepository)
        }
        val viewModel: LoginViewModel = viewModel(
            factory = loginContainer.loginViewModelFactory
        )
        // Render the login flow using loginContainer.loginData and viewModel.
    }
    // [END android_di_manual_login_flow]
}

private class LoginRetrofitService
