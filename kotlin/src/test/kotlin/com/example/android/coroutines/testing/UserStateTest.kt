@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.android.coroutines.testing

import com.example.android.coroutines.testing.scope.FakeUserRepository
import com.example.android.coroutines.testing.scope.UserState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UserStateTest {
    val testScope = TestScope()

    @Test
    fun addUserTest() = testScope.runTest {
        val repository = FakeUserRepository()
        val userState = UserState(repository, backgroundScope)

        // Add an empty collector so that stateIn is active
        backgroundScope.launch { userState.users.collect() }

        userState.registerUser("Mona")
        runCurrent() // Let the coroutine complete and changes propagate

        assertEquals(listOf("Mona"), userState.users.value)
    }
}
