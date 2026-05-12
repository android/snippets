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

package com.example.android.coroutines.bestpractices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Placeholder types
class ArticleHeadline
sealed class LatestNewsUiState {
    data class Success(val news: Result<List<ArticleWithAuthor>>) : LatestNewsUiState()
    object Loading : LatestNewsUiState()
}
class Article(val author: String = "")
class ArticleWithAuthor(article: Article, author: String)
class AuthorsRepository {
    suspend fun getAuthor(id: String): String = "Author"
    suspend fun getAllAuthors(): List<String> = emptyList()
}
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
}
fun <T> mutableEmptyList(): MutableList<T> = mutableListOf()

private object GoodExample {
    // [START android_kotlin_coroutines_best_practices_inject]
    // DO inject Dispatchers
    class NewsRepository(
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {
        suspend fun loadNews() = withContext(defaultDispatcher) { /* ... */ }
    }
    // [START_EXCLUDE silent]
}
private object BadExample {
    // [END_EXCLUDE]

    // DO NOT hardcode Dispatchers
    class NewsRepository {
        // DO NOT use Dispatchers.Default directly, inject it instead
        suspend fun loadNews() = withContext(Dispatchers.Default) { /* ... */ }
    }
    // [END android_kotlin_coroutines_best_practices_inject]
}

// [START android_kotlin_coroutines_best_practices_mainsafe]
class NewsRepository(private val ioDispatcher: CoroutineDispatcher) {

    // As this operation is manually retrieving the news from the server
    // using a blocking HttpURLConnection, it needs to move the execution
    // to an IO dispatcher to make it main-safe
    suspend fun fetchLatestNews(): List<Article> {
        withContext(ioDispatcher) { /* ... implementation ... */ }
        // [START_EXCLUDE silent]
        return emptyList()
        // [END_EXCLUDE]
    }
}

// This use case fetches the latest news and the associated author.
class GetLatestNewsWithAuthorsUseCase(
    private val newsRepository: NewsRepository,
    private val authorsRepository: AuthorsRepository
) {
    // This method doesn't need to worry about moving the execution of the
    // coroutine to a different thread as newsRepository is main-safe.
    // The work done in the coroutine is lightweight as it only creates
    // a list and add elements to it
    suspend operator fun invoke(): Result<List<ArticleWithAuthor>> {
        val news = newsRepository.fetchLatestNews()

        val response: MutableList<ArticleWithAuthor> = mutableEmptyList()
        for (article in news) {
            val author = authorsRepository.getAuthor(article.author)
            response.add(ArticleWithAuthor(article, author))
        }
        return Result.Success(response)
    }
}
// [END android_kotlin_coroutines_best_practices_mainsafe]

private object VmSnippetGood {
    // [START android_kotlin_coroutines_best_practices_vm_good]
    // DO create coroutines in the ViewModel
    class LatestNewsViewModel(
        private val getLatestNewsWithAuthors: GetLatestNewsWithAuthorsUseCase
    ) : ViewModel() {

        private val _uiState = MutableStateFlow<LatestNewsUiState>(LatestNewsUiState.Loading)
        val uiState: StateFlow<LatestNewsUiState> = _uiState

        fun loadNews() {
            viewModelScope.launch {
                val latestNewsWithAuthors = getLatestNewsWithAuthors()
                _uiState.value = LatestNewsUiState.Success(latestNewsWithAuthors)
            }
        }
    }
    // [END android_kotlin_coroutines_best_practices_vm_good]
}

private object VmSnippetBad {
    // [START android_kotlin_coroutines_best_practices_vm_bad]
    // Prefer observable state rather than suspend functions from the ViewModel
    class LatestNewsViewModel(
        private val getLatestNewsWithAuthors: GetLatestNewsWithAuthorsUseCase
    ) : ViewModel() {
        // DO NOT do this. News would probably need to be refreshed as well.
        // Instead of exposing a single value with a suspend function, news should
        // be exposed using a stream of data as in the code snippet above.
        suspend fun loadNews() = getLatestNewsWithAuthors()
    }
    // [END android_kotlin_coroutines_best_practices_vm_bad]
}

private object ImmutableSnippetGood {
    // [START android_kotlin_coroutines_best_practices_immutable_good]
    // DO expose immutable types
    class LatestNewsViewModel : ViewModel() {

        private val _uiState = MutableStateFlow(LatestNewsUiState.Loading)
        val uiState: StateFlow<LatestNewsUiState> = _uiState

        /* ... */
    }
    // [END android_kotlin_coroutines_best_practices_immutable_good]
}

private object ImmutableSnippetBad {
    // [START android_kotlin_coroutines_best_practices_immutable_bad]
    class LatestNewsViewModel : ViewModel() {

