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

package com.example.snippets.room3.relationships.index.multimap

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.MapColumn
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase

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
abstract class AppDatabase : RoomDatabase() {
    abstract fun userBookDao(): UserBookDao
}

@Dao
interface UserBookDao {
    // [START android_room3_relationships_multimap]
    @Query(
        """
        SELECT *
        FROM user JOIN book ON user.id = book.user_id
        """
    )
    suspend fun loadUserAndBookNames(): Map<User, List<Book>>
    // [END android_room3_relationships_multimap]

    // [START android_room3_relationships_multimap_count]
    @Query(
        """
        SELECT user.*, COUNT(book.id) AS book_count
        FROM user LEFT JOIN book ON user.id = book.user_id
        GROUP BY user.id
        """
    )
    suspend fun loadUserAndBookCount(): Map<User, @MapColumn(columnName = "book_count") Int>
    // [END android_room3_relationships_multimap_count]
}
