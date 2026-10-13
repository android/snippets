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

import android.app.Application
import android.content.Context
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
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

class ExampleRepository @Inject constructor()

class MyRepository @Inject constructor()

class WorkerDependency @Inject constructor()

private data object MyDestination

@Composable
private fun MyScreen(viewModel: MyViewModel) {}

// [START android_di_hilt_jetpack_viewmodel]
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ExampleRepository
) : ViewModel() {
    // ...
}
// [END android_di_hilt_jetpack_viewmodel]

internal object HiltJetpackActivityViewModelSnippet {
    // [START android_di_hilt_jetpack_activity_viewmodel]
    @AndroidEntryPoint
    class ExampleActivity : AppCompatActivity() {
        private val exampleViewModel: ExampleViewModel by viewModels()
        // ...
    }
    // [END android_di_hilt_jetpack_activity_viewmodel]
}

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
private fun HiltJetpackNav3Snippet(backStack: List<Any>) {
    // [START android_di_hilt_jetpack_nav3]
    NavDisplay(
        backStack = backStack,
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
    // [END android_di_hilt_jetpack_nav3]
}

@Composable
private fun HiltJetpackNavComposeSnippet() {
    // [START android_di_hilt_jetpack_nav_compose]
    val viewModel = hiltViewModel<MyViewModel>()
    // [END android_di_hilt_jetpack_nav_compose]
}

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

internal object HiltJetpackApplicationSnippet {
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
}
