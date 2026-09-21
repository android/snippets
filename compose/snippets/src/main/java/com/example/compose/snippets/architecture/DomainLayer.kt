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

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object DomainLayerSnippet1 {
    interface NewsRepository
    interface AuthorsRepository

    // [START android_architecture_domain_layer_dependencies]
    class GetLatestNewsWithAuthorsUseCase(
        private val newsRepository: NewsRepository,
        private val authorsRepository: AuthorsRepository
    ) { /* ... */ }
    // [END android_architecture_domain_layer_dependencies]
}

private object DomainLayerSnippet2 {
    interface NewsRepository
    interface AuthorsRepository
    class FormatDateUseCase

    // [START android_architecture_domain_layer_nested_dependencies]
    class GetLatestNewsWithAuthorsUseCase(
        private val newsRepository: NewsRepository,
        private val authorsRepository: AuthorsRepository,
        private val formatDateUseCase: FormatDateUseCase
    ) { /* ... */ }
    // [END android_architecture_domain_layer_nested_dependencies]
}

private object DomainLayerSnippet3 {
    interface UserRepository {
        fun getPreferredDateFormat(): String = "yyyy-MM-dd"
        fun getPreferredLocale(): Locale = Locale.getDefault()
    }

    // [START android_architecture_domain_layer_format_date_use_case]
    class FormatDateUseCase(userRepository: UserRepository) {

        private val formatter = SimpleDateFormat(
            userRepository.getPreferredDateFormat(),
            userRepository.getPreferredLocale()
        )

        operator fun invoke(date: Date): String {
            return formatter.format(date)
        }
    }
    // [END android_architecture_domain_layer_format_date_use_case]
}

private object DomainLayerSnippet4 {
    class FormatDateUseCase {
        operator fun invoke(date: Date): String = ""
        operator fun invoke(calendar: Calendar): String = invoke(calendar.time)
    }

    // [START android_architecture_domain_layer_call_use_case_viewmodel]
    class MyViewModel(formatDateUseCase: FormatDateUseCase) : ViewModel() {
        init {
            val today = Calendar.getInstance()
            val todaysDate = formatDateUseCase(today)
            /* ... */
        }
    }
    // [END android_architecture_domain_layer_call_use_case_viewmodel]
}

private object DomainLayerSnippet5 {
    // [START android_architecture_domain_layer_threading]
    class MyUseCase(
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {

        suspend operator fun invoke(/* ... */) = withContext(defaultDispatcher) {
            // Long-running blocking operations happen on a background thread.
        }
    }
    // [END android_architecture_domain_layer_threading]
}

private object DomainLayerSnippet6 {
    data class Article(val authorId: String)
    data class Author(val id: String)
    data class ArticleWithAuthor(val article: Article, val author: Author)

    interface NewsRepository {
        suspend fun fetchLatestNews(): List<Article>
    }

    interface AuthorsRepository {
        suspend fun getAuthor(authorId: String): Author
    }

    // [START android_architecture_domain_layer_combine_repositories]
    /**
     * This use case fetches the latest news and the associated author.
     */
    class GetLatestNewsWithAuthorsUseCase(
        private val newsRepository: NewsRepository,
        private val authorsRepository: AuthorsRepository,
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {
        suspend operator fun invoke(): List<ArticleWithAuthor> =
            withContext(defaultDispatcher) {
                val news = newsRepository.fetchLatestNews()
                val result: MutableList<ArticleWithAuthor> = mutableListOf()
                // This is not parallelized, the use case is linearly slow.
                for (article in news) {
                    // The repository exposes suspend functions
                    val author = authorsRepository.getAuthor(article.authorId)
                    result.add(ArticleWithAuthor(article, author))
                }
                result
            }
    }
    // [END android_architecture_domain_layer_combine_repositories]
}
