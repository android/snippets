// Copyright 2026 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.snippets.room3.relationships.index.dataclass

import androidx.lifecycle.LiveData
import androidx.room3.*
import androidx.room3.livedata.LiveDataDaoReturnTypeConverter

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val name: String
)

@Entity(tableName = "book")
data class Book(
    @PrimaryKey val id: Int,
    val name: String,
    val user_id: Int
)

@Database(entities = [User::class, Book::class], version = 1)
@DaoReturnTypeConverters(LiveDataDaoReturnTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userBookDao(): UserBookDao
}

// [START room_3_relationships_intermediate]
@Dao
interface UserBookDao {
    @Query(
        """
        SELECT user.name AS userName, book.name AS bookName
        FROM user JOIN book ON user.id = book.user_id
        """
    )
    fun loadUserAndBookNames(): LiveData<List<UserBook>>
}

data class UserBook(val userName: String, val bookName: String)
// [END room_3_relationships_intermediate]
