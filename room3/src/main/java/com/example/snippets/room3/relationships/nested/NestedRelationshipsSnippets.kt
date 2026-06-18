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

package com.example.snippets.room3.relationships.nested

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Junction
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Relation
import androidx.room3.RoomDatabase
import androidx.room3.Transaction

// [START android_room3_relationships_nested_define]
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

@Entity
data class Song(
    @PrimaryKey val songId: Long,
    val songName: String,
    val artist: String
)

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)
// [END android_room3_relationships_nested_define]

// [START android_room3_relationships_nested_query_1]
data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumns = ["playlistId"],
        entityColumns = ["songId"],
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)
// [END android_room3_relationships_nested_query_1]

// [START android_room3_relationships_nested_query_2]
data class UserWithPlaylistsAndSongs(
    @Embedded val user: User,
    @Relation(
        entity = Playlist::class,
        parentColumns = ["userId"],
        entityColumns = ["userCreatorId"]
    )
    val playlists: List<PlaylistWithSongs>
)
// [END android_room3_relationships_nested_query_2]

@Dao
interface UserPlaylistSongDao {
    // [START android_room3_relationships_nested_dao]
    @Transaction
    @Query("SELECT * FROM User")
    suspend fun getUsersWithPlaylistsAndSongs(): List<UserWithPlaylistsAndSongs>
    // [END android_room3_relationships_nested_dao]
}

@Database(entities = [User::class, Playlist::class, Song::class, PlaylistSongCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPlaylistSongDao(): UserPlaylistSongDao
}
