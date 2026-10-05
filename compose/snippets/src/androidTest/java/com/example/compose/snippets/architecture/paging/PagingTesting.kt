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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult
import androidx.paging.insertSeparators
import androidx.paging.testing.TestPager
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private object PagingTestingSnapshotScope {
    object viewModel {
        val items: Flow<PagingData<String>> = flowOf()
    }

    // [START android_architecture_paging_test_snapshot_basic]
    fun test_items_contain_one_to_ten() = runTest {
        // Get the Flow of PagingData from the ViewModel under test
        val items: Flow<PagingData<String>> = viewModel.items

        val itemsSnapshot: List<String> = items.asSnapshot {
            // Scroll to the 50th item in the list. This will also suspend till
            // the prefetch requirement is met if there's one.
            // It also suspends until all loading is complete.
            scrollTo(index = 50)
        }

        // With the asSnapshot complete, you can now verify that the snapshot
        // has the expected values
        assertEquals(
            expected = (0..50).map(Int::toString),
            actual = itemsSnapshot
        )
    }
    // [END android_architecture_paging_test_snapshot_basic]

    // [START android_architecture_paging_test_snapshot_scroll]
    fun test_footer_is_visible() = runTest {
        // Get the Flow of PagingData from the ViewModel under test
        val items: Flow<PagingData<String>> = viewModel.items

        val itemsSnapshot: List<String> = items.asSnapshot {
            // Scroll till the footer is visible
            appendScrollWhile { item: String -> item != "Footer" }
        }
    }
    // [END android_architecture_paging_test_snapshot_scroll]
}

private object PagingTestingTransformScope {
    interface MyRepository {
        fun pagingSource(): PagingSource<Int, String>
    }

    // In a real app, this class lives in the main source set.
    // [START android_architecture_paging_test_viewmodel]
    class MyViewModel(
        myRepository: MyRepository
    ) {
        val items = Pager(
            config = PagingConfig(pageSize = 20),
            initialKey = null,
            pagingSourceFactory = { myRepository.pagingSource() }
        )
            .flow
            .map { pagingData ->
                pagingData.insertSeparators<String, String> { before, _ ->
                    when {
                        // Add a dashed String separator if the prior item is a multiple of 10
                        before?.last() == '0' -> "---------"
                        // Return null to avoid adding a separator between two items.
                        else -> null
                    }
                }
            }
    }
    // [END android_architecture_paging_test_viewmodel]

    // [START android_architecture_paging_test_fake_repository]
    class FakeMyRepository() : MyRepository {
        private val items = (0..100).map(Any::toString)
        private val pagingSourceFactory = items.asPagingSourceFactory()

        // Expose as a function so a new PagingSource instance is
        // created each time it is called by the Pager
        override fun pagingSource() = pagingSourceFactory()
    }
    // [END android_architecture_paging_test_fake_repository]

    // [START android_architecture_paging_test_separators]
    fun test_separators_are_added_every_10_items() = runTest {
        // Create your ViewModel
        val viewModel = MyViewModel(
            myRepository = FakeMyRepository()
        )
        // Get the Flow of PagingData from the ViewModel with the separator transformations applied
        val items: Flow<PagingData<String>> = viewModel.items

        val snapshot: List<String> = items.asSnapshot()

        // With the asSnapshot complete, you can now verify that the snapshot
        // has the expected separators.
    }
    // [END android_architecture_paging_test_separators]
}

private object PagingTestingSourceScope {
    const val DEFAULT_SUBREDDIT = "androiddev"
    val CONFIG = PagingConfig(pageSize = 10)

    data class RedditPost(val subreddit: String, val title: String = "")

    class PostFactory {
        fun createRedditPost(subreddit: String, title: String = ""): RedditPost =
            RedditPost(subreddit, title)
    }

    val postFactory = PostFactory()

    interface RedditApi {
        var failureMsg: String?
        fun addPost(post: RedditPost)
        fun clearPosts()
    }

    open class FakeRedditApi : RedditApi {
        override var failureMsg: String? = null
        override fun addPost(post: RedditPost) {}
        override fun clearPosts() {}
        fun setReturnsError() {}
    }

    class RedditPagingSource(
        val api: RedditApi,
        val subreddit: String
    ) : PagingSource<String, RedditPost>() {
        var errorNextLoad: Boolean = false
        override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> =
            LoadResult.Page(emptyList(), null, null)
        override fun getRefreshKey(state: PagingState<String, RedditPost>): String? = null
    }

    // [START android_architecture_paging_test_source_setup]
    class SubredditPagingSourceTest {
        private val mockPosts = listOf(
            postFactory.createRedditPost(DEFAULT_SUBREDDIT),
            postFactory.createRedditPost(DEFAULT_SUBREDDIT),
            postFactory.createRedditPost(DEFAULT_SUBREDDIT)
        )
        private val fakeApi = FakeRedditApi().apply {
            mockPosts.forEach { post -> addPost(post) }
        }

