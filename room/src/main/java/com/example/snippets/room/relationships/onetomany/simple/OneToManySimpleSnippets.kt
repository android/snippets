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

package com.example.snippets.room.relationships.onetomany.simple

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Relation
import androidx.room3.RoomDatabase
import androidx.room3.Transaction

// [START android_room3_relationships_onetomany_define]
@Entity
data class User(
    @PrimaryKey val userId: Long,
    val name: String,
    val age: Int
)

@Entity
data class Playlist(
    @PrimaryKey val playlistId: Long,
    val userCreatorId: Long,
    val playlistName: String
)
// [END android_room3_relationships_onetomany_define]

// [START android_room3_relationships_onetomany_query]
data class UserWithPlaylists(
    @Embedded val user: User,
    @Relation(
        parentColumns = ["userId"],
        entityColumns = ["userCreatorId"]
    )
    val playlists: List<Playlist>
)
// [END android_room3_relationships_onetomany_query]

@Dao
interface UserDao {
    // [START android_room3_relationships_onetomany_dao]
    @Transaction
    @Query("SELECT * FROM User")
    suspend fun getUsersWithPlaylists(): List<UserWithPlaylists>
    // [END android_room3_relationships_onetomany_dao]
}

@Database(entities = [User::class, Playlist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
