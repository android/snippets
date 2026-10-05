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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private object CoroutinesLifecycleSnippet1 {
    // [START android_architecture_coroutines_viewmodel_scope]
    class MyViewModel : ViewModel() {
        init {
            viewModelScope.launch {
                // Coroutine that will be canceled when the ViewModel is cleared.
            }
        }
    }
    // [END android_architecture_coroutines_viewmodel_scope]
}

private object CoroutinesLifecycleSnippet2 {
    class DashboardViewModel : ViewModel() {
        val userFlow: StateFlow<String> = MutableStateFlow("")
        val feedFlow: StateFlow<List<String>> = MutableStateFlow(emptyList())
    }

    // [START android_architecture_coroutines_dashboard_screen]
    @Composable
    fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
        // Both flows are collected safely in parallel and will emit updates when either changes, the composables will recompose
        val userData by viewModel.userFlow.collectAsStateWithLifecycle()
        val feedData by viewModel.feedFlow.collectAsStateWithLifecycle()

        // ...
    }
    // [END android_architecture_coroutines_dashboard_screen]
}

private object CoroutinesLifecycleSnippet3 {
    sealed interface Result {
        object Loading : Result
        data class Success(val data: String) : Result
    }

    object repository {
        suspend fun fetchData(): Result = Result.Success("data")
    }

    class MyViewModel : ViewModel() {
        // [START android_architecture_coroutines_state_in]
        val uiState: StateFlow<Result> = flow {
            emit(repository.fetchData())
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Result.Loading
            )
        // [END android_architecture_coroutines_state_in]
    }
}
