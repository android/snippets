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

package com.example.snippets.room3.relationships.onetoone.simple

import androidx.room3.*

// [START room3_relationships_onetoone_define]
@Entity
data class User(
    @PrimaryKey val userId: Long,
    val name: String,
    val age: Int
)

@Entity
data class Library(
    @PrimaryKey val libraryId: Long,
    val userOwnerId: Long
)
// [END room3_relationships_onetoone_define]

// [START room3_relationships_onetoone_query]
data class UserAndLibrary(
    @Embedded val user: User,
    @Relation(
         parentColumns = ["userId"],
         entityColumns = ["userOwnerId"]
    )
    val library: Library
)
// [END room3_relationships_onetoone_query]

@Dao
interface UserDao {
    // [START room3_relationships_onetoone_dao]
    @Transaction
    @Query("SELECT * FROM User")
    suspend fun getUsersAndLibraries(): List<UserAndLibrary>
    // [END room3_relationships_onetoone_dao]
}

@Database(entities = [User::class, Library::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