        @Test
        fun loadReturnsPageWhenOnSuccessfulLoadOfItemKeyedData() = runTest {
            val pagingSource = RedditPagingSource(
                fakeApi,
                DEFAULT_SUBREDDIT
            )

            val pager = TestPager(CONFIG, pagingSource)

            val result = pager.refresh() as LoadResult.Page

            // Write assertions against the loaded data
            assertThat(result.data)
                .containsExactlyElementsIn(mockPosts)
                .inOrder()
        }
    }
    // [END android_architecture_paging_test_source_setup]

    class AdditionalPagingSourceTests {
        private val fakeApi = FakeRedditApi()
        private val source = RedditPagingSource(fakeApi, DEFAULT_SUBREDDIT)
        private val pager = TestPager(CONFIG, source)
        private val testPosts = emptyList<RedditPost>()

        // [START android_architecture_paging_test_source_consecutive]
        @Test
        fun test_consecutive_loads() = runTest {

            val page = with(pager) {
                refresh()
                append()
                append()
            } as LoadResult.Page

            assertThat(page.data)
                .containsExactlyElementsIn(testPosts)
                .inOrder()
        }
        // [END android_architecture_paging_test_source_consecutive]

        // [START android_architecture_paging_test_source_error]
        @Test
        fun refresh_returnError() {
            val pagingSource = RedditPagingSource(
                fakeApi,
                DEFAULT_SUBREDDIT
            )
            // Configure your fake to return errors
            fakeApi.setReturnsError()
            val pager = TestPager(CONFIG, source)

            runTest {
                source.errorNextLoad = true
                val result = pager.refresh()
                assertTrue(result is LoadResult.Error)

                val page = pager.getLastLoadedPage()
                assertThat(page).isNull()
            }
        }
        // [END android_architecture_paging_test_source_error]
    }

    class RedditDb {
        fun clearAllTables() {}
        companion object {
            fun create(context: Context, useInMemory: Boolean): RedditDb = RedditDb()
        }
    }

    object SubRedditViewModel {
        const val DEFAULT_SUBREDDIT = "androiddev"
    }

    fun mockRedditApi(): FakeRedditApi = FakeRedditApi()

    // In a real app, this class lives in the main source set.
    // [START android_architecture_paging_test_mediator_class]
    @OptIn(ExperimentalPagingApi::class)
    class PageKeyedRemoteMediator(
        private val db: RedditDb,
        private val redditApi: RedditApi,
        private val subredditName: String
    ) : RemoteMediator<Int, RedditPost>() {
        // [START_EXCLUDE]
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, RedditPost>
        ): MediatorResult {
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        // [END_EXCLUDE]
    }
    // [END android_architecture_paging_test_mediator_class]

    // [START android_architecture_paging_test_mediator_setup]
    @ExperimentalPagingApi
    @OptIn(ExperimentalCoroutinesApi::class)
    @RunWith(AndroidJUnit4::class)
    class PageKeyedRemoteMediatorTest {
        private val postFactory = PostFactory()
        private val mockPosts = listOf(
            postFactory.createRedditPost(SubRedditViewModel.DEFAULT_SUBREDDIT),
            postFactory.createRedditPost(SubRedditViewModel.DEFAULT_SUBREDDIT),
            postFactory.createRedditPost(SubRedditViewModel.DEFAULT_SUBREDDIT)
        )
        private val mockApi = mockRedditApi()

        private val mockDb = RedditDb.create(
            ApplicationProvider.getApplicationContext(),
            useInMemory = true
        )

        @After
        fun tearDown() {
            mockDb.clearAllTables()
            // Clear out failure message to default to the successful response.
            mockApi.failureMsg = null
            // Clear out posts after each test run.
            mockApi.clearPosts()
        }
    }
    // [END android_architecture_paging_test_mediator_setup]

    @OptIn(ExperimentalPagingApi::class)
    class PageKeyedRemoteMediatorTestCases {
        private val postFactory = PostFactory()
        private val mockPosts = listOf(
            postFactory.createRedditPost(SubRedditViewModel.DEFAULT_SUBREDDIT)
        )
        private val mockApi = mockRedditApi()
        private val mockDb = RedditDb()

        // [START android_architecture_paging_test_mediator_more_data]
        @Test
        fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
            // Add mock results for the API to return.
            mockPosts.forEach { post -> mockApi.addPost(post) }
            val remoteMediator = PageKeyedRemoteMediator(
                mockDb,
                mockApi,
                SubRedditViewModel.DEFAULT_SUBREDDIT
            )
            val pagingState = PagingState<Int, RedditPost>(
                listOf(),
                null,
                PagingConfig(10),
                10
            )
            val result = remoteMediator.load(LoadType.REFRESH, pagingState)
            assertTrue { result is MediatorResult.Success }
            assertFalse { (result as MediatorResult.Success).endOfPaginationReached }
        }
        // [END android_architecture_paging_test_mediator_more_data]

