/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private object ArchitectureSnippets1 {
    @Composable
    fun ArchitectureSnippets1() {
        // [START android_compose_architecture_architecture1]
        var name by remember { mutableStateOf("") }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        // [END android_compose_architecture_architecture1]
    }
}

private object ArchitectureSnippets2 {
    data class News(val news: String)
    // [START android_compose_architecture_architecture2]
    @Composable
    fun Header(title: String, subtitle: String) {
        // Recomposes when title or subtitle have changed.
    }

    @Composable
    fun Header(news: News) {
        // Recomposes when a new instance of News is passed in.
    }
    // [END android_compose_architecture_architecture2]
}

private object ArchitectureSnippets3 {
    val localizedString = ""
    @OptIn(ExperimentalMaterial3Api::class)
    // [START android_compose_architecture_architecture3]
    @Composable
    fun MyAppTopAppBar(topAppBarText: String, onBackPressed: () -> Unit) {
        TopAppBar(
            title = {
                Text(
                    text = topAppBarText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = localizedString
                    )
                }
            },
            // ...
        )
    }
    // [END android_compose_architecture_architecture3]
}

private sealed class UiState {
    object SignedOut : UiState()
    object InProgress : UiState()
    object Error : UiState()
    object SignIn : UiState()
}

private object ArchitectureSnippets4 {
    // [START android_compose_architecture_architecture4]

    class MyViewModel : ViewModel() {
        private val _uiState = mutableStateOf<UiState>(UiState.SignedOut)
        val uiState: State<UiState>
            get() = _uiState

        // ...
    }
    // [END android_compose_architecture_architecture4]
}

private object ArchitectureSnippets5 {
    // [START android_compose_architecture_architecture5]
    class MyViewModel : ViewModel() {
        private val _uiState = MutableLiveData<UiState>(UiState.SignedOut)
        val uiState: LiveData<UiState>
            get() = _uiState

        // ...
    }

    @Composable
    fun MyComposable(viewModel: MyViewModel) {
        val uiState = viewModel.uiState.observeAsState()
        // ...
    }
    // [END android_compose_architecture_architecture5]
}
