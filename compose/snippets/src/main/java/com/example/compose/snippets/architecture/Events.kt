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

package com.example.compose.snippets.architecture

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private object EventsSnippet1 {
    class LatestNewsViewModel : ViewModel() {
        fun refreshNews() {}
    }

    // [START android_architecture_ui_layer_events_user_events]
    @Composable
    fun LatestNewsScreen(viewModel: LatestNewsViewModel = viewModel()) {

        // State of whether more details should be shown
        var expanded by remember { mutableStateOf(false) }

        Column {
            Text("Some text")
            if (expanded) {
                Text("More details")
            }

            Button(
                // The expand details event is processed by the UI that
                // modifies this composable's internal state.
                onClick = { expanded = !expanded }
            ) {
                val expandText = if (expanded) "Collapse" else "Expand"
                Text("$expandText details")
            }

            // The refresh event is processed by the ViewModel that is in charge
            // of the UI's business logic.
            Button(onClick = { viewModel.refreshNews() }) {
                Text("Refresh data")
            }
        }
    }
    // [END android_architecture_ui_layer_events_user_events]
}

private object EventsSnippet2 {
    // [START android_architecture_ui_layer_events_lazy_list]
    data class MyItem(val id: Int)

    @Composable
    fun MyList(
        items: List<String>,
        onItemClick: (MyItem) -> Unit
    ) {
        Card {
            LazyColumn {
                itemsIndexed(items) { index, string ->
                    ListItem(
                        modifier = Modifier.clickable {
                            onItemClick(MyItem(index))
                        },
                        headlineContent = {
                            Text(text = string)
                        }
                    )
                }
            }
        }
    }
    // [END android_architecture_ui_layer_events_lazy_list]
}

private object EventsSnippet3 {
    // [START android_architecture_ui_layer_events_login_ui_state]
    data class LoginUiState(
        val isLoginInProgress: Boolean = false,
        val errorMessage: String? = null,
        val isUserLoggedIn: Boolean = false
    )
    // [END android_architecture_ui_layer_events_login_ui_state]
}

private object EventsSnippet4 {
    data class LoginUiState(
        val isLoginInProgress: Boolean = false,
        val errorMessage: String? = null,
        val isUserLoggedIn: Boolean = false
    )

    // [START android_architecture_ui_layer_events_login_flow]
    class LoginViewModel : ViewModel() {

        var uiState by mutableStateOf(LoginUiState())

        fun tryLogin(username: String, password: String) {
            viewModelScope.launch {
                // Emit a new state indicating that login is in progress
                uiState = uiState.copy(isLoginInProgress = true)

                uiState = if (login(username, password)) {
                    // Emit a new state indicating that login was successful
                    uiState.copy(isLoginInProgress = false, isUserLoggedIn = true)
                } else {
                    // Emit a new state with the error message
                    LoginUiState(isLoginInProgress = false, errorMessage = "Login failed")
                }
            }
        }

        private suspend fun login(username: String, password: String): Boolean {
            delay(1000)
            return (username == "Hello" && password == "World!")
        }
    }

    @Composable
    fun LoginScreen(viewModel: LoginViewModel, onSuccessfulLogin: () -> Unit) {

        val uiState = viewModel.uiState

        LaunchedEffect(uiState) {
            if (uiState.isUserLoggedIn) {
                onSuccessfulLogin()
            }
        }

        if (uiState.isLoginInProgress) {
            CircularProgressIndicator()
        } else {
            LoginForm(
                onLoginAttempt = { username, password ->
                    viewModel.tryLogin(username, password)
                },
                errorMessage = uiState.errorMessage
            )
        }
    }
    // [END android_architecture_ui_layer_events_login_flow]

    @Composable
    private fun LoginForm(
        onLoginAttempt: (String, String) -> Unit,
        errorMessage: String?
    ) {}
}

private object EventsSnippet5 {
    class News

    // [START android_architecture_ui_layer_events_latest_news_ui_state]
    // Models the UI state for the Latest news screen.
    data class LatestNewsUiState(
        val news: List<News> = emptyList(),
        val isLoading: Boolean = false,
        val userMessage: String? = null
    )
    // [END android_architecture_ui_layer_events_latest_news_ui_state]
}

private object EventsSnippet6 {
    data class LatestNewsUiState(
        val news: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val userMessage: String? = null
    )

    private fun internetConnection(): Boolean = true

    // [START android_architecture_ui_layer_events_latest_news_viewmodel]
    class LatestNewsViewModel(/* ... */) : ViewModel() {

        var uiState by mutableStateOf(LatestNewsUiState())
            private set

        fun refreshNews() {
            viewModelScope.launch {
                // If there isn't internet connection, show a new message on the screen.
                if (!internetConnection()) {
                    uiState = uiState.copy(userMessage = "No Internet connection")
                    return@launch
                }

                // Do something else.
            }
        }

