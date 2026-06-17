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

package com.example.android.basics.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Placeholder types
const val ITEM_1 = 1
val ALL_MESSAGES = listOf("Hello", "How are you?", "Bye")
class MyUnitUnderTest(repository: MyRepository)

interface MyRepository {
    fun observeChatMessages(): Flow<String> = ALL_MESSAGES.asFlow()
    fun scores(): Flow<Int> = flow {}
}

// [START android_kotlin_flow_test_fake]
class MyFakeRepository : MyRepository {
    fun observeCount() = flow {
        emit(ITEM_1)
    }
}
// [END android_kotlin_flow_test_fake]

class FlowTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // [START android_kotlin_flow_test_verify]
    @Test
    fun myTest() {
        // Given a class with fake dependencies:
        val sut = MyUnitUnderTest(MyFakeRepository())
        // Trigger and verify
        // ...
    }
    // [END android_kotlin_flow_test_verify]

    // [START android_kotlin_flow_test_assert]
    @Test
    fun myRepositoryTest() = runTest {
        // [START_EXCLUDE silent]
        val fakeSource1 = 1
        val fakeSource2 = 2

        class MyRepository(a: Int, b: Int) {
            val counter: Flow<Int> = flow { emit(ITEM_1) }
        }

        // [END_EXCLUDE]
        // Given a repository that combines values from two data sources:
        val repository = MyRepository(fakeSource1, fakeSource2)

        // When the repository emits a value
        val firstItem = repository.counter.first() // Returns the first item in the flow

        // Then check it's the expected item
        assertEquals(ITEM_1, firstItem)
    }
    // [END android_kotlin_flow_test_assert]

    // [START android_kotlin_flow_test_assert_list]
    @Test
    fun myRepositoryTestList() = runTest {
        val repository = MyFakeRepository()
        // Given a repository with a fake data source that emits ALL_MESSAGES
        val messages = repository.observeChatMessages().toList()

        // When all messages are emitted then they should be ALL_MESSAGES
        assertEquals(ALL_MESSAGES, messages)
    }
    // [END android_kotlin_flow_test_assert_list]

    @Suppress("UNUSED_VARIABLE")
    suspend fun testTestingOperators(outputFlow: Flow<Int>, predicate: (Int) -> Boolean) {
        // [START android_kotlin_flow_test_operators]
        // Take the second item
        outputFlow.drop(1).first()

        // Take the first 5 items
        outputFlow.take(5).toList()

        // Takes the first item verifying that the flow is closed after that
        outputFlow.single()

        // Finite data streams
        // Verify that the flow emits exactly N elements (optional predicate)
        outputFlow.count()
        outputFlow.count(predicate)
        // [END android_kotlin_flow_test_operators]
    }

    // [START android_kotlin_flow_test_continuous]
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun continuouslyCollect() = runTest {
        val dataSource = FakeDataSource()
        val repository = Repository(dataSource)

        val values = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.scores().toList(values)
        }

        dataSource.emit(1)
        assertEquals(10, values[0]) // Assert on the list contents

        dataSource.emit(2)
        dataSource.emit(3)
        assertEquals(30, values[2])

        assertEquals(3, values.size) // Assert the number of items collected
    }
    // [END android_kotlin_flow_test_continuous]

    // [START android_kotlin_flow_test_turbine]
    @Test
    fun usingTurbine() = runTest {
        val dataSource = FakeDataSource()
        val repository = Repository(dataSource)

        repository.scores().test {
            // Make calls that will trigger value changes only within test{}
            dataSource.emit(1)
            assertEquals(10, awaitItem())

            dataSource.emit(2)
            awaitItem() // Ignore items if needed, can also use skip(n)

            dataSource.emit(3)
            assertEquals(30, awaitItem())
        }
    }
    // [END android_kotlin_flow_test_turbine]

    // [START android_kotlin_flow_test_hot_fake]
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
    // [END android_kotlin_flow_test_hot_fake]

    // [START android_kotlin_flow_test_lazily]
    @OptIn(ExperimentalCoroutinesApi::class)
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
    // [END android_kotlin_flow_test_lazily]
}

// [START android_kotlin_flow_test_continuous_source]
class Repository(private val dataSource: DataSource) {
    fun scores(): Flow<Int> {
        return dataSource.counts().map { it * 10 }
    }
}

class FakeDataSource : DataSource {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun counts(): Flow<Int> = flow
}
// [END android_kotlin_flow_test_continuous_source]

interface DataSource {
    fun counts(): Flow<Int>
}

// [START android_kotlin_flow_test_stateflow_vm]
class MyViewModel(private val myRepository: MyRepository) : ViewModel() {
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
// [END android_kotlin_flow_test_stateflow_vm]

// [START android_kotlin_flow_test_stateflow_fake]
class FakeRepository : MyRepository {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun scores(): Flow<Int> = flow
}
// [END android_kotlin_flow_test_stateflow_fake]

class HotFakeRepository : MyRepository {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun scores(): Flow<Int> = flow
}

// [START android_kotlin_flow_test_statein]
class MyViewModelWithStateIn(myRepository: MyRepository) : ViewModel() {
    val score: StateFlow<Int> = myRepository.scores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)
}
// [END android_kotlin_flow_test_statein]
