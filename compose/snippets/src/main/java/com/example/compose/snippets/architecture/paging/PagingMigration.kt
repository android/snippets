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

package com.example.compose.snippets.architecture.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

private object PagingMigrationSnippets {
    data class User(val id: String, val name: String = "")
    class ExampleBackendService

    abstract class ItemKeyedUserPagingSource : PagingSource<String, User>() {
        // [START android_architecture_paging_migration_refresh_key_item]
        // Replaces ItemKeyedDataSource.
        override fun getRefreshKey(state: PagingState<String, User>): String? {
            return state.anchorPosition?.let { anchorPosition ->
                state.closestItemToPosition(anchorPosition)?.id
            }
        }
        // [END android_architecture_paging_migration_refresh_key_item]
    }

    abstract class PositionalUserPagingSource : PagingSource<Int, User>() {
        // [START android_architecture_paging_migration_refresh_key_positional]
        // Replacing PositionalDataSource.
        override fun getRefreshKey(state: PagingState<Int, User>): Int? {
            return state.anchorPosition
        }
        // [END android_architecture_paging_migration_refresh_key_positional]
    }

    abstract class ExamplePagingSource(
        val backend: ExampleBackendService,
        val query: String
    ) : PagingSource<Int, User>()

    class MigrationViewModel(
        private val backend: ExampleBackendService,
        private val query: String
    ) : ViewModel() {
        private fun ExamplePagingSource(
            backend: ExampleBackendService,
            query: String
        ): PagingSource<Int, User> = throw UnsupportedOperationException()

        // [START android_architecture_paging_migration_pager_flow]
        val flow = Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 20)
        ) {
            ExamplePagingSource(backend, query)
        }.flow
            .cachedIn(viewModelScope)
        // [END android_architecture_paging_migration_pager_flow]
    }

    class UserViewModel : ViewModel() {
        lateinit var pager: Pager<Int, User>
    }

    @Composable
    fun UserRow(user: User) {}

    // [START android_architecture_paging_migration_screen]
    @Composable
    fun UserScreen(viewModel: UserViewModel) {
        // Collects the Flow into a LazyPagingItems object
        val lazyPagingItems = viewModel.pager.flow.collectAsLazyPagingItems()

        UserList(lazyPagingItems)
    }

    @Composable
    fun UserList(lazyPagingItems: LazyPagingItems<User>) {
        LazyColumn {
            items(
                count = lazyPagingItems.itemCount,
                // Provide a stable key for each item, similar to DiffUtil in Views
                key = lazyPagingItems.itemKey { user -> user.id }
            ) { index ->
                val user = lazyPagingItems[index]
                if (user != null) {
                    UserRow(user = user)
                }
            }
        }
    }
    // [END android_architecture_paging_migration_screen]

    @Composable
    fun LoadingStateListExample(lazyPagingItems: LazyPagingItems<User>) {
        // [START android_architecture_paging_migration_item_content_type]
        LazyColumn {
            // ... items(lazyPagingItems) go here ...

            // Show loading spinner at bottom of list when appending data
            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
        // [END android_architecture_paging_migration_item_content_type]
    }
}
