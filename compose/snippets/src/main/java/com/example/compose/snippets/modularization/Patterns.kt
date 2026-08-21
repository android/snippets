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

package com.example.compose.snippets.modularization

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private object ModularizationSnippet1 {
    fun navigateToCheckout(navController: NavHostController, bookId: String) {
        // [START android_modularization_patterns_navigation_primitive_id]
        navController.navigate("checkout/$bookId")
        // [END android_modularization_patterns_navigation_primitive_id]
    }
}

private object ModularizationSnippet2 {
    class CheckoutUiState
    class BookRepository {
        fun getBook(id: String): String = ""
    }

    // [START android_modularization_patterns_checkout_viewmodel]
    class CheckoutViewModel(
        savedStateHandle: SavedStateHandle,
        /* [START_EXCLUDE] */
        bookRepository: BookRepository = BookRepository()
        /* [END_EXCLUDE] */
    ) : ViewModel() {

        val uiState: StateFlow<CheckoutUiState> =
            savedStateHandle.getStateFlow<String>("bookId", "").map { bookId ->
                // produce UI state calling bookRepository.getBook(bookId)
                /* [START_EXCLUDE silent] */
                CheckoutUiState()
                /* [END_EXCLUDE] */
            }
                /* [START_EXCLUDE silent] */
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CheckoutUiState())
        /* [END_EXCLUDE] */
        /* ... */
    }
    // [END android_modularization_patterns_checkout_viewmodel]
}
