package com.example.android.coroutines.testing.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UserRepository {
    suspend fun getAllUsers(): List<String>
    suspend fun register(name: String)
}

class FakeUserRepository : UserRepository {
    private var users = listOf<String>()

    override suspend fun getAllUsers(): List<String> = users

    override suspend fun register(name: String) {
        delay(100L) // Simulate work
        users = users + name
        println("Registered $name")
    }
}

// [START android_coroutine_test_user_state]
class UserState(
    private val userRepository: UserRepository,
    private val scope: CoroutineScope,
) {
    private val _users = MutableStateFlow(emptyList<String>())
    val users: StateFlow<List<String>> = _users.asStateFlow()

    fun registerUser(name: String) {
        scope.launch {
            userRepository.register(name)
            _users.update { userRepository.getAllUsers() }
        }
    }
}
// [END android_coroutine_test_user_state]
