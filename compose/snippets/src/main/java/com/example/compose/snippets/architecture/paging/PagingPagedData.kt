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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow

private object PagingPagedDataSnippets {
    data class User(val id: String, val name: String = "")
    data class UserSearchResponse(val users: List<User>)
    class HttpException : Exception()

    class ExampleBackendService {
        fun addDatabaseOnChangedListener(listener: () -> Unit) {}
        fun searchUsers(query: String, pageNumber: Int): UserSearchResponse =
            UserSearchResponse(emptyList())
    }

    // [START android_architecture_paging_paged_data_source]
    class ExamplePagingSource(
        val backend: ExampleBackendService,
        val query: String
    ) : PagingSource<Int, User>() {
        init {
            // the data source is expected to be immutable
            // invalidate PagingSource if data source
            // has updated
            backend.addDatabaseOnChangedListener {
                invalidate()
            }
        }

        override suspend fun load(
            params: LoadParams<Int>
        ): LoadResult<Int, User> {
            try {
                // Start refresh at page 1 if undefined.
                val nextPageNumber = params.key ?: 1
                val response = backend.searchUsers(query, nextPageNumber)
                return LoadResult.Page(
                    data = response.users,
                    prevKey = null, // Only paging forward.
                    nextKey = nextPageNumber + 1
                )
            } catch (e: Exception) {
                // Handle errors in this block and return LoadResult.Error for
                // expected errors (such as a network failure).
                // [START_EXCLUDE]
                return LoadResult.Error(e)
                // [END_EXCLUDE]
            }
        }

        override fun getRefreshKey(state: PagingState<Int, User>): Int? {
            // Try to find the page key of the closest page to anchorPosition from
            // either the prevKey or the nextKey; you need to handle nullability
            // here.
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey are null -> anchorPage is the
            //    initial page, so return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
    // [END android_architecture_paging_paged_data_source]

    abstract class ErrorHandlingPagingSource(
        val backend: ExampleBackendService,
        val query: String
    ) : PagingSource<Int, User>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
            try {
                val nextPageNumber = params.key ?: 1
                val response = backend.searchUsers(query, nextPageNumber)
                return LoadResult.Page(
                    data = response.users,
                    prevKey = null,
                    nextKey = nextPageNumber + 1
                )
            }
            // [START android_architecture_paging_paged_data_error]
            catch (e: IOException) {
                // IOException for network failures.
                return LoadResult.Error(e)
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                return LoadResult.Error(e)
            }
            // [END android_architecture_paging_paged_data_error]
        }
    }

    // [START android_architecture_paging_paged_data_pager]
    class UserViewModel(
        private val backend: ExampleBackendService,
        private val query: String
    ) : ViewModel() {

        val userPagingFlow: Flow<PagingData<User>> = Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as pageSize and enabling or disabling placeholders.
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ExamplePagingSource(backend, query)
            }
        )
            .flow
            .cachedIn(viewModelScope)
    }
    // [END android_architecture_paging_paged_data_pager]

    @Composable
    fun UserRow(user: User) {
        Text(text = user.name)
    }

    @Composable
    fun UserPlaceholder() {
        Text(text = "Loading...")
    }

    // [START android_architecture_paging_paged_data_screen]
    @Composable
    fun UserScreen(viewModel: UserViewModel = viewModel()) {
        val userFlow = viewModel.userPagingFlow
        UserList(flow = userFlow)
    }
    // [END android_architecture_paging_paged_data_screen]

    // [START android_architecture_paging_paged_data_list]
    @Composable
    fun UserList(flow: Flow<PagingData<User>>) {
        val lazyPagingItems = flow.collectAsLazyPagingItems()
        LazyColumn {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                val user = lazyPagingItems[index]
                if (user != null) {
                    UserRow(user)
                } else {
                    UserPlaceholder()
                }
            }
        }
    }
    // [END android_architecture_paging_paged_data_list]
}
