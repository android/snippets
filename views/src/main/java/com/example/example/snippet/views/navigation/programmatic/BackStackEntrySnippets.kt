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

package com.example.example.snippet.views.navigation.programmatic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

private class MyViewModel : ViewModel() {
    val liveData: LiveData<String> = MutableLiveData()
}

private fun observeBackStackEntry(myViewModel: MyViewModel, backStackEntry: NavBackStackEntry) {
    // [START android_views_navigation_programmatic_observe_back_stack_entry]
    myViewModel.liveData.observe(
        backStackEntry,
        Observer { myData ->
            // react to live data update.
        }
    )
    // [END android_views_navigation_programmatic_observe_back_stack_entry]
}

private fun setResult(navController: NavController, result: String) {
    // [START android_views_navigation_programmatic_set_result]
    navController.previousBackStackEntry?.savedStateHandle?.set("key", result)
    // [END android_views_navigation_programmatic_set_result]
}
