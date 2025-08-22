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

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private const val ITEM_1 = "item1"
private val ALL_MESSAGES = listOf("message1", "message2")

interface MyRepository {
    fun observeCount(): Flow<String>
    fun observeChatMessages(): Flow<List<String>>
}

// [START android_snippets_kotlin_flow_test_fake_repository]
class MyFakeRepository : MyRepository {
    override fun observeCount() = flow {
        emit(ITEM_1)
    }

    override fun observeChatMessages(): Flow<List<String>> = flow {
        emit(ALL_MESSAGES)
    }
}
// [END android_snippets_kotlin_flow_test_fake_repository]

class MyUnitUnderTest(private val myRepository: MyRepository)

class RepositoryTest {

    @Test
    fun myTest() {
        // [START android_snippets_kotlin_flow_test_my_test]
        // Given a class with fake dependencies:
        val sut = MyUnitUnderTest(MyFakeRepository())
        // Trigger and verify
        // ...
        // [END android_snippets_kotlin_flow_test_my_test]
    }

    @Test
    fun myRepositoryTest_first() = runTest {
        // [START android_snippets_kotlin_flow_test_repository_test_first]
        // Given a repository that combines values from two data sources:
        val repository = MyFakeRepository()

        // When the repository emits a value
        val firstItem = repository.observeCount().first() // Returns the first item in the flow

        // Then check it's the expected item
        assertEquals(ITEM_1, firstItem)
        // [END android_snippets_kotlin_flow_test_repository_test_first]
    }

    @Test
    fun myRepositoryTest_toList() = runTest {
        // [START android_snippets_kotlin_flow_test_repository_test_to_list]
        // Given a repository with a fake data source that emits ALL_MESSAGES
        val repository = MyFakeRepository()
        val messages = repository.observeChatMessages().toList()

        // When all messages are emitted then they should be ALL_MESSAGES
        assertEquals(ALL_MESSAGES, messages[0])
        // [END android_snippets_kotlin_flow_test_repository_test_to_list]
    }
}

// [START android_snippets_kotlin_flow_test_repository_and_datasource]
interface DataSource {
    fun counts(): Flow<Int>
}

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
// [END android_snippets_kotlin_flow_test_repository_and_datasource]

@OptIn(ExperimentalCoroutinesApi::class)
class ContinuousCollectionTest {
    @Test
    fun continuouslyCollect() = runTest {
        // [START android_snippets_kotlin_flow_test_continuously_collect]
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
        // [END android_snippets_kotlin_flow_test_continuously_collect]
    }

    @Test
    fun usingTurbine() = runTest {
        // [START android_snippets_kotlin_flow_test_using_turbine]
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
        // [END android_snippets_kotlin_flow_test_using_turbine]
    }
}
