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
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private object ViewModelOverviewSnippet1 {
    // [START android_architecture_viewmodel_dice_roll]
    data class DiceUiState(
        val firstDieValue: Int? = null,
        val secondDieValue: Int? = null,
        val numberOfRolls: Int = 0,
    )

    class DiceRollViewModel : ViewModel() {

        // Expose screen UI state
        private val _uiState = MutableStateFlow(DiceUiState())
        val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

        // Handle business logic
        fun rollDice() {
            _uiState.update { currentState ->
                currentState.copy(
                    firstDieValue = Random.nextInt(from = 1, until = 7),
                    secondDieValue = Random.nextInt(from = 1, until = 7),
                    numberOfRolls = currentState.numberOfRolls + 1,
                )
            }
        }
    }
    // [END android_architecture_viewmodel_dice_roll]

    // [START android_architecture_viewmodel_dice_roll_screen]
    // import androidx.lifecycle.viewmodel.compose.viewModel

    // Use the 'viewModel()' function from the lifecycle-viewmodel-compose artifact
    @Composable
    fun DiceRollScreen(
        viewModel: DiceRollViewModel = viewModel()
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        // Update UI elements
    }
    // [END android_architecture_viewmodel_dice_roll_screen]
}

private object ViewModelOverviewSnippet3 {
    // [START android_architecture_viewmodel_custom_scope_cleared]
    class MyViewModel(
        private val coroutineScope: CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    ) : ViewModel() {

        // Other ViewModel logic ...

        override fun onCleared() {
            coroutineScope.cancel()
        }
    }
    // [END android_architecture_viewmodel_custom_scope_cleared]
}

private object ViewModelOverviewSnippet4 {
    // [START android_architecture_viewmodel_closeable_scope]
    class CloseableCoroutineScope(
        context: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    ) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context
        override fun close() {
            coroutineContext.cancel()
        }
    }

    class MyViewModel(
        private val coroutineScope: CoroutineScope = CloseableCoroutineScope()
    ) : ViewModel(coroutineScope) {
        // Other ViewModel logic ...
    }
    // [END android_architecture_viewmodel_closeable_scope]
}
