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

package com.example.kmp.snippets.room

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RoomDatabase
import androidx.room3.RoomRawQuery
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

// [START android_kmp_room_dao]
// shared/src/commonMain/kotlin/TodoDao.kt

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(item: TodoEntity)

    @Query("SELECT count(*) FROM TodoEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM TodoEntity")
    fun getAllAsFlow(): Flow<List<TodoEntity>>
}
// [END android_kmp_room_dao]

private object RawQueryExample {
    // [START android_kmp_room_raw_query_dao]
    @Dao
    interface TodoDao {
        @RawQuery
        suspend fun getTodos(query: RoomRawQuery): List<TodoEntity>
    }
    // [END android_kmp_room_raw_query_dao]

    abstract class AppDatabase : RoomDatabase() {
        abstract fun todoDao(): TodoDao
    }

    // [START android_kmp_room_raw_query_exec]
    suspend fun AppDatabase.getTodosWithLowercaseTitle(title: String): List<TodoEntity> {
        val query = RoomRawQuery(
            sql = "SELECT * FROM TodoEntity WHERE title = ?",
            onBindStatement = {
                it.bindText(1, title.lowercase())
            }
        )

        return todoDao().getTodos(query)
    }
    // [END android_kmp_room_raw_query_exec]
}

private object DaoMethodExamples {
    @Entity
    data class Todo(
        @PrimaryKey val id: Long
    )

    @Dao
    interface ExampleTodoDao {
        // [START android_kmp_room_suspend_query]
        @Query("SELECT * FROM Todo")
        suspend fun getAllTodos(): List<Todo>
        // [END android_kmp_room_suspend_query]

        // [START android_kmp_room_suspend_transaction]
        @Transaction
        suspend fun transaction() {
            // ...
        }
        // [END android_kmp_room_suspend_transaction]

        // [START android_kmp_room_reactive_flow]
        @Query("SELECT * FROM Todo")
        fun getTodosFlow(): Flow<List<Todo>>
        // [END android_kmp_room_reactive_flow]
    }
}
