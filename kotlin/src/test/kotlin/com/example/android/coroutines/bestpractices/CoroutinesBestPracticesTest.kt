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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakeArticlesDataSource : ArticlesDataSource()

// [START android_kotlin_coroutines_best_practices_test]    
class ArticlesRepositoryTest {

    @Test
    fun testBookmarkArticle() = runTest {
        // Pass the testScheduler provided by runTest's coroutine scope to
        // the test dispatcher
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)

        val articlesDataSource = FakeArticlesDataSource()
        val repository = ArticlesRepository(
            articlesDataSource,
            defaultDispatcher = testDispatcher
        )
        val article = Article()
        repository.bookmarkArticle(article)
        assertThat(articlesDataSource.isBookmarked(article)).isTrue()
    }
}
// [END android_kotlin_coroutines_best_practices_test]
