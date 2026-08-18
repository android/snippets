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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private object UiLayerSnippet1 {
    // [START android_architecture_ui_layer_news_ui_state]
    data class NewsUiState(
        val isSignedIn: Boolean = false,
        val isPremium: Boolean = false,
        val newsItems: List<NewsItemUiState> = listOf(),
        val userMessages: List<Message> = listOf()
    )

    data class NewsItemUiState(
        val title: String,
        val body: String,
        val bookmarked: Boolean = false,
        // [START_EXCLUDE]
        val publicationDate: String = ""
        // [END_EXCLUDE]
    )
    // [END android_architecture_ui_layer_news_ui_state]
}

private object UiLayerSnippet2 {
    class NewsUiState

    interface NewsRepository

    // [START android_architecture_ui_layer_viewmodel_expose_state]
    class NewsViewModel(
        // [START_EXCLUDE]
        repository: NewsRepository
        // [END_EXCLUDE]
    ) : ViewModel() {

        val uiState: NewsUiState = // [START_EXCLUDE]
            NewsUiState()
        // [END_EXCLUDE]
    }
    // [END android_architecture_ui_layer_viewmodel_expose_state]
}

private object UiLayerSnippet3 {
    class NewsUiState

    interface NewsRepository

    // [START android_architecture_ui_layer_viewmodel_mutable_state]
    class NewsViewModel(
        // [START_EXCLUDE]
        repository: NewsRepository
        // [END_EXCLUDE]
    ) : ViewModel() {

        var uiState by mutableStateOf(NewsUiState())
            private set

        // [START_EXCLUDE]
        fun fetchArticles() {}
        // [END_EXCLUDE]
    }
    // [END android_architecture_ui_layer_viewmodel_mutable_state]
}

private object UiLayerSnippet4 {
    data class NewsItemUiState(val title: String)
    data class NewsUiState(
        val newsItems: List<NewsItemUiState> = emptyList(),
        val userMessages: List<Message> = emptyList()
    )
    interface NewsRepository {
        suspend fun newsItemsForCategory(category: String): List<NewsItemUiState>
    }
    private fun getMessagesFromThrowable(ioe: IOException): List<Message> = listOf()

    // [START android_architecture_ui_layer_viewmodel_fetch_articles]
    class NewsViewModel(
        private val repository: NewsRepository,
        // [START_EXCLUDE]
        private val savedStateHandle: SavedStateHandle
        // [END_EXCLUDE]
    ) : ViewModel() {

        var uiState by mutableStateOf(NewsUiState())
            private set

        private var fetchJob: Job? = null

        fun fetchArticles(category: String) {
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                try {
                    val newsItems = repository.newsItemsForCategory(category)
                    uiState = uiState.copy(newsItems = newsItems)
                } catch (ioe: IOException) {
                    // Handle the error and notify the UI when appropriate.
                    val messages = getMessagesFromThrowable(ioe)
                    uiState = uiState.copy(userMessages = messages)
                }
            }
        }
    }
    // [END android_architecture_ui_layer_viewmodel_fetch_articles]
}

private object UiLayerSnippet5 {
    class NewsItemUiState

    // [START android_architecture_ui_layer_news_can_bookmark]
    data class NewsUiState(
        val isSignedIn: Boolean = false,
        val isPremium: Boolean = false,
        val newsItems: List<NewsItemUiState> = listOf()
    )

    val NewsUiState.canBookmarkNews: Boolean get() = isSignedIn && isPremium
    // [END android_architecture_ui_layer_news_can_bookmark]
}

private object UiLayerSnippet6 {
    // [START android_architecture_ui_layer_news_fetching_state]
    data class NewsUiState(
        val isFetchingArticles: Boolean = false,
        // [START_EXCLUDE]
        val isSignedIn: Boolean = false
        // [END_EXCLUDE]
    )
    // [END android_architecture_ui_layer_news_fetching_state]
}

private object UiLayerSnippet7 {
    data class NewsUiState(val isFetchingArticles: Boolean = false)
    class NewsViewModel : ViewModel() {
        var uiState by mutableStateOf(NewsUiState())
    }

    // [START android_architecture_ui_layer_latest_news_screen]
    @Composable
    fun LatestNewsScreen(
        modifier: Modifier = Modifier,
        viewModel: NewsViewModel = viewModel()
    ) {
        Box(modifier.fillMaxSize()) {

            if (viewModel.uiState.isFetchingArticles) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            // Add other UI elements. For example, the list.
        }
    }
    // [END android_architecture_ui_layer_latest_news_screen]
}

private object UiLayerSnippet8 {
    // [START android_architecture_ui_layer_news_user_messages]
    data class Message(val id: Long, val message: String)

    data class NewsUiState(
        val userMessages: List<Message> = listOf(),
        // [START_EXCLUDE]
        val isFetchingArticles: Boolean = false
        // [END_EXCLUDE]
    )
    // [END android_architecture_ui_layer_news_user_messages]
}

private data class Message(val id: Long, val message: String)
