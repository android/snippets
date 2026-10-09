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

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private object RecommendationsSnippet1 {
    class MyViewModel : ViewModel() {
        val uiState: StateFlow<String> = MutableStateFlow("")
    }

    // [START android_architecture_recommendations_collect_lifecycle]
    @Composable
    fun MyScreen(
        viewModel: MyViewModel = viewModel()
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    }
    // [END android_architecture_recommendations_collect_lifecycle]
}

sealed interface NewsFeedUiState {
    object Loading : NewsFeedUiState
}

interface NewsRepository {
    fun getNewsResourcesStream(): Flow<List<String>>
}

private val savedNewsResourcesState: Flow<Set<String>> = MutableStateFlow(emptySet())

private fun Flow<List<String>>.mapToFeedState(
    savedState: Flow<Set<String>>
): Flow<NewsFeedUiState> = map { NewsFeedUiState.Loading }

// [START android_architecture_recommendations_bookmarks_viewmodel]
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    newsRepository: NewsRepository
) : ViewModel() {

    val feedState: StateFlow<NewsFeedUiState> =
        newsRepository
            .getNewsResourcesStream()
            .mapToFeedState(savedNewsResourcesState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NewsFeedUiState.Loading
            )

    // ...
}
// [END android_architecture_recommendations_bookmarks_viewmodel]

@SuppressLint("MissingPermission")
private object RecommendationsSnippet3 {
    // [START android_architecture_recommendations_location_effect]
    @Composable
    fun LocationChangedEffect(
        locationManager: LocationManager,
        onLocationChanged: (Location) -> Unit
    ) {
        val currentOnLocationChanged by rememberUpdatedState(onLocationChanged)

        LifecycleStartEffect(locationManager) {
            val listener = LocationListener { newLocation ->
                currentOnLocationChanged(newLocation)
            }

            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    listener
                )
            } catch (e: SecurityException) {
                // TODO: Handle missing permissions
            }

            onStopOrDispose {
                locationManager.removeUpdates(listener)
            }
        }
    }
    // [END android_architecture_recommendations_location_effect]
}
