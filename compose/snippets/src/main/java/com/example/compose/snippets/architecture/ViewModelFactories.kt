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

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

private object ViewModelFactoriesSnippet1 {
    interface MyRepository
    class MyApplication : Application() {
        lateinit var myRepository: MyRepository
    }

    // [START android_architecture_viewmodel_factories_companion]
    // import androidx.lifecycle.SavedStateHandle
    // import androidx.lifecycle.ViewModel
    // import androidx.lifecycle.ViewModelProvider
    // import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
    // import androidx.lifecycle.createSavedStateHandle
    // import androidx.lifecycle.viewmodel.initializer
    // import androidx.lifecycle.viewmodel.viewModelFactory

    class MyViewModel(
        private val myRepository: MyRepository,
        private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {

        // ViewModel logic
        // ...

        // Define ViewModel factory in a companion object
        companion object {

            val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val myRepository = (this[APPLICATION_KEY] as MyApplication).myRepository
                    MyViewModel(
                        myRepository = myRepository,
                        savedStateHandle = savedStateHandle
                    )
                }
            }
        }
    }
    // [END android_architecture_viewmodel_factories_companion]

    // [START android_architecture_viewmodel_factories_screen]
    // import androidx.lifecycle.viewmodel.compose.viewModel

    @Composable
    fun MyScreen(
        modifier: Modifier = Modifier,
        viewModel: MyViewModel = viewModel(factory = MyViewModel.Factory)
    ) {
        // ...
    }
    // [END android_architecture_viewmodel_factories_screen]
}

private object ViewModelFactoriesSnippet3 {
    interface MyRepository

    // [START android_architecture_viewmodel_factories_custom_key]
    // import androidx.lifecycle.ViewModel
    // import androidx.lifecycle.ViewModelProvider
    // import androidx.lifecycle.viewModelScope
    // import androidx.lifecycle.viewmodel.CreationExtras
    // import androidx.lifecycle.viewmodel.initializer
    // import androidx.lifecycle.viewmodel.viewModelFactory

    class MyViewModel(
        private val myRepository: MyRepository,
    ) : ViewModel() {
        // ViewModel logic

        // Define ViewModel factory in a companion object
        companion object {

            // Define a custom key using the factory function
            val MY_REPOSITORY_KEY = CreationExtras.Key<MyRepository>()

            val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    // Get the dependency in your factory
                    val myRepository = this[MY_REPOSITORY_KEY] as MyRepository
                    MyViewModel(
                        myRepository = myRepository,
                    )
                }
            }
        }
    }
    // [END android_architecture_viewmodel_factories_custom_key]

    // [START android_architecture_viewmodel_factories_mutable_extras]
    // import androidx.lifecycle.viewmodel.MutableCreationExtras
    // import androidx.lifecycle.viewmodel.compose.viewModel
    // ...
    @Composable
    fun MyApp(myRepository: MyRepository) {
        val extras = MutableCreationExtras().apply {
            set(MyViewModel.MY_REPOSITORY_KEY, myRepository)
        }
        val viewModel: MyViewModel = viewModel(
            factory = MyViewModel.Factory,
            extras = extras,
        )
    }
    // [END android_architecture_viewmodel_factories_mutable_extras]
}
