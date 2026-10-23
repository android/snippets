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

@file:Suppress("unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER")

package com.example.compose.snippets.appstate

import androidx.appstate.transform.testing.transformTest
import androidx.appstate.transform.transform
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

// [START_EXCLUDE]
private interface SearchApi {
    suspend fun search(query: String): List<String>
}

private class FakeMediaRepository : MediaRepository {
    private val _allTracksFlow = MutableStateFlow<List<Track>>(emptyList())
    private val _downloadedTracksFlow = MutableStateFlow<List<Track>>(emptyList())
    override val allTracksFlow: Flow<List<Track>> = _allTracksFlow
    override val downloadedTracksFlow: Flow<List<Track>> = _downloadedTracksFlow

    fun emitAllTracks(tracks: List<Track>) {
        _allTracksFlow.value = tracks
    }

    fun emitDownloadedTracks(tracks: List<Track>) {
        _downloadedTracksFlow.value = tracks
    }
}

private class FakeNetworkMonitor : NetworkMonitor {
    private val _isOnline = mutableStateOf(true)
    override val isOnline: State<Boolean> = _isOnline

    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
}

class TransformTestSnippets {
    private val api = object : SearchApi {
        override suspend fun search(query: String): List<String> {
            return if (query == "compose") {
                listOf("Compose State", "Compose UI")
            } else {
                emptyList()
            }
        }
    }
    // [END_EXCLUDE]

    // [START android_compose_appstate_transform_unit_test]
    @Test
    fun testStateUpdate() = transformTest {
        var sourceState by mutableStateOf(0)

        val state = transform(
            initialValue = 0,
            scope = backgroundScope,
            dispatcher = testDispatcher,
        ) {
            sourceState
        }
        assertThat(state.value).isEqualTo(0)

        // Modify the source state and advance to trigger recomposition.
        sourceState = 1
        advance()

        assertThat(state.value).isEqualTo(1)
    }
    // [END android_compose_appstate_transform_unit_test]

    // [START android_compose_appstate_transform_debounce_test]
    @Test
    fun testDebouncedSearch() = transformTest {
        var query by mutableStateOf("")

        val searchResults = transform<List<String>>(
            initialValue = emptyList(),
            scope = backgroundScope,
            dispatcher = testDispatcher,
        ) {
            var results by remember { mutableStateOf(emptyList<String>()) }
            LaunchedEffect(query) {
                if (query.isBlank()) {
                    results = emptyList()
                    return@LaunchedEffect
                }
                delay(300.milliseconds)
                results = api.search(query)
            }
            results
        }

        query = "compose"
        // Recompose to start the LaunchedEffect for the new query.
        advance()

        // Results don't update before the 300ms debounce delay completes.
        advanceTimeBy(200.milliseconds)
        advance()
        assertThat(searchResults.value).isEmpty()

        // Advance past the remaining 100ms and trigger recomposition.
        advanceTimeBy(100.milliseconds)
        advance()
        assertThat(searchResults.value).containsExactly("Compose State", "Compose UI")
    }
    // [END android_compose_appstate_transform_debounce_test]

    // [START android_compose_appstate_transform_viewmodel_test]
    class HomeViewModel(
        private val mediaRepository: MediaRepository,
        private val networkMonitor: NetworkMonitor,
        coroutineScope: CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ) : ViewModel(coroutineScope) {
        val uiState: State<HomeUiState> = transform<HomeUiState>(
            initialValue = HomeUiState.Loading,
            scope = viewModelScope,
            dispatcher = dispatcher,
        ) {
            val isOnline by networkMonitor.isOnline
            val tracks = if (isOnline) {
                mediaRepository.allTracksFlow.collectAsState(initial = null).value
            } else {
                mediaRepository.downloadedTracksFlow
                    .collectAsState(initial = null).value
            } ?: return@transform HomeUiState.Loading

            HomeUiState.Success(
                tracks = tracks,
                isOnline = isOnline,
            )
        }
    }

    @Test
    fun testHomeViewModelOfflineState() = transformTest {
        val mediaRepository = FakeMediaRepository()
        val networkMonitor = FakeNetworkMonitor()
        val viewModel = HomeViewModel(
            mediaRepository = mediaRepository,
            networkMonitor = networkMonitor,
            coroutineScope = backgroundScope,
            dispatcher = testDispatcher,
        )

        val downloadedTrack = Track(id = "1", isDownloaded = true)
        val streamingTrack = Track(id = "2", isDownloaded = false)
        mediaRepository.emitAllTracks(listOf(downloadedTrack, streamingTrack))
        mediaRepository.emitDownloadedTracks(listOf(downloadedTrack))
        advance()
        assertThat(viewModel.uiState.value).isEqualTo(
            HomeUiState.Success(
                tracks = listOf(downloadedTrack, streamingTrack),
                isOnline = true,
            )
        )

        // Switch offline and verify it switches to downloadedTracksFlow.
        networkMonitor.setOnline(false)
        // First advance() recomposes into the offline branch (where collectAsState
        // initially returns null -> Loading) and starts collecting downloadedTracksFlow.
        advance()
        // Second advance() applies the collected downloadedTracksFlow value and
        // recomposes uiState with the offline Success state.
        advance()
        assertThat(viewModel.uiState.value).isEqualTo(
            HomeUiState.Success(
                tracks = listOf(downloadedTrack),
                isOnline = false,
            )
        )
    }
    // [END android_compose_appstate_transform_viewmodel_test]
}