        // [START android_architecture_paging_test_mediator_no_more_data]
        @Test
        fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
            // To test endOfPaginationReached, don't set up the mockApi to return post
            // data here.
            val remoteMediator = PageKeyedRemoteMediator(
                mockDb,
                mockApi,
                SubRedditViewModel.DEFAULT_SUBREDDIT
            )
            val pagingState = PagingState<Int, RedditPost>(
                listOf(),
                null,
                PagingConfig(10),
                10
            )
            val result = remoteMediator.load(LoadType.REFRESH, pagingState)
            assertTrue { result is MediatorResult.Success }
            assertTrue { (result as MediatorResult.Success).endOfPaginationReached }
        }
        // [END android_architecture_paging_test_mediator_no_more_data]

        // [START android_architecture_paging_test_mediator_error]
        @Test
        fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
            // Set up failure message to throw exception from the mock API.
            mockApi.failureMsg = "Throw test failure"
            val remoteMediator = PageKeyedRemoteMediator(
                mockDb,
                mockApi,
                SubRedditViewModel.DEFAULT_SUBREDDIT
            )
            val pagingState = PagingState<Int, RedditPost>(
                listOf(),
                null,
                PagingConfig(10),
                10
            )
            val result = remoteMediator.load(LoadType.REFRESH, pagingState)
            assertTrue { result is MediatorResult.Error }
        }
        // [END android_architecture_paging_test_mediator_error]
    }

    @OptIn(ExperimentalTestApi::class)
    object EndToEndTestScope {
        class MockRedditApi : FakeRedditApi()

        open class DefaultServiceLocator(val useInMemoryDb: Boolean) {
            open fun getRedditApi(): RedditApi = FakeRedditApi()
        }

        object ServiceLocator {
            fun swap(locator: DefaultServiceLocator) {}
        }

        @Composable
        fun MyTheme(content: @Composable () -> Unit) {
            content()
        }

        @Composable
        fun RedditScreen(initialSubreddit: String) {}

        // [START android_architecture_paging_test_end_to_end]
        // import androidx.compose.ui.test.assertIsDisplayed
        //         // import androidx.compose.ui.test.hasText
        // import androidx.compose.ui.test.junit4.createComposeRule
        // import androidx.compose.ui.test.onNodeWithTag
        // import androidx.compose.ui.test.onNodeWithText
        // import androidx.compose.ui.test.performClick
        // import androidx.compose.ui.test.performTextClearance
        // import androidx.compose.ui.test.performTextInput
        // import androidx.test.ext.junit.runners.AndroidJUnit4
        // import kotlinx.coroutines.test.runTest
        // import org.junit.Before
        // import org.junit.Rule
        // import org.junit.Test
        // import org.junit.runner.RunWith

        @RunWith(AndroidJUnit4::class)
        class RedditScreenTest {

            @get:Rule
            val composeTestRule = createComposeRule()

            private val postFactory = PostFactory()
            private val mockApi = MockRedditApi()

            @Before
            fun setup() {
                // Pre-populate the mock API with test data for the default subreddit
                mockApi.addPost(postFactory.createRedditPost(subreddit = "androiddev", title = "Jetpack Compose Paging"))

                // Swap your real dependency injection module/Service Locator with the mock API
                ServiceLocator.swap(
                    object : DefaultServiceLocator(useInMemoryDb = true) {
                        override fun getRedditApi(): RedditApi = mockApi
                    }
                )
            }

            @Test
            fun loadsTheDefaultResults() = runTest {
                // 1. Set the Compose UI content
                composeTestRule.setContent {
                    MyTheme {
                        // Assume that this composable uses `collectAsLazyPagingItems()` internally
                        RedditScreen(initialSubreddit = "androiddev")
                    }
                }

                // 2. Wait for the asynchronous Paging loads to complete
                composeTestRule.waitUntilExactlyOneExists(
                    matcher = hasText("Jetpack Compose Paging"),
                    timeoutMillis = 5000
                )

                // 3. Assert that the loaded paged items are displayed correctly on screen
                composeTestRule.onNodeWithText("Jetpack Compose Paging").assertIsDisplayed()
            }

            @Test
            fun loadsNewDataBasedOnUserInput() = runTest {
                // Add data for a different subreddit to the mock API
                mockApi.addPost(postFactory.createRedditPost(subreddit = "compose", title = "Compose Testing"))

                composeTestRule.setContent {
                    MyTheme {
                        RedditScreen(initialSubreddit = "androiddev")
                    }
                }

                // Wait for the initial load to finish
                composeTestRule.waitUntilExactlyOneExists(hasText("Jetpack Compose Paging"))

                // Simulate user entering a new subreddit in a text field and clicking search
                composeTestRule.onNodeWithTag("SubredditInput").performTextClearance()
                composeTestRule.onNodeWithTag("SubredditInput").performTextInput("compose")
                composeTestRule.onNodeWithTag("SearchButton").performClick()

                // Wait for the new paged data to load
                composeTestRule.waitUntilExactlyOneExists(
                    matcher = hasText("Compose Testing"),
                    timeoutMillis = 5000
                )

                // Assert the old data is gone and the new data is displayed
                composeTestRule.onNodeWithText("Jetpack Compose Paging").assertDoesNotExist()
                composeTestRule.onNodeWithText("Compose Testing").assertIsDisplayed()
            }
        }
        // [END android_architecture_paging_test_end_to_end]
    }
}
