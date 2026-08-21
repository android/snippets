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
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private object DataLayerSnippet1 {
    interface ExampleRemoteDataSource
    interface ExampleLocalDataSource

    // [START android_architecture_data_layer_repository_constructor]
    class ExampleRepository(
        // network
        private val exampleRemoteDataSource: ExampleRemoteDataSource,
        // database
        private val exampleLocalDataSource: ExampleLocalDataSource
    ) { /* ... */ }
    // [END android_architecture_data_layer_repository_constructor]
}

private object DataLayerSnippet2 {
    interface ExampleRemoteDataSource
    interface ExampleLocalDataSource
    class Example

    // [START android_architecture_data_layer_expose_apis]
    class ExampleRepository(
        // network
        private val exampleRemoteDataSource: ExampleRemoteDataSource,
        // database
        private val exampleLocalDataSource: ExampleLocalDataSource
    ) {

        val data: Flow<Example> = /* [START_EXCLUDE] */ flowOf(Example()) /* [END_EXCLUDE] */

        suspend fun modifyData(example: Example) { /* [START_EXCLUDE silent] */ /* [END_EXCLUDE] */ }
    }
    // [END android_architecture_data_layer_expose_apis]
}

private object DataLayerSnippet3 {
    class CommentApiModel

    // [START android_architecture_data_layer_network_model]
    data class ArticleApiModel(
        val id: Long,
        val title: String,
        val content: String,
        val publicationDate: Date,
        val modifications: Array<ArticleApiModel>,
        val comments: Array<CommentApiModel>,
        val lastModificationDate: Date,
        val authorId: Long,
        val authorName: String,
        val authorDateOfBirth: Date,
        val readTimeMin: Int
    )
    // [END android_architecture_data_layer_network_model]
}

private object DataLayerSnippet4 {
    // [START android_architecture_data_layer_business_model]
    data class Article(
        val id: Long,
        val title: String,
        val content: String,
        val publicationDate: Date,
        val authorName: String,
        val readTimeMin: Int
    )
    // [END android_architecture_data_layer_business_model]
}

private object DataLayerSnippet5 {
    data class ArticleHeadline(val title: String)

    // [START android_architecture_data_layer_news_remote_data_source]
    class NewsRemoteDataSource(
        private val newsApi: NewsApi,
        private val ioDispatcher: CoroutineDispatcher
    ) {
        /**
         * Fetches the latest news from the network and returns the result.
         * This executes on an IO-optimized thread pool, the function is main-safe.
         */
        suspend fun fetchLatestNews(): List<ArticleHeadline> =
            // Move the execution to an IO-optimized thread since the ApiService
            // doesn't support coroutines and makes synchronous requests.
            withContext(ioDispatcher) {
                newsApi.fetchLatestNews()
            }
    }

    // Makes news-related network synchronous requests.
    interface NewsApi {
        fun fetchLatestNews(): List<ArticleHeadline>
    }
    // [END android_architecture_data_layer_news_remote_data_source]
}

private object DataLayerSnippet6 {
    data class ArticleHeadline(val title: String)
    class NewsRemoteDataSource {
        fun fetchLatestNews(): List<ArticleHeadline> = emptyList()
    }

    // [START android_architecture_data_layer_news_repository_fetch]
    // NewsRepository is consumed from other layers of the hierarchy.
    class NewsRepository(
        private val newsRemoteDataSource: NewsRemoteDataSource
    ) {
        suspend fun fetchLatestNews(): List<ArticleHeadline> =
            newsRemoteDataSource.fetchLatestNews()
    }
    // [END android_architecture_data_layer_news_repository_fetch]
}

private object DataLayerSnippet7 {
    data class ArticleHeadline(val title: String)
    class NewsRemoteDataSource {
        fun fetchLatestNews(): List<ArticleHeadline> = emptyList()
    }

