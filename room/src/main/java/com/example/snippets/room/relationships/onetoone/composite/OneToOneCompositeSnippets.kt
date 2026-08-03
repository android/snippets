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

package com.example.snippets.room.relationships.onetoone.composite

import androidx.room3.Database
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
import androidx.room3.RoomDatabase

// [START android_room3_relationships_onetoone_composite]
@Entity(primaryKeys = ["firstName", "lastName"])
data class User(
    val firstName: String,
    val lastName: String,
    val age: Int
)

@Entity
data class Library(
    @PrimaryKey val libraryId: Long,
    val userFirstName: String,
    val userLastName: String
)

data class UserAndLibrary(
    @Embedded val user: User,
    @Relation(
        parentColumns = ["firstName", "lastName"],
        entityColumns = ["userFirstName", "userLastName"]
    )
    val library: Library
)
// [END android_room3_relationships_onetoone_composite]

@Database(entities = [User::class, Library::class], version = 1)
abstract class AppDatabase : RoomDatabase()
