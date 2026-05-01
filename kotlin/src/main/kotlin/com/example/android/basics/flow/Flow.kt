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

package com.example.android.basics.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

// Placeholder types for imports
class ArticleHeadline
class UserData {
    fun isFavoriteTopic(topic: ArticleHeadline): Boolean = true
}
fun saveInCache(news: List<ArticleHeadline>) {}
fun lastCachedNews(): List<ArticleHeadline> = emptyList()
fun notifyError(exception: Throwable) {}

// [START android_kotlin_flow_create]
class NewsRemoteDataSource(
    private val newsApi: NewsApi,
    private val refreshIntervalMs: Long = 5000
) {
    val latestNews: Flow<List<ArticleHeadline>> = flow {
        while (true) {
            val latestNews = newsApi.fetchLatestNews()
            emit(latestNews) // Emits the result of the request to the flow
            delay(refreshIntervalMs) // Suspends the coroutine for some time
        }
    }
}

// Interface that provides a way to make network requests with suspend functions
interface NewsApi {
    suspend fun fetchLatestNews(): List<ArticleHeadline>
}
// [END android_kotlin_flow_create]

// [START android_kotlin_flow_modify]
class NewsRepository(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val userData: UserData
) {
    /**
     * Returns the favorite latest news applying transformations on the flow.
     * These operations are lazy and don't trigger the flow. They just transform
     * the current value emitted by the flow at that point in time.
     */
    val favoriteLatestNews: Flow<List<ArticleHeadline>> =
        newsRemoteDataSource.latestNews
            // Intermediate operation to filter the list of favorite topics
            .map { news -> news.filter { userData.isFavoriteTopic(it) } }
            // Intermediate operation to save the latest news in the cache
            .onEach { news -> saveInCache(news) }
}
// [END android_kotlin_flow_modify]

// [START android_kotlin_flow_collect]
class LatestNewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            // Trigger the flow and consume its elements using collect
            newsRepository.favoriteLatestNews.collect { favoriteNews ->
                // Update View with the latest favorite news
            }
        }
    }
}
// [END android_kotlin_flow_collect]

class LatestNewsViewModelCatch(
    private val newsRepository: NewsRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            // [START android_kotlin_flow_exceptions]
            newsRepository.favoriteLatestNews
                // Intermediate catch operator. If an exception is thrown,
                // catch and update the UI
                .catch { exception -> notifyError(exception) }
                .collect { favoriteNews ->
                    // Update View with the latest favorite news
                }
            // [END android_kotlin_flow_exceptions]
        }
    }
}

private object EmitCachedSnippet {
    // [START android_kotlin_flow_emit_cached]
    class NewsRepository(
        private val newsRemoteDataSource: NewsRemoteDataSource,
        private val userData: UserData
    ) {
        val favoriteLatestNews: Flow<List<ArticleHeadline>> =
            newsRemoteDataSource.latestNews
                .map { news -> news.filter { userData.isFavoriteTopic(it) } }
                .onEach { news -> saveInCache(news) }
                // If an error happens, emit the last cached values
                .catch { exception -> emit(lastCachedNews()) }
    }
    // [END android_kotlin_flow_emit_cached]
}

private object FlowOnSnippet {
    // [START android_kotlin_flow_flowon]
    class NewsRepository(
        private val newsRemoteDataSource: NewsRemoteDataSource,
        private val userData: UserData,
        private val defaultDispatcher: CoroutineDispatcher
    ) {
        val favoriteLatestNews: Flow<List<ArticleHeadline>> =
            newsRemoteDataSource.latestNews
                .map { news -> // Executes on the default dispatcher
                    news.filter { userData.isFavoriteTopic(it) }
                }
                .onEach { news -> // Executes on the default dispatcher
                    saveInCache(news)
                }
                // flowOn affects the upstream flow ↑
                .flowOn(defaultDispatcher)
                // the downstream flow ↓ is not affected
                .catch { exception -> // Executes in the consumer's context
                    emit(lastCachedNews())
                }
    }
    // [END android_kotlin_flow_flowon]
}

class NewsRemoteDataSourceWithFlowOn(
    private val newsApi: NewsApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    val refreshIntervalMs: Long = 5000

    // [START android_kotlin_flow_flowon_datasource]
    val latestNews: Flow<List<ArticleHeadline>> = flow {
        // Executes on the IO dispatcher
        // [START_EXCLUDE]
        while (true) {
            val latestNews = newsApi.fetchLatestNews()
            emit(latestNews)
            delay(refreshIntervalMs)
        }
        // [END_EXCLUDE]
    }
        .flowOn(ioDispatcher)
    // [END android_kotlin_flow_flowon_datasource]
}

class Example
// [START android_kotlin_flow_room]
@Dao
abstract class ExampleDao {
    @Query("SELECT * FROM Example")
    abstract fun getExamples(): Flow<List<Example>>
}
// [END android_kotlin_flow_room]

class UserEvents


class FirestoreUserEventsDataSource(
    private val firestore: FirebaseFirestore
) {
    // [START android_kotlin_flow_callback]
    // Method to get user events from the Firestore database
    fun getUserEvents(): Flow<UserEvents> = callbackFlow {

        // Reference to use in Firestore
        var eventsCollection: DocumentReference? = null
        try {
            eventsCollection = FirebaseFirestore.getInstance()
                .collection("collection")
                .document("app")
        } catch (e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }

        // Registers callback to firestore, which will be called on new events
        val subscription = eventsCollection?.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) {
                return@addSnapshotListener
            }
            // Sends events to the flow! Consumers will get the new events
            try {
                trySend(snapshot.getEvents())
            } catch (e: Throwable) {
                // Event couldn't be sent to the flow
            }
        }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from Firestore
        awaitClose { subscription?.remove() }
    }
    // [END android_kotlin_flow_callback]
}
