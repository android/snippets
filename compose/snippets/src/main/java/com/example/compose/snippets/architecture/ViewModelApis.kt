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

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

private object ViewModelApisSnippet1 {
    class MyViewModel : ViewModel()

    // [START android_architecture_viewmodel_apis_default_owner]
    // import androidx.lifecycle.viewmodel.compose.viewModel

    @Composable
    fun MyScreen(
        modifier: Modifier = Modifier,
        // ViewModel API available in lifecycle.lifecycle-viewmodel-compose
        // The ViewModel is scoped to the closest ViewModelStoreOwner provided
        // via the LocalViewModelStoreOwner CompositionLocal. In order of proximity,
        // this could be the destination of a Navigation graph
        // or the host Activity.
        viewModel: MyViewModel = viewModel()
    ) { /* ... */ }
    // [END android_architecture_viewmodel_apis_default_owner]
}

private object ViewModelApisSnippet2 {
    class MyViewModel : ViewModel()

    // [START android_architecture_viewmodel_apis_custom_owner]
    // import androidx.lifecycle.viewmodel.compose.viewModel
    // import androidx.lifecycle.ViewModelStoreOwner

    @Composable
    fun MyScreen(
        // A custom owner passed in, such as a parent NavBackStackEntry
        customOwner: ViewModelStoreOwner,
        // The ViewModel is now scoped to the provided customOwner
        viewModel: MyViewModel = viewModel(viewModelStoreOwner = customOwner)
    ) {
        /* ... */
    }
    // [END android_architecture_viewmodel_apis_custom_owner]
}

private object ViewModelApisSnippet3 {
    class TestViewModel(val data: String) : ViewModel()

    // [START android_architecture_viewmodel_apis_remember_owner]
    @Composable
    fun RememberViewModelStoreOwnerSample() {
        // Create a ViewModelStoreOwner scoped to this specific call site.
        // When this composable leaves the composition,
        // the associated ViewModelStore will be cleared.
        val scopedOwner = rememberViewModelStoreOwner()

        CompositionLocalProvider(LocalViewModelStoreOwner provides scopedOwner) {
            // This ViewModel is scoped to `scopedOwner`.
            // It will survive configuration changes but will be cleared when
            // the composable is removed from the UI tree.
            val viewModel = viewModel { TestViewModel("scoped_data") }
            // Use the ViewModel
        }
    }
    // [END android_architecture_viewmodel_apis_remember_owner]

    // [START android_architecture_viewmodel_apis_remember_provider]
    @Composable
    fun RememberViewModelStoreProviderSample() {
        val storeProvider = rememberViewModelStoreProvider()
        val pages = listOf("Page 1", "Page 2", "Page 3")

        HorizontalPager(state = rememberPagerState { pages.size }) { page ->
            // Create a ViewModelStoreOwner for the specific page using the provider.
            val pageOwner = rememberViewModelStoreOwner(provider = storeProvider, key = page)

            CompositionLocalProvider(LocalViewModelStoreOwner provides pageOwner) {
                val pageViewModel = viewModel { TestViewModel(pages[page]) }
                // Use pageViewModel
            }
        }
    }
    // [END android_architecture_viewmodel_apis_remember_provider]
}

private object ViewModelApisSnippet5 {
    class SharedViewModel : ViewModel()
    lateinit var navController: NavController
    fun composable(route: String, content: @Composable (NavBackStackEntry) -> Unit) {}

    // [START android_architecture_viewmodel_apis_nav_graph]
    // import androidx.lifecycle.viewmodel.compose.viewModel

    @Composable
    fun MyAppNavHost() {
        // ...
        composable("myScreen") { backStackEntry ->
            // Retrieve the NavBackStackEntry of "parentNavigationRoute"
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("parentNavigationRoute")
            }
            // Get the ViewModel scoped to the `parentNavigationRoute` Nav graph
            val parentViewModel: SharedViewModel = viewModel(parentEntry)
            // ...
        }
    }
    // [END android_architecture_viewmodel_apis_nav_graph]
}

private object ViewModelApisSnippet6 {
    class SharedViewModel : ViewModel()
    lateinit var navController: NavController
    fun composable(route: String, content: @Composable (NavBackStackEntry) -> Unit) {}

    // [START android_architecture_viewmodel_apis_hilt_nav_graph]
    // import androidx.hilt.navigation.compose.hiltViewModel

    @Composable
    fun MyAppNavHost() {
        // ...
        composable("myScreen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("parentNavigationRoute")
            }

            // ViewModel API available in hilt.hilt-navigation-compose
            // The ViewModel is scoped to the `parentNavigationRoute` Navigation graph
            // and is provided using the Hilt-generated ViewModel factory
            val parentViewModel: SharedViewModel = hiltViewModel(parentEntry)
            // ...
        }
    }
    // [END android_architecture_viewmodel_apis_hilt_nav_graph]
}
