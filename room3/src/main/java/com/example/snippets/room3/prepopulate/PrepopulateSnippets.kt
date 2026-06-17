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

package com.example.snippets.room3.prepopulate

import android.content.Context
import androidx.room3.*
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import java.io.File

@Entity
data class SampleEntity(@PrimaryKey val id: Int)

@Database(entities = [SampleEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase()

fun createSimpleDb(appContext: Context) {
    // [START room_3_prepopulate_asset]
    Room.databaseBuilder<AppDatabase>(appContext, "sample.db")
        .createFromAsset("database/myapp.db")
        .build()
    // [END room_3_prepopulate_asset]

    // [START room_3_prepopulate_file]
    Room.databaseBuilder<AppDatabase>(appContext, "sample.db")
        .createFromFile(File("mypath"))
        .build()
    // [END room_3_prepopulate_file]
}

// Fallback example
// [START room_3_prepopulate_fallback_ex]
// Database class definition declaring version 3.
@Database(entities = [SampleEntity::class], version = 3)
abstract class FallbackAppDatabase : RoomDatabase() {
    // ...
}

// Destructive migrations are enabled and a prepackaged database
// is provided.
fun createFallbackDb(appContext: Context) {
    Room.databaseBuilder<FallbackAppDatabase>(appContext, "sample.db")
        .createFromAsset("database/myapp.db")
        .fallbackToDestructiveMigration()
        .build()
}
// [END room_3_prepopulate_fallback_ex]

// Implemented example
// [START room_3_prepopulate_implemented_ex]
// Database class definition declaring version 3.
@Database(entities = [SampleEntity::class], version = 3)
abstract class ImplementedAppDatabase : RoomDatabase() {
    // ...
}

// Migration path definition from version 2 to version 3.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // ...
    }
}

// A prepackaged database is provided.
fun createImplementedDb(appContext: Context) {
    Room.databaseBuilder<ImplementedAppDatabase>(appContext, "sample.db")
        .createFromAsset("database/myapp.db")
        .addMigrations(MIGRATION_2_3)
        .build()
}
// [END room_3_prepopulate_implemented_ex]

// Multi-step example
// [START room_3_prepopulate_multistep_ex]
// Database class definition declaring version 4.
// Added entities parameter to make it compile
@Database(entities = [SampleEntity::class], version = 4)
abstract class MultiStepAppDatabase : RoomDatabase() {
    // ...
}

// Migration path definition from version 3 to version 4.
val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // ...
    }
}

// Destructive migrations are enabled and a prepackaged database is
// provided.
fun createMultiStepDb(appContext: Context) {
    Room.databaseBuilder<MultiStepAppDatabase>(appContext, "sample.db")
        .createFromAsset("database/myapp.db")
        .addMigrations(MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()
}
// [END room_3_prepopulate_multistep_ex]
