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

package com.example.snippets.room3.testingdb

import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Room
import androidx.room3.RoomDatabase

// [START android_room3_testing_db_jvm]
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

val db = Room.inMemoryDatabaseBuilder<TestDatabase>()
    .setDriver(BundledSQLiteDriver())
    .build()
// [END android_room3_testing_db_jvm]

@Database(entities = [User::class], version = 1)
abstract class TestDatabase : RoomDatabase()

@Entity
data class User(@PrimaryKey val id: Int)
