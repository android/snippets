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

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.startup.Initializer
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

private object OfflineFirstSnippet1 {
    // [START android_architecture_offline_first_models_network_local]
    /**
     * Network representation of [Author]
     */
    @Serializable
    data class NetworkAuthor(
        val id: String,
        val name: String,
        val imageUrl: String,
        val twitter: String,
        val mediumPage: String,
        val bio: String,
    )

    /**
     * Defines an author for either an [EpisodeEntity] or [NewsResourceEntity].
     * It has a many-to-many relationship with both entities
     */
    @Entity(tableName = "authors")
    data class AuthorEntity(
        @PrimaryKey
        val id: String,
        val name: String,
        @ColumnInfo(name = "image_url")
        val imageUrl: String,
        @ColumnInfo(defaultValue = "")
        val twitter: String,
        @ColumnInfo(name = "medium_page", defaultValue = "")
        val mediumPage: String,
        @ColumnInfo(defaultValue = "")
        val bio: String,
    )
    // [END android_architecture_offline_first_models_network_local]
}

private object OfflineFirstSnippet2 {
    // [START android_architecture_offline_first_model_external]
    /**
     * External data layer representation of a "Now in Android" Author
     */
    data class Author(
        val id: String,
        val name: String,
        val imageUrl: String,
        val twitter: String,
        val mediumPage: String,
        val bio: String,
    )
    // [END android_architecture_offline_first_model_external]
}

private object OfflineFirstSnippet3 {
    data class NetworkAuthor(
        val id: String,
        val name: String,
        val imageUrl: String,
        val twitter: String,
        val mediumPage: String,
        val bio: String,
    )

    data class AuthorEntity(
        val id: String,
        val name: String,
        val imageUrl: String,
        val twitter: String,
        val mediumPage: String,
        val bio: String,
    )

    data class Author(
        val id: String,
        val name: String,
        val imageUrl: String,
        val twitter: String,
        val mediumPage: String,
        val bio: String,
    )

    // [START android_architecture_offline_first_mappers]
    /**
     * Converts the network model to the local model for persisting
     * by the local data source
     */
    fun NetworkAuthor.asEntity() = AuthorEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        twitter = twitter,
        mediumPage = mediumPage,
        bio = bio,
    )

    /**
     * Converts the local model to the external model for use
     * by layers external to the data layer
     */
    fun AuthorEntity.asExternalModel() = Author(
        id = id,
        name = name,
        imageUrl = imageUrl,
        twitter = twitter,
        mediumPage = mediumPage,
        bio = bio,
    )
    // [END android_architecture_offline_first_mappers]
}

private object OfflineFirstSnippet4 {
    data class Topic(val id: String, val name: String)
    interface OfflineFirstTopicsRepository {
        fun getTopicsStream(): Flow<List<Topic>>
    }

    // [START android_architecture_offline_first_topics_viewmodel]
    class TopicsViewModel(
        offlineFirstTopicsRepository: OfflineFirstTopicsRepository
    ) : ViewModel() {

        val topics: StateFlow<List<Topic>> = offlineFirstTopicsRepository.getTopicsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }
    // [END android_architecture_offline_first_topics_viewmodel]
}

private object OfflineFirstSnippet5 {
    data class Author(val id: String = "", val name: String = "") {
        companion object {
            fun empty() = Author()
        }
    }

    interface AuthorsRepository {
        fun getAuthorStream(id: String): Flow<Author>
    }

    // [START android_architecture_offline_first_author_viewmodel_catch]
    class AuthorViewModel(
        authorsRepository: AuthorsRepository,
        /* [START_EXCLUDE silent] */
        authorId: String = ""
        /* [END_EXCLUDE] */
    ) : ViewModel() {
        private val authorId: String = /* [START_EXCLUDE] */ authorId /* [END_EXCLUDE] */

        // Observe author information
        private val authorStream: Flow<Author> =
            authorsRepository.getAuthorStream(
                id = authorId
            )
                .catch { emit(Author.empty()) }
    }
    // [END android_architecture_offline_first_author_viewmodel_catch]
}

private object OfflineFirstSnippet6 {
    data class Author(val id: String = "", val name: String = "")

    interface AuthorsRepository {
        fun getAuthorStream(id: String): Flow<Author>
    }

    // [START android_architecture_offline_first_author_lce]
    // Define the LCE UI state
    sealed interface AuthorUiState {
        data object Loading : AuthorUiState
        data class Success(val author: Author) : AuthorUiState
        data object Error : AuthorUiState
    }

    class AuthorViewModel(
        authorsRepository: AuthorsRepository,
        /* [START_EXCLUDE silent] */
        authorId: String = ""
        /* [END_EXCLUDE] */
    ) : ViewModel() {
        private val authorId: String = /* [START_EXCLUDE] */ authorId /* [END_EXCLUDE] */

        // Observe author information and map to LCE state
        val authorUiState: StateFlow<AuthorUiState> =
            authorsRepository.getAuthorStream(id = authorId)
                .map<Author, AuthorUiState> { author ->
                    AuthorUiState.Success(author)
                }
                .catch { emit(AuthorUiState.Error) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = AuthorUiState.Loading
                )
    }
    // [END android_architecture_offline_first_author_lce]
}

