/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.kmp.snippets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

// [START android_kmp_viewmodel_class]
// commonMain/MainViewModel.kt

class MainViewModel(
    private val repository: DataRepository,
) : ViewModel() { /* some logic */ }

// ViewModelFactory that retrieves the data repository for your app.
val mainViewModelFactory = viewModelFactory {
    initializer {
        MainViewModel(repository = getDataRepository())
    }
}

fun getDataRepository(): DataRepository = DataRepository()
// [END android_kmp_viewmodel_class]

class DataRepository
