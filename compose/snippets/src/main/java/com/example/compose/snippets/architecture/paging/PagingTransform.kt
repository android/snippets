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
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private object PagingTransformSnippets {
    data class User(
        val id: String,
        val label: String = "",
        val hiddenFromUi: Boolean = false
    )

    object BasicMapScope {
        data class UiModel(val user: User)
        lateinit var pager: Pager<Int, User>

        fun outerMapExample() {
            // [START android_architecture_paging_transform_outer_map]
                pager.flow // Type is Flow<PagingData<User>>.
                // Map the outer stream so that the transformations are applied to
                // each new generation of PagingData.
                .map { pagingData ->
                    // Transformations in this block are applied to the items
                    // in the paged data.
                }
            // [END android_architecture_paging_transform_outer_map]
        }

        fun mapItemsExample() {
            // [START android_architecture_paging_transform_map]
            pager.flow // Type is Flow<PagingData<User>>.
                .map { pagingData ->
                    pagingData.map { user -> UiModel(user) }
                }
            // [END android_architecture_paging_transform_map]
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    object QueryFlowScope {
        object userDatabase {
            fun searchBy(query: String): Flow<User> = flowOf()
        }

        // [START android_architecture_paging_transform_query_flow]
        private val queryFlow = MutableStateFlow("")

        fun onQueryChanged(query: String) {
            queryFlow.value = query
        }
        // [END android_architecture_paging_transform_query_flow]

        // [START android_architecture_paging_transform_flat_map_latest]
        val querySearchResults: Flow<User> = queryFlow.flatMapLatest { query ->
            // The database query returns a Flow which is output through
            // querySearchResults
            userDatabase.searchBy(query)
        }
        // [END android_architecture_paging_transform_flat_map_latest]
    }

    object SeparatorsScope : ViewModel() {
        lateinit var pager: Pager<Int, User>
        fun shouldSeparate(before: UiModel.UserModel, after: UiModel.UserModel): Boolean = false

        fun filterExample() {
            // [START android_architecture_paging_transform_filter]
            pager.flow // Type is Flow<PagingData<User>>.
                .map { pagingData ->
                    pagingData.filter { user -> !user.hiddenFromUi }
                }
            // [END android_architecture_paging_transform_filter]
        }

        // [START android_architecture_paging_transform_ui_model]
        sealed class UiModel {
            class UserModel(val id: String, val label: String) : UiModel() {
                constructor(user: User) : this(user.id, user.label)
            }

            class SeparatorModel(val description: String) : UiModel()
        }
        // [END android_architecture_paging_transform_ui_model]

        fun insertSeparatorsExample() {
            // [START android_architecture_paging_transform_insert_separators]
            pager.flow.map { pagingData: PagingData<User> ->
                // Map outer stream, so you can perform transformations on
                // each paging generation.
                pagingData
                    .map { user ->
                        // Convert items in stream to UiModel.UserModel.
                        UiModel.UserModel(user)
                    }
                    .insertSeparators<UiModel.UserModel, UiModel> { before, after ->
                        when {
                            before == null -> UiModel.SeparatorModel("HEADER")
                            after == null -> UiModel.SeparatorModel("FOOTER")
                            shouldSeparate(before, after) -> UiModel.SeparatorModel(
                                "BETWEEN ITEMS $before AND $after"
                            )
                            // Return null to avoid adding a separator between two items.
                            else -> null
                        }
                    }
            }
            // [END android_architecture_paging_transform_insert_separators]
        }

        fun cachedInExample() {
            // [START android_architecture_paging_transform_cached_in]
            pager.flow // Type is Flow<PagingData<User>>.
                .map { pagingData ->
                    pagingData.filter { user -> !user.hiddenFromUi }
                        .map { user -> UiModel.UserModel(user) }
                }
                .cachedIn(viewModelScope)
            // [END android_architecture_paging_transform_cached_in]
        }
    }

    object UserListUiScope {
        sealed class UiModel {
            class UserModel(val user: User) : UiModel()
            class SeparatorModel(val description: String) : UiModel()
        }

        @Composable
        fun UserItemComposable(user: User) {
            Text(text = user.label)
        }

        @Composable
        fun SeparatorComposable(description: String) {
            Text(text = description)
        }

        @Composable
        fun PlaceholderComposable() {
            Text(text = "Loading...")
        }

        // [START android_architecture_paging_transform_user_list]
        @Composable fun UserList(pagingItems: LazyPagingItems<UiModel>) {
            LazyColumn {
                items(
                    count = pagingItems.itemCount,
                    key = { index ->
                        val item = pagingItems.peek(index)
                        when (item) {
                            is UiModel.UserModel -> item.user.id
                            is UiModel.SeparatorModel -> item.description
                            else -> index
                        }
                    }
                ) { index ->
                    when (val item = pagingItems[index]) {
                        is UiModel.UserModel -> UserItemComposable(item.user)
                        is UiModel.SeparatorModel -> SeparatorComposable(item.description)
                        null -> PlaceholderComposable()
                    }
                }
            }
        }
        // [END android_architecture_paging_transform_user_list]
    }
}
