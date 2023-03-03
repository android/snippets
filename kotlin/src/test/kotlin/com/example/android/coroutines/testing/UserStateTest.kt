@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.android.coroutines.testing

import com.example.android.coroutines.testing.scope.FakeUserRepository
import com.example.android.coroutines.testing.scope.UserState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

// [START android_coroutine_test_user_state_test]
class UserStateTest {
    @Test
    fun addUserTest() = runTest { // this: TestScope
        val repository = FakeUserRepository()
        val userState = UserState(repository, scope = this)

        userState.registerUser("Mona")
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        assertEquals(listOf("Mona"), userState.users.value)
    }
}
// [END android_coroutine_test_user_state_test]
