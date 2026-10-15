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

package com.example.compose.snippets.architecture.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.work.HiltWorker
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.work.Configuration
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Note: Helpers and wrappers with @Inject/@AssistedInject use `internal` because Dagger KSP
// rejects injection into private classes.
internal class ExampleRepository @Inject constructor()

internal class MyRepository @Inject constructor()

internal class WorkerDependency @Inject constructor()

private data object MyDestination

internal object HiltJetpackViewModelSnippet {
    // [START android_di_hilt_jetpack_viewmodel]
    @HiltViewModel
    class ExampleViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val repository: ExampleRepository
    ) : ViewModel() {
        // ...
    }
    // [END android_di_hilt_jetpack_viewmodel]

    // [START android_di_hilt_jetpack_activity_viewmodel]
    @AndroidEntryPoint
    class ExampleActivity : AppCompatActivity() {
        private val exampleViewModel: ExampleViewModel by viewModels()
        // ...
    }
    // [END android_di_hilt_jetpack_activity_viewmodel]
}

internal object HiltJetpackAssistedViewModelSnippet {
    // [START android_di_hilt_jetpack_assisted_viewmodel]
    @HiltViewModel(assistedFactory = MyViewModel.Factory::class)
    class MyViewModel @AssistedInject constructor(
        @Assisted val userId: String,
        private val repository: MyRepository
    ) : ViewModel() {
        @AssistedFactory
        interface Factory {
            fun create(userId: String): MyViewModel
        }
    }
    // [END android_di_hilt_jetpack_assisted_viewmodel]

    @Composable
    private fun MyScreen(viewModel: MyViewModel) {}

    @Composable
    private fun HiltJetpackNav3Snippet() {
        // [START android_di_hilt_jetpack_nav3]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        NavDisplay(...,
          entryDecorators = listOf(..., rememberViewModelStoreNavEntryDecorator()),
          entryProvider = entryProvider {
            entry<MyDestination> { key ->
              val viewModel = hiltViewModel<MyViewModel>()
              MyScreen(viewModel = viewModel)
            }
          }
        )
        // [START_EXCLUDE silent]
        */
        NavDisplay(
            backStack = remember { mutableStateListOf<Any>(MyDestination) },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<MyDestination> { key ->
                    val viewModel = hiltViewModel<MyViewModel>()
                    MyScreen(viewModel = viewModel)
                }
            }
        )
        // [END_EXCLUDE]
        // [END android_di_hilt_jetpack_nav3]
    }

    @Composable
    private fun HiltJetpackNavComposeSnippet() {
        // [START android_di_hilt_jetpack_nav_compose]
        val viewModel = hiltViewModel<MyViewModel>()
        // [END android_di_hilt_jetpack_nav_compose]
    }
}

internal object HiltJetpackWorkerSnippet {
    // [START android_di_hilt_jetpack_worker]
    @HiltWorker
    class ExampleWorker @AssistedInject constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        workerDependency: WorkerDependency
    ) : Worker(appContext, workerParams) {
        // [START_EXCLUDE]
        override fun doWork(): Result = Result.success()
        // [END_EXCLUDE]
    }
    // [END android_di_hilt_jetpack_worker]
}

@SuppressLint("RemoveWorkManagerInitializer")
// [START android_di_hilt_jetpack_application]
@HiltAndroidApp
class ExampleApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
// [END android_di_hilt_jetpack_application]