        fun userMessageShown() {
            uiState = uiState.copy(userMessage = null)
        }
    }
    // [END android_architecture_ui_layer_events_latest_news_viewmodel]
}

private object EventsSnippet7 {
    data class LatestNewsUiState(val userMessage: String? = null)
    class LatestNewsViewModel : ViewModel() {
        val uiState: LatestNewsUiState = LatestNewsUiState()
        fun userMessageShown() {}
    }

    // [START android_architecture_ui_layer_events_latest_news_snackbar]
    @Composable
    fun LatestNewsScreen(
        snackbarHostState: SnackbarHostState,
        viewModel: LatestNewsViewModel = viewModel(),
    ) {
        // Rest of the UI content.

        // If there are user messages to show on the screen,
        // show it and notify the ViewModel.
        viewModel.uiState.userMessage?.let { userMessage ->
            LaunchedEffect(userMessage) {
                snackbarHostState.showSnackbar(userMessage)
                // Once the message is displayed and dismissed, notify the ViewModel.
                viewModel.userMessageShown()
            }
        }
    }
    // [END android_architecture_ui_layer_events_latest_news_snackbar]
}

private object EventsSnippet8 {
    class LoginViewModel : ViewModel()

    // [START android_architecture_ui_layer_events_login_help]
    @Composable
    fun LoginScreen(
        onHelp: () -> Unit, // Caller navigates to the help screen
        viewModel: LoginViewModel = viewModel()
    ) {
        // Rest of the UI
        Button(
            onClick = dropUnlessResumed { onHelp() }
        ) {
            Text("Get help")
        }
    }
    // [END android_architecture_ui_layer_events_login_help]
}

private object EventsSnippet9 {
    data class LoginUiState(val isUserLoggedIn: Boolean = false)
    class LoginViewModel : ViewModel() {
        val uiState = LoginUiState()
        fun tryLogin() {}
    }

    // [START android_architecture_ui_layer_events_navigation_login]
    @Composable
    fun LoginScreen(
        onUserLogIn: () -> Unit, // Caller navigates to the right screen
        viewModel: LoginViewModel = viewModel()
    ) {
        Button(
            onClick = {
                // ViewModel validation is triggered
                viewModel.tryLogin()
            }
        ) {
            Text("Log in")
        }
        // Rest of the UI

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val currentOnUserLogIn by rememberUpdatedState(onUserLogIn)
        LaunchedEffect(viewModel, lifecycle) {
            // Whenever the uiState changes, check if the user is logged in and
            // call the `onUserLogin` event when `lifecycle` is at least STARTED
            snapshotFlow { viewModel.uiState }
                .filter { it.isUserLoggedIn }
                .flowWithLifecycle(lifecycle)
                .collect {
                    currentOnUserLogIn()
                }
        }
    }
    // [END android_architecture_ui_layer_events_navigation_login]
}

private object EventsSnippet10 {
    data class DobValidationUiState(val isDobValid: Boolean = false)

    // [START android_architecture_ui_layer_events_dob_validation]
    class DobValidationViewModel(/* ... */) : ViewModel() {
        var uiState by mutableStateOf(DobValidationUiState())
            private set
        /* [START_EXCLUDE silent] */
        fun validateInput() {}
        /* [END_EXCLUDE] */
    }

    @Composable
    fun DobValidationScreen(
        onNavigateToNextScreen: () -> Unit, // Caller navigates to the right screen
        viewModel: DobValidationViewModel = viewModel()
    ) {
        // TextField that updates the ViewModel when a date of birth is selected

        var validationInProgress by rememberSaveable { mutableStateOf(false) }

        Button(
            onClick = {
                viewModel.validateInput()
                validationInProgress = true
            }
        ) {
            Text("Continue")
        }
        // Rest of the UI

        /*
         * The following code implements the requirement of advancing automatically
         * to the next screen when a valid date of birth has been introduced
         * and the user wanted to continue with the registration process.
         */

        if (validationInProgress) {
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            val currentNavigateToNextScreen by rememberUpdatedState(onNavigateToNextScreen)
            LaunchedEffect(viewModel, lifecycle) {
                // If the date of birth is valid and the validation is in progress,
                // navigate to the next screen when `lifecycle` is at least STARTED,
                // which is the default Lifecycle.State for the `flowWithLifecycle` operator.
                snapshotFlow { viewModel.uiState }
                    .filter { it.isDobValid }
                    .flowWithLifecycle(lifecycle)
                    .collect {
                        validationInProgress = false
                        currentNavigateToNextScreen()
                    }
            }
        }
    }
    // [END android_architecture_ui_layer_events_dob_validation]
}
