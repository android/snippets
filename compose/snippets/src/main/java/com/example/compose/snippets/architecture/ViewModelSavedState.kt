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

import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot.Companion.withMutableSnapshot
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.serialization.saved
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

private object ViewModelSavedStateSnippet1 {
    // [START android_architecture_viewmodel_savedstate_constructor]
    class SavedStateViewModel(private val state: SavedStateHandle) : ViewModel() { /* ... */ }
    // [END android_architecture_viewmodel_savedstate_constructor]
}

private object ViewModelSavedStateSnippet2 {
    object repository {
        fun getFilteredData(query: String): Flow<List<String>> = flowOf(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    // [START android_architecture_viewmodel_savedstate_stateflow]
    class SavedStateViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

        // Use getMutableStateFlow to read and write the query directly
        private val _query = savedStateHandle.getMutableStateFlow("query", "")
        val query: StateFlow<String> = _query.asStateFlow()

        // Use getStateFlow if you only need a read-only stream to react to changes
        val filteredData: StateFlow<List<String>> =
            query.flatMapLatest {
                repository.getFilteredData(it)
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )

        fun setQuery(newQuery: String) {
            // Updating the MutableStateFlow automatically updates the SavedStateHandle
            _query.value = newQuery
        }
    }
    // [END android_architecture_viewmodel_savedstate_stateflow]
}

private object ViewModelSavedStateSnippet3 {
    // [START android_architecture_viewmodel_savedstate_serializable]
    // import androidx.lifecycle.SavedStateHandle
    // import androidx.lifecycle.ViewModel
    // Ensure you have the savedstate-ktx dependency
    // import androidx.lifecycle.serialization.saved
    // import kotlinx.serialization.Serializable

    @Serializable
    data class UserFilterState(
        val searchQuery: String,
        val minAge: Int,
        val includeInactive: Boolean
    )

    class FilterViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

        // The state is automatically serialized to a Bundle on process death,
        // and deserialized upon recreation.
        var filterState by savedStateHandle.saved {
            UserFilterState(searchQuery = "", minAge = 18, includeInactive = false)
        }

        fun updateQuery(newQuery: String) {
            // Mutating the property automatically updates the underlying SavedStateHandle
            filterState = filterState.copy(searchQuery = newQuery)
        }
    }
    // [END android_architecture_viewmodel_savedstate_serializable]
}

@OptIn(SavedStateHandleSaveableApi::class)
private object ViewModelSavedStateSnippet4 {
    // [START android_architecture_viewmodel_savedstate_saveable]
    class SavedStateViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

        var filteredData: List<String> by savedStateHandle.saveable {
            mutableStateOf(emptyList())
        }

        fun setQuery(query: String) {
            withMutableSnapshot {
                filteredData += query
            }
        }
    }
    // [END android_architecture_viewmodel_savedstate_saveable]
}

private object ViewModelSavedStateSnippet5 {
    // [START android_architecture_viewmodel_savedstate_temp_file_basic]
    class TempFileViewModel : ViewModel() {
        private var tempFile: File? = null

        fun createOrGetTempFile(): File {
            return tempFile ?: File.createTempFile("temp", null).also {
                tempFile = it
            }
        }
    }
    // [END android_architecture_viewmodel_savedstate_temp_file_basic]
}

private object ViewModelSavedStateSnippet6 {
    // [START android_architecture_viewmodel_savedstate_provider]
    private fun File.saveTempFile() = bundleOf("path" to absolutePath)

    class TempFileViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
        private var tempFile: File? = null
        init {
            savedStateHandle.setSavedStateProvider("temp_file") { // saveState()
                if (tempFile != null) {
                    tempFile!!.saveTempFile()
                } else {
                    Bundle()
                }
            }
        }

        fun createOrGetTempFile(): File {
            return tempFile ?: File.createTempFile("temp", null).also {
                tempFile = it
            }
        }
    }
    // [END android_architecture_viewmodel_savedstate_provider]
}

private object ViewModelSavedStateSnippet7 {
    // [START android_architecture_viewmodel_savedstate_restore_provider]
    private fun File.saveTempFile() = bundleOf("path" to absolutePath)

    private fun Bundle.restoreTempFile() = if (containsKey("path")) {
        File(getString("path")!!)
    } else {
        null
    }

    class TempFileViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
        private var tempFile: File? = null
        init {
            val tempFileBundle = savedStateHandle.get<Bundle>("temp_file")
            if (tempFileBundle != null) {
                tempFile = tempFileBundle.restoreTempFile()
            }
            savedStateHandle.setSavedStateProvider("temp_file") { // saveState()
                if (tempFile != null) {
                    tempFile!!.saveTempFile()
                } else {
                    Bundle()
                }
            }
        }

        fun createOrGetTempFile(): File {
            return tempFile ?: File.createTempFile("temp", null).also {
                tempFile = it
            }
        }
    }
    // [END android_architecture_viewmodel_savedstate_restore_provider]
}
