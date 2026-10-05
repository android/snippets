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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

private object PagingNetworkDbSnippets {
    class HttpException : Exception()
    data class UserSearchResponse(val users: List<User>, val nextKey: String? = null)

    class ExampleBackendService {
        suspend fun searchUsers(query: String, after: String? = null): UserSearchResponse =
            UserSearchResponse(emptyList())
    }

    // [START android_architecture_paging_network_db_user_entity]
    @Entity(tableName = "users")
    data class User(val id: String, val label: String)
    // [END android_architecture_paging_network_db_user_entity]

    // [START android_architecture_paging_network_db_user_dao]
    @Dao
    interface UserDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(users: List<User>)

        @Query("SELECT * FROM users WHERE label LIKE :query")
        fun pagingSource(query: String): PagingSource<Int, User>

        @Query("DELETE FROM users")
        suspend fun clearAll()
        // [START_EXCLUDE silent]
        suspend fun deleteByQuery(query: String)
        // [END_EXCLUDE]
    }
    // [END android_architecture_paging_network_db_user_dao]

    // [START android_architecture_paging_network_db_remote_key_entity]
    @Entity(tableName = "remote_keys")
    data class RemoteKey(val label: String, val nextKey: String?)
    // [END android_architecture_paging_network_db_remote_key_entity]

    // [START android_architecture_paging_network_db_remote_key_dao]
    @Dao
    interface RemoteKeyDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertOrReplace(remoteKey: RemoteKey)

        @Query("SELECT * FROM remote_keys WHERE label = :query")
        suspend fun remoteKeyByQuery(query: String): RemoteKey

        @Query("DELETE FROM remote_keys WHERE label = :query")
        suspend fun deleteByQuery(query: String)
    }
    // [END android_architecture_paging_network_db_remote_key_dao]

    abstract class RoomDb {
        abstract fun userDao(): UserDao
        abstract fun remoteKeyDao(): RemoteKeyDao
        fun lastUpdated(): Long = 0L
        suspend fun <R> withTransaction(block: suspend () -> R): R = block()
    }

    object SkeletonScope {
        // [START android_architecture_paging_network_db_mediator_skeleton]
        @OptIn(ExperimentalPagingApi::class)
        class ExampleRemoteMediator(
            private val query: String,
            private val database: RoomDb,
            private val networkService: ExampleBackendService
        ) : RemoteMediator<Int, User>() {
            val userDao = database.userDao()

            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, User>
            ): MediatorResult {
                // [START_EXCLUDE]
                return MediatorResult.Success(endOfPaginationReached = true)
                // [END_EXCLUDE]
            }
        }
        // [END android_architecture_paging_network_db_mediator_skeleton]
    }

    object LoadAndInitScope {
        @OptIn(ExperimentalPagingApi::class)
        class ExampleRemoteMediator(
            private val query: String,
            private val database: RoomDb,
            private val networkService: ExampleBackendService
        ) : RemoteMediator<Int, User>() {
            val userDao = database.userDao()
            private val db: RoomDb get() = database

            // [START android_architecture_paging_network_db_mediator_load]
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, User>
            ): MediatorResult {
                return try {
                    // The network load method takes an optional after=<user.id>
                    // parameter. For every page after the first, pass the last user
                    // ID to let it continue from where it left off. For REFRESH,
                    // pass null to load the first page.
                    val loadKey = when (loadType) {
                        LoadType.REFRESH -> null
                        // In this example, you never need to prepend, since REFRESH
                        // will always load the first page in the list. Immediately
                        // return, reporting end of pagination.
                        LoadType.PREPEND ->
                            return MediatorResult.Success(endOfPaginationReached = true)
                        LoadType.APPEND -> {
                            val lastItem = state.lastItemOrNull()

                            // You must explicitly check if the last item is null when
                            // appending, since passing null to networkService is only
                            // valid for initial load. If lastItem is null it means no
                            // items were loaded after the initial REFRESH and there are
                            // no more items to load.
                            if (lastItem == null) {
                                return MediatorResult.Success(
                                    endOfPaginationReached = true
                                )
                            }

                            lastItem.id
                        }
                    }

                    // Suspending network load via Retrofit. This doesn't need to be
                    // wrapped in a withContext(Dispatcher.IO) { ... } block since
                    // Retrofit's Coroutine CallAdapter dispatches on a worker
                    // thread.
                    val response = networkService.searchUsers(
                        query = query, after = loadKey
                    )

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            userDao.deleteByQuery(query)
                        }

                        // Insert new users into database, which invalidates the
                        // current PagingData, allowing Paging to present the updates
                        // in the DB.
                        userDao.insertAll(response.users)
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = response.nextKey == null
                    )
                } catch (e: IOException) {
                    MediatorResult.Error(e)
                } catch (e: HttpException) {
                    MediatorResult.Error(e)
                }
            }
            // [END android_architecture_paging_network_db_mediator_load]

            // [START android_architecture_paging_network_db_mediator_init]
            override suspend fun initialize(): InitializeAction {
                val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
                return if (System.currentTimeMillis() - db.lastUpdated() <= cacheTimeout) {
                    // Cached data is up-to-date, so there is no need to re-fetch
                    // from the network.
                    InitializeAction.SKIP_INITIAL_REFRESH
                } else {
                    // Need to refresh cached data from network; returning
                    // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
                    // APPEND and PREPEND from running until REFRESH succeeds.
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }
            // [END android_architecture_paging_network_db_mediator_init]
        }

        @OptIn(ExperimentalPagingApi::class)
        fun createPager(
            query: String,
            database: RoomDb,
            networkService: ExampleBackendService
        ) {
            // [START android_architecture_paging_network_db_pager]
            val userDao = database.userDao()
            val pager = Pager(
                config = PagingConfig(pageSize = 50),
                remoteMediator = ExampleRemoteMediator(query, database, networkService)
            ) {
                userDao.pagingSource(query)
            }
            // [END android_architecture_paging_network_db_pager]
        }
    }

    object ItemKeyMediatorScope {
        // [START android_architecture_paging_network_db_mediator_item_key]
        @OptIn(ExperimentalPagingApi::class)
        class ExampleRemoteMediator(
            private val query: String,
            private val database: RoomDb,
            private val networkService: ExampleBackendService
        ) : RemoteMediator<Int, User>() {
            val userDao = database.userDao()

            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, User>
            ): MediatorResult {
                return try {
                    // The network load method takes an optional String
                    // parameter. For every page after the first, pass the String
                    // token returned from the previous page to let it continue
                    // from where it left off. For REFRESH, pass null to load the
                    // first page.
                    val loadKey = when (loadType) {
                        LoadType.REFRESH -> null
                        // In this example, you never need to prepend, since REFRESH
                        // will always load the first page in the list. Immediately
                        // return, reporting end of pagination.
                        LoadType.PREPEND -> return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                        // Get the last User object id for the next RemoteKey.
                        LoadType.APPEND -> {
                            val lastItem = state.lastItemOrNull()

                            // You must explicitly check if the last item is null when
                            // appending, since passing null to networkService is only
                            // valid for initial load. If lastItem is null it means no
                            // items were loaded after the initial REFRESH and there are
                            // no more items to load.
                            if (lastItem == null) {
                                return MediatorResult.Success(
                                    endOfPaginationReached = true
                                )
                            }

                            lastItem.id
                        }
                    }

                    // Suspending network load via Retrofit. This doesn't need to
                    // be wrapped in a withContext(Dispatcher.IO) { ... } block
                    // since Retrofit's Coroutine CallAdapter dispatches on a
                    // worker thread.
                    val response = networkService.searchUsers(query, loadKey)

                    // Store loaded data, and next key in transaction, so that
                    // they're always consistent.
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            userDao.deleteByQuery(query)
                        }

                        // Insert new users into database, which invalidates the
                        // current PagingData, allowing Paging to present the updates
                        // in the DB.
                        userDao.insertAll(response.users)
                    }

                    // End of pagination has been reached if no users are returned from the
                    // service
                    MediatorResult.Success(
                        endOfPaginationReached = response.users.isEmpty()
                    )
                } catch (e: IOException) {
                    MediatorResult.Error(e)
                } catch (e: HttpException) {
                    MediatorResult.Error(e)
                }
            }
        }
        // [END android_architecture_paging_network_db_mediator_item_key]
    }

    object RemoteKeyMediatorScope {
        // [START android_architecture_paging_network_db_mediator_remote_key]
        @OptIn(ExperimentalPagingApi::class)
        class ExampleRemoteMediator(
            private val query: String,
            private val database: RoomDb,
            private val networkService: ExampleBackendService
        ) : RemoteMediator<Int, User>() {
            val userDao = database.userDao()
            val remoteKeyDao = database.remoteKeyDao()

            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, User>
            ): MediatorResult {
                return try {
                    // The network load method takes an optional String
                    // parameter. For every page after the first, pass the String
                    // token returned from the previous page to let it continue
                    // from where it left off. For REFRESH, pass null to load the
                    // first page.
                    val loadKey = when (loadType) {
                        LoadType.REFRESH -> null
                        // In this example, you never need to prepend, since REFRESH
                        // will always load the first page in the list. Immediately
                        // return, reporting end of pagination.
                        LoadType.PREPEND -> return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                        // Query remoteKeyDao for the next RemoteKey.
                        LoadType.APPEND -> {
                            val remoteKey = database.withTransaction {
                                remoteKeyDao.remoteKeyByQuery(query)
                            }

                            // You must explicitly check if the page key is null when
                            // appending, since null is only valid for initial load.
                            // If you receive null for APPEND, that means you have
                            // reached the end of pagination and there are no more
                            // items to load.
                            if (remoteKey.nextKey == null) {
                                return MediatorResult.Success(
                                    endOfPaginationReached = true
                                )
                            }

                            remoteKey.nextKey
                        }
                    }

                    // Suspending network load via Retrofit. This doesn't need to
                    // be wrapped in a withContext(Dispatcher.IO) { ... } block
                    // since Retrofit's Coroutine CallAdapter dispatches on a
                    // worker thread.
                    val response = networkService.searchUsers(query, loadKey)

                    // Store loaded data, and next key in transaction, so that
                    // they're always consistent.
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeyDao.deleteByQuery(query)
                            userDao.deleteByQuery(query)
                        }

                        // Update RemoteKey for this query.
                        remoteKeyDao.insertOrReplace(
                            RemoteKey(query, response.nextKey)
                        )

                        // Insert new users into database, which invalidates the
                        // current PagingData, allowing Paging to present the updates
                        // in the DB.
                        userDao.insertAll(response.users)
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = response.nextKey == null
                    )
                } catch (e: IOException) {
                    MediatorResult.Error(e)
                } catch (e: HttpException) {
                    MediatorResult.Error(e)
                }
            }
        }
        // [END android_architecture_paging_network_db_mediator_remote_key]
    }
}