private object OfflineFirstSnippet7 {
    // [START android_architecture_offline_first_user_data_repository_write]
    interface UserDataRepository {
        /**
         * Updates the bookmarked status for a news resource
         */
        suspend fun updateNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean)
    }
    // [END android_architecture_offline_first_user_data_repository_write]
}

private object OfflineFirstSnippet8 {
    const val NETWORK_PAGE_SIZE = 20
    data class FeedItem(val id: String)

    typealias PagingSource<Value> = androidx.paging.PagingSource<Int, Value>

    @OptIn(ExperimentalPagingApi::class)
    class FeedRemoteMediator(/* ... */) : RemoteMediator<Int, FeedItem>() {
        override suspend fun load(
            loadType: androidx.paging.LoadType,
            state: PagingState<Int, FeedItem>
        ): MediatorResult = MediatorResult.Success(endOfPaginationReached = true)
    }

    // [START android_architecture_offline_first_paging_pull_sync]
    class FeedRepository(/* ... */) {

        fun feedPagingSource(): PagingSource<FeedItem> {
            /* [START_EXCLUDE silent] */
            return object : androidx.paging.PagingSource<Int, FeedItem>() {
                override fun getRefreshKey(state: PagingState<Int, FeedItem>): Int? = null
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItem> =
                    LoadResult.Page(emptyList(), null, null)
            }
            /* [END_EXCLUDE] */
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    class FeedViewModel(
        private val repository: FeedRepository
    ) : ViewModel() {
        /* [START_EXCLUDE silent] */
        private val feedRepository = repository
        /* [END_EXCLUDE] */
        private val pager = Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = FeedRemoteMediator(/* ... */),
            pagingSourceFactory = feedRepository::feedPagingSource
        )

        val feedPagingData = pager.flow
    }
    // [END android_architecture_offline_first_paging_pull_sync]
}

private object OfflineFirstSnippet9 {
    class UserData
    class NetworkDataSource {
        fun fetchUserData(): UserData = UserData()
    }
    class LocalDataSource {
        fun saveUserData(data: UserData) {}
    }

    // [START android_architecture_offline_first_push_sync]
    class UserDataRepository(
        /* [START_EXCLUDE silent] */
        private val networkDataSource: NetworkDataSource = NetworkDataSource(),
        private val localDataSource: LocalDataSource = LocalDataSource()
        /* [END_EXCLUDE] */
    ) {

        suspend fun synchronize() {
            val userData = networkDataSource.fetchUserData()
            localDataSource.saveUserData(userData)
        }
    }
    // [END android_architecture_offline_first_push_sync]
}

private object OfflineFirstSnippet10 {
    object Sync
    const val SyncWorkName = "SyncWork"

    // [START android_architecture_offline_first_sync_initializer]
    class SyncInitializer : Initializer<Sync> {
        override fun create(context: Context): Sync {
            WorkManager.getInstance(context).apply {
                // Queue sync on app startup and ensure only one
                // sync worker runs at any time
                enqueueUniqueWork(
                    SyncWorkName,
                    ExistingWorkPolicy.KEEP,
                    SyncWorker.startUpSyncWork()
                )
            }
            return Sync
        }
        /* [START_EXCLUDE silent] */
        override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
        /* [END_EXCLUDE] */
    }
    // [END android_architecture_offline_first_sync_initializer]
}

private object OfflineFirstSnippet11 {
    // [START android_architecture_offline_first_sync_work_request]
    /**
     Create a WorkRequest to call the SyncWorker using a DelegatingWorker.
     This allows for dependency injection into the SyncWorker in a different
     module than the app module without having to create a custom WorkManager
     configuration.
     */
    fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
        // Run sync as expedited work if the app is able to.
        // If not, it runs as regular work.
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setConstraints(SyncConstraints)
        // Delegate to the SyncWorker.
        .setInputData(SyncWorker::class.delegatedData())
        .build()

    val SyncConstraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    // [END android_architecture_offline_first_sync_work_request]
}

private object OfflineFirstSnippet12 {
    class TopicRepository {
        suspend fun sync(): Boolean = true
    }
    class AuthorsRepository {
        suspend fun sync(): Boolean = true
    }
    class NewsRepository {
        suspend fun sync(): Boolean = true
    }

    // [START android_architecture_offline_first_sync_worker]
    class SyncWorker(
        /* [START_EXCLUDE silent] */
        appContext: Context,
        workerParams: WorkerParameters,
        private val topicRepository: TopicRepository = TopicRepository(),
        private val authorsRepository: AuthorsRepository = AuthorsRepository(),
        private val newsRepository: NewsRepository = NewsRepository(),
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        /* [END_EXCLUDE] */
    ) : CoroutineWorker(appContext, workerParams), Synchronizer {

        override suspend fun doWork(): Result = withContext(ioDispatcher) {
            // First sync the repositories in parallel
            val syncedSuccessfully = awaitAll(
                async { topicRepository.sync() },
                async { authorsRepository.sync() },
                async { newsRepository.sync() },
            ).all { it }

            if (syncedSuccessfully) Result.success()
            else Result.retry()
        }
    }
    // [END android_architecture_offline_first_sync_worker]
}

private class DelegatingWorker(context: Context, params: WorkerParameters) : ListenableWorker(context, params) {
    override fun startWork() = throw UnsupportedOperationException()
}

private object SyncWorker {
    fun startUpSyncWork(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<DelegatingWorker>().build()
}

private fun KClass<*>.delegatedData(): Data = workDataOf()

private interface Synchronizer
