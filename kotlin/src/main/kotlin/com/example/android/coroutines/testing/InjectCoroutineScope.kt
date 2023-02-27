package com.example.android.coroutines.testing.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UserRepository {
    fun getAllUsers(): Flow<List<String>>
    suspend fun register(name: String)
}

class FakeUserRepository : UserRepository {
    private var users = MutableStateFlow(listOf<String>())

    override fun getAllUsers(): Flow<List<String>> = users

    override suspend fun register(name: String) {
        users.update { users -> users + name }
        println("Registered $name")
    }
}

class UserState(
    private val userRepository: UserRepository,
    private val scope: CoroutineScope,
) {
    val users = userRepository.getAllUsers()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun registerUser(name: String) {
        scope.launch {
            userRepository.register(name)
        }
    }
}