    // [START android_architecture_data_layer_in_memory_cache]
    class NewsRepository(
        private val newsRemoteDataSource: NewsRemoteDataSource
    ) {
        // Mutex to make writes to cached values thread-safe.
        private val latestNewsMutex = Mutex()

        // Cache of the latest news got from the network.
        private var latestNews: List<ArticleHeadline> = emptyList()

        suspend fun getLatestNews(refresh: Boolean = false): List<ArticleHeadline> {
            if (refresh || latestNews.isEmpty()) {
                val networkResult = newsRemoteDataSource.fetchLatestNews()
                // Thread-safe write to latestNews
                latestNewsMutex.withLock {
                    this.latestNews = networkResult
                }
            }

            return latestNewsMutex.withLock { this.latestNews }
        }
    }
    // [END android_architecture_data_layer_in_memory_cache]
}

private object DataLayerSnippet8 {
    class NewsRemoteDataSource

    // [START android_architecture_data_layer_external_scope_constructor]
    class NewsRepository(
        /* [START_EXCLUDE] */
        newsRemoteDataSource: NewsRemoteDataSource,
        /* [END_EXCLUDE] */
        // This could be CoroutineScope(SupervisorJob() + Dispatchers.Default).
        private val externalScope: CoroutineScope
    ) { /* ... */ }
    // [END android_architecture_data_layer_external_scope_constructor]
}

private object DataLayerSnippet9 {
    data class ArticleHeadline(val title: String)
    class NewsRemoteDataSource {
        fun fetchLatestNews(): List<ArticleHeadline> = emptyList()
    }

    // [START android_architecture_data_layer_external_scope_async]
    class NewsRepository(
        private val newsRemoteDataSource: NewsRemoteDataSource,
        private val externalScope: CoroutineScope
    ) {
        /* ... */
        /* [START_EXCLUDE silent] */
        private val latestNewsMutex = Mutex()
        private var latestNews: List<ArticleHeadline> = emptyList()
        /* [END_EXCLUDE] */

        suspend fun getLatestNews(refresh: Boolean = false): List<ArticleHeadline> {
            return if (refresh) {
                externalScope.async {
                    newsRemoteDataSource.fetchLatestNews().also { networkResult ->
                        // Thread-safe write to latestNews.
                        latestNewsMutex.withLock {
                            latestNews = networkResult
                        }
                    }
                }.await()
            } else {
                return latestNewsMutex.withLock { this.latestNews }
            }
        }
    }
    // [END android_architecture_data_layer_external_scope_async]
}

private object DataLayerSnippet10 {
    class NewsRepository {
        fun refreshLatestNews() {}
    }

    // [START android_architecture_data_layer_refresh_latest_news_worker]
    class RefreshLatestNewsWorker(
        private val newsRepository: NewsRepository,
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result = try {
            newsRepository.refreshLatestNews()
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
    // [END android_architecture_data_layer_refresh_latest_news_worker]
}

private object DataLayerSnippet11 {
    class RefreshLatestNewsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result = Result.success()
    }

    // [START android_architecture_data_layer_news_tasks_data_source]
    private const val REFRESH_RATE_HOURS = 4L
    private const val FETCH_LATEST_NEWS_TASK = "FetchLatestNewsTask"
    private const val TAG_FETCH_LATEST_NEWS = "FetchLatestNewsTaskTag"

    class NewsTasksDataSource(
        private val workManager: WorkManager
    ) {
        fun fetchNewsPeriodically() {
            val fetchNewsRequest = PeriodicWorkRequestBuilder<RefreshLatestNewsWorker>(
                REFRESH_RATE_HOURS, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.TEMPORARILY_UNMETERED)
                    .setRequiresCharging(true)
                    .build()
            )
                .addTag(TAG_FETCH_LATEST_NEWS)

            workManager.enqueueUniquePeriodicWork(
                FETCH_LATEST_NEWS_TASK,
                ExistingPeriodicWorkPolicy.KEEP,
                fetchNewsRequest.build()
            )
        }

        fun cancelFetchingNewsPeriodically() {
            workManager.cancelAllWorkByTag(TAG_FETCH_LATEST_NEWS)
        }
    }
    // [END android_architecture_data_layer_news_tasks_data_source]
}
