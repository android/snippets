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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner

private object SavingStatesSnippet1 {
    // [START android_architecture_saving_states_search_manager]
    class SearchManager : SavedStateRegistry.SavedStateProvider {
        companion object {
            private const val QUERY = "query"
        }

        private val query: String? = null

        // ...

        override fun saveState(): Bundle {
            return bundleOf(QUERY to query)
        }
    }
    // [END android_architecture_saving_states_search_manager]
}

private object SavingStatesSnippet2 {
    // [START android_architecture_saving_states_registry_owner]
    class SearchManager(registryOwner: SavedStateRegistryOwner) : SavedStateRegistry.SavedStateProvider {
        companion object {
            private const val PROVIDER = "search_manager"
            private const val QUERY = "query"
        }

        private var query: String? = null

        init {
            // Register a LifecycleObserver for when the Lifecycle hits ON_CREATE
            registryOwner.lifecycle.addObserver(
                LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_CREATE) {
                        val registry = registryOwner.savedStateRegistry

                        // Register this object for future calls to saveState()
                        registry.registerSavedStateProvider(PROVIDER, this)

                        // Get the previously saved state and restore it
                        val state = registry.consumeRestoredStateForKey(PROVIDER)

                        // Apply the previously saved state
                        query = state?.getString(QUERY)
                    }
                }
            )
        }

        override fun saveState(): Bundle {
            return bundleOf(QUERY to query)
        }

        // ...
    }

    class SearchActivity : ComponentActivity() {
        private var searchManager = SearchManager(this)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Set up your Compose UI here
            setContent {
                // ...
            }
        }
    }
    // [END android_architecture_saving_states_registry_owner]
}
