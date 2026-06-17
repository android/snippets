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

package com.example.snippets.room3.relationships.manytomany.composite

import androidx.room3.*

// [START android_room3_relationships_manytomany_composite]
@Entity(primaryKeys = ["playlistId", "creatorId"])
data class Playlist(
    val playlistId: Long,
    val creatorId: Long,
    val playlistName: String
)

@Entity
data class Song(
    @PrimaryKey val songId: Long,
    val songName: String,
    val artist: String
)

@Entity(primaryKeys = ["playlistId", "creatorId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val creatorId: Long,
    val songId: Long
)

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
         parentColumns = ["playlistId", "creatorId"],
         entityColumns = ["songId"],
         associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)
// [END android_room3_relationships_manytomany_composite]

@Database(entities = [Playlist::class, Song::class, PlaylistSongCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase()
