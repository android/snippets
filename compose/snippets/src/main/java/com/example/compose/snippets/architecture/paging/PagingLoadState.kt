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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf

private object PagingLoadStateSnippets {
    data class User(val id: String, val name: String = "")

    @Composable
    fun UserItem(user: User?) {}

    @Composable
    fun ErrorButton(message: String, onClick: () -> Unit, modifier: Modifier = Modifier) {}

    object RefreshLoadStateScope {
        class UserViewModel : ViewModel() {
            val flow: Flow<PagingData<User>> = flowOf()
        }

        // [START android_architecture_paging_load_state_refresh]
        @Composable
        fun UserListScreen(viewModel: UserViewModel) {
            val pagingItems = viewModel.flow.collectAsLazyPagingItems()

            Box(modifier = Modifier.fillMaxSize()) {
                // Show the list content
                LazyColumn {
                    items(pagingItems.itemCount) { index ->
                        UserItem(pagingItems[index])
                    }
                }

                // Handle the loading state
                when (val state = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is LoadState.Error -> {
                        ErrorButton(
                            message = state.error.message ?: "Unknown error",
                            onClick = { pagingItems.retry() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {} // No separate view needed for success/not loading
                }
            }
        }
        // [END android_architecture_paging_load_state_refresh]
    }

    object PrependAppendLoadStateScope {
        class UserViewModel : ViewModel() {
            lateinit var pager: Pager<Int, User>
        }

        // [START android_architecture_paging_load_state_prepend_append]
        @Composable
        fun UserList(viewModel: UserViewModel) {
            val pagingItems = viewModel.pager.flow.collectAsLazyPagingItems()

            LazyColumn {
                // 1. Header (Prepend state)
                // Useful if you support bidirectional paging or jumping to the middle
                item {
                    val prependState = pagingItems.loadState.prepend
                    if (prependState is LoadState.Loading) {
                        LoadingItem()
                    } else if (prependState is LoadState.Error) {
                        ErrorItem(
                            message = prependState.error.message ?: "Error",
                            onClick = { pagingItems.retry() }
                        )
                    }
                }

                // 2. Main Data
                items(pagingItems.itemCount) { index ->
                    UserItem(pagingItems[index])
                }

                // 3. Footer (Append state)
                // Shows when the user scrolls to the bottom and more data is loading
                item {
                    val appendState = pagingItems.loadState.append
                    if (appendState is LoadState.Loading) {
                        LoadingItem()
                    } else if (appendState is LoadState.Error) {
                        ErrorItem(
                            message = appendState.error.message ?: "Error",
                            onClick = { pagingItems.retry() }
                        )
                    }
                }
            }
        }

        @Composable
        fun LoadingItem() {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        @Composable
        fun ErrorItem(message: String, onClick: () -> Unit) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = message, color = Color.Red)
                Button(onClick = onClick) { Text("Retry") }
            }
        }
        // [END android_architecture_paging_load_state_prepend_append]
    }

    object SourceMediatorLoadStateScope {
        @Composable
        fun FullScreenLoading() {}

        @Composable
        fun UserList(pagingItems: LazyPagingItems<User>) {}

        @Composable
        fun TopOverlaySpinner() {}

        @Composable
        fun SourceAndMediatorExample(pagingItems: LazyPagingItems<User>) {
            // [START android_architecture_paging_load_state_source_mediator]
            val loadState = pagingItems.loadState

            val isSyncing = loadState.mediator?.refresh is LoadState.Loading

            val isLocalEmpty = loadState.source.refresh is LoadState.NotLoading &&
                pagingItems.itemSnapshotList.items.isEmpty()

            if (isSyncing && isLocalEmpty) {
                FullScreenLoading()
            } else {
                UserList(pagingItems)

                if (isSyncing) {
                    TopOverlaySpinner()
                }
            }
            // [END android_architecture_paging_load_state_source_mediator]
        }

        @Composable
        fun ScrollToTopExample(pagingItems: LazyPagingItems<User>) {
            // [START android_architecture_paging_load_state_scroll_top]
            val listState = rememberLazyListState()

            LaunchedEffect(pagingItems) {
                // 1. Convert the state to a Flow
                snapshotFlow { pagingItems.loadState.refresh }
                    // 2. Filter for the specific event (Refresh completed successfully)
                    .distinctUntilChanged()
                    .filter { it is LoadState.NotLoading }
                    .collect {
                        // 3. Trigger the side effect
                        listState.animateScrollToItem(0)
                    }
            }
            // [END android_architecture_paging_load_state_scroll_top]
        }
    }
}
