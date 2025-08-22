/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.flow.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

interface MyRepositoryVM {
    fun scores(): Flow<Int>
}

// [START android_snippets_kotlin_flow_test_my_view_model]
class MyViewModel(private val myRepository: MyRepositoryVM) : ViewModel() {
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            myRepository.scores().collect { score ->
                _score.value = score
            }
        }
    }
}
// [END android_snippets_kotlin_flow_test_my_view_model]

// [START android_snippets_kotlin_flow_test_fake_repository_viewmodel]
class FakeRepository : MyRepositoryVM {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun scores(): Flow<Int> = flow
}
// [END android_snippets_kotlin_flow_test_fake_repository_viewmodel]

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // [START android_snippets_kotlin_flow_test_hot_fake_repository]
    @Test
    fun testHotFakeRepository() = runTest {
        val fakeRepository = FakeRepository()
        val viewModel = MyViewModel(fakeRepository)

        assertEquals(0, viewModel.score.value) // Assert on the initial value

        // Start collecting values from the Repository
        viewModel.initialize()

        // Then we can send in values one by one, which the ViewModel will collect
        fakeRepository.emit(1)
        assertEquals(1, viewModel.score.value)

        fakeRepository.emit(2)
        fakeRepository.emit(3)
        assertEquals(3, viewModel.score.value) // Assert on the latest value
    }
    // [END android_snippets_kotlin_flow_test_hot_fake_repository]
}

// [START android_snippets_kotlin_flow_test_my_view_model_with_state_in]
class MyViewModelWithStateIn(myRepository: MyRepositoryVM) : ViewModel() {
    val score: StateFlow<Int> = myRepository.scores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)
}
// [END android_snippets_kotlin_flow_test_my_view_model_with_state_in]

typealias HotFakeRepository = FakeRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LazilySharingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // [START android_snippets_kotlin_flow_test_lazily_sharing_view_model]
    @Test
    fun testLazilySharingViewModel() = runTest {
        val fakeRepository = HotFakeRepository()
        val viewModel = MyViewModelWithStateIn(fakeRepository)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.score.collect {}
        }

        assertEquals(0, viewModel.score.value) // Can assert initial value

        // Trigger-assert like before
        fakeRepository.emit(1)
        assertEquals(1, viewModel.score.value)

        fakeRepository.emit(2)
        fakeRepository.emit(3)
        assertEquals(3, viewModel.score.value)
    }
    // [END android_snippets_kotlin_flow_test_lazily_sharing_view_model]
}
