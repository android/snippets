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

package com.example.android.basics.stateflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

// Placeholder types
class ArticleHeadline

class NewsRepository {
    val favoriteLatestNews: Flow<List<ArticleHeadline>> = flow { emit(emptyList()) }
    suspend fun refreshLatestNews() {}
}

// [START android_kotlin_flow_stateflow_viewmodel]
class LatestNewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(LatestNewsUiState.Success(emptyList()))
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<LatestNewsUiState> = _uiState

    init {
        viewModelScope.launch {
            newsRepository.favoriteLatestNews
                // Update UI with the latest favorite news
                // Writes to the value property of MutableStateFlow,
                // adding a new element to the flow and updating all
                // of its collectors
                .collect { favoriteNews ->
                    _uiState.value = LatestNewsUiState.Success(favoriteNews)
                }
        }
    }
}

// Represents different states for the LatestNews screen
sealed class LatestNewsUiState {
    data class Success(val news: List<ArticleHeadline>) : LatestNewsUiState()
    data class Error(val exception: Throwable) : LatestNewsUiState()
}
// [END android_kotlin_flow_stateflow_viewmodel]

fun showFavoriteNews(news: List<ArticleHeadline>) {}
fun showError(exception: Throwable) {}

// [START android_kotlin_flow_stateflow_activity]
class LatestNewsActivity : ComponentActivity() {
    private val latestNewsViewModel: LatestNewsViewModel = TODO() // getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                latestNewsViewModel.uiState.collect { uiState ->
                    // New value received
                    when (uiState) {
                        is LatestNewsUiState.Success -> showFavoriteNews(uiState.news)
                        is LatestNewsUiState.Error -> showError(uiState.exception)
                    }
                }
            }
        }
    }
}
// [END android_kotlin_flow_stateflow_activity]

// [START android_kotlin_flow_sharein]
class NewsRemoteDataSource(
    private val externalScope: CoroutineScope
) {
    val latestNews: Flow<List<ArticleHeadline>> = flow {
        // [START_EXCLUDE]
        emit(emptyList<ArticleHeadline>())
        // [END_EXCLUDE]
    }.shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )
}
// [END android_kotlin_flow_sharein]

// [START android_kotlin_flow_sharedflow_tick]
// Class that centralizes when the content of the app needs to be refreshed
class TickHandler(
    private val externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 5000
) {
    // Backing property to avoid flow emissions from other classes
    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: SharedFlow<Unit> = _tickFlow

    init {
        externalScope.launch {
            while (true) {
                _tickFlow.emit(Unit)
                delay(tickIntervalMs)
            }
        }
    }
}
// [END android_kotlin_flow_sharedflow_tick]

private object SharedFlowTickSnippet {
    // [START android_kotlin_flow_sharedflow_tick_part2]
    class NewsRepository(
        // ...
        private val tickHandler: TickHandler,
        private val externalScope: CoroutineScope
    ) {
        init {
            externalScope.launch {
                // Listen for tick updates
                tickHandler.tickFlow.collect {
                    refreshLatestNews()
                }
            }
        }

        suspend fun refreshLatestNews() { /* ... */ }
        // ...
    }
    // [END android_kotlin_flow_sharedflow_tick_part2]
}
