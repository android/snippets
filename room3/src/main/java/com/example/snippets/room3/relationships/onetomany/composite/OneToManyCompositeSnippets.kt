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

package com.example.snippets.room3.relationships.onetomany.composite

import androidx.room3.*

// [START android_room3_relationships_onetomany_composite]
@Entity(primaryKeys = ["firstName", "lastName"])
data class User(
    val firstName: String,
    val lastName: String,
    val age: Int
)

@Entity
data class Playlist(
    @PrimaryKey val playlistId: Long,
    val userFirstName: String,
    val userLastName: String,
    val playlistName: String
)

data class UserWithPlaylists(
    @Embedded val user: User,
    @Relation(
         parentColumns = ["firstName", "lastName"],
         entityColumns = ["userFirstName", "userLastName"]
    )
    val playlists: List<Playlist>
)
// [END android_room3_relationships_onetomany_composite]

@Database(entities = [User::class, Playlist::class], version = 1)
abstract class AppDatabase : RoomDatabase()
