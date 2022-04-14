package com.example.android.coroutines.testing

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

// [START coroutine_test_repo_dispatcher_injection_test]
class RepositoryTest {
    @Test
    fun repoInitWorksAndDataIsHelloWorld() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = Repository(dispatcher)

        repository.initialize()
        advanceUntilIdle() // Runs the new coroutine
        assertEquals(true, repository.initialized.get())

        val data = repository.fetchData() // No thread switch, delay is skipped
        assertEquals("Hello world", data)
    }
}
// [END coroutine_test_repo_dispatcher_injection_test]

class BetterRepositoryTest {
    // [START coroutine_test_repo_dispatcher_injection_test_better]
    @Test
    fun repoInitWorks() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = BetterRepository(dispatcher)

        repository.initialize().await() // Suspends until the new coroutine is done
        assertEquals(true, repository.initialized.get())

        // [START_EXCLUDE]
        val data = repository.fetchData()
        assertEquals("Hello world", data)
        // [END_EXCLUDE]
    }
    // [END coroutine_test_repo_dispatcher_injection_test_better]
}
