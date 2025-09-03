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

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

// [START android_kmp_viewmodel_screen]
// androidApp/ui/MainScreen.kt

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(
        factory = mainViewModelFactory,
    ),
) {
// observe the viewModel state
}
// [END android_kmp_viewmodel_screen]