        // DO NOT expose mutable types
        val uiState = MutableStateFlow(LatestNewsUiState.Loading)

        /* ... */
    }
    // [END android_kotlin_coroutines_best_practices_immutable_bad]
}

class Example
// [START android_kotlin_coroutines_best_practices_data_layer]
// Classes in the data and business layer expose
// either suspend functions or Flows
class ExampleRepository {
    suspend fun makeNetworkRequest() { /* ... */ }

    fun getExamples(): Flow<Example> {
        /* ... */
        // [START_EXCLUDE silent]
        return flow { emit(Example()) }
        // [END_EXCLUDE]
    }
}
// [END android_kotlin_coroutines_best_practices_data_layer]

class BooksRepository {
    suspend fun getAllBooks(): List<String> = emptyList()
}
class BookAndAuthors(books: List<String>, authors: List<String>)

// [START android_kotlin_coroutines_best_practices_parallel]
class GetAllBooksAndAuthorsUseCase(
    private val booksRepository: BooksRepository,
    private val authorsRepository: AuthorsRepository,
) {
    suspend fun getBookAndAuthors(): BookAndAuthors {
        // In parallel, fetch books and authors and return when both requests
        // complete and the data is ready
        return coroutineScope {
            val books = async { booksRepository.getAllBooks() }
            val authors = async { authorsRepository.getAllAuthors() }
            BookAndAuthors(books.await(), authors.await())
        }
    }
}
// [END android_kotlin_coroutines_best_practices_parallel]

open class ArticlesDataSource {
    fun bookmarkArticle(article: Article) {}
    fun isBookmarked(article: Article): Boolean = true
}

private object ExternalSnippet {
    // [START android_kotlin_coroutines_best_practices_external]
    class ArticlesRepository(
        private val articlesDataSource: ArticlesDataSource,
        private val externalScope: CoroutineScope,
    ) {
        // As we want to complete bookmarking the article even if the user moves
        // away from the screen, the work is done creating a new coroutine
        // from an external scope
        suspend fun bookmarkArticle(article: Article) {
            externalScope.launch { articlesDataSource.bookmarkArticle(article) }
                .join() // Wait for the coroutine to complete
        }
    }
    // [END android_kotlin_coroutines_best_practices_external]
}

// [START android_kotlin_coroutines_best_practices_global_good]
// DO inject an external scope instead of using GlobalScope.
// GlobalScope can be used indirectly. Here as a default parameter makes sense.
class ArticlesRepository(
    private val articlesDataSource: ArticlesDataSource,
    private val externalScope: CoroutineScope = GlobalScope,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    // As we want to complete bookmarking the article even if the user moves
    // away from the screen, the work is done creating a new coroutine
    // from an external scope
    suspend fun bookmarkArticle(article: Article) {
        externalScope.launch(defaultDispatcher) {
            articlesDataSource.bookmarkArticle(article)
        }
            .join() // Wait for the coroutine to complete
    }
}
// [END android_kotlin_coroutines_best_practices_global_good]

private object GlobalSnippetBad {
    // [START android_kotlin_coroutines_best_practices_global_bad]
    // DO NOT use GlobalScope directly
    class ArticlesRepository(
        private val articlesDataSource: ArticlesDataSource,
    ) {
        // As we want to complete bookmarking the article even if the user moves away
        // from the screen, the work is done creating a new coroutine with GlobalScope
        suspend fun bookmarkArticle(article: Article) {
            GlobalScope.launch {
                articlesDataSource.bookmarkArticle(article)
            }
                .join() // Wait for the coroutine to complete
        }
    }
    // [END android_kotlin_coroutines_best_practices_global_bad]
}

class File
fun readFile(f: File) {}

fun testCancellable(someScope: CoroutineScope, files: List<File>) {
    // [START android_kotlin_coroutines_best_practices_cancellable]
    someScope.launch {
        for (file in files) {
            ensureActive() // Check for cancellation
            readFile(file)
        }
    }
    // [END android_kotlin_coroutines_best_practices_cancellable]
}

class LoginRepository {
    fun login(u: String, t: String) {}
}

// [START android_kotlin_coroutines_best_practices_login_vm]
class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun login(username: String, token: String) {
        viewModelScope.launch {
            try {
                loginRepository.login(username, token)
                // Update UI, user logged in successfully
            } catch (exception: IOException) {
                // Update UI, login attempt failed
            }
        }
    }
}
// [END android_kotlin_coroutines_best_practices_login_vm]
