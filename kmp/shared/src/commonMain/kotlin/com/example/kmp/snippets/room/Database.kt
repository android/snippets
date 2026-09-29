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

package com.example.kmp.snippets.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.deferredTransaction
import androidx.room3.immediateTransaction
import androidx.room3.migration.AutoMigrationSpec
import androidx.room3.migration.Migration
import androidx.room3.useReaderConnection
import androidx.room3.useWriterConnection
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

// [START android_kmp_room_database]
// shared/src/commonMain/kotlin/Database.kt

@Database(entities = [TodoEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): TodoDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
// [END android_kmp_room_database]

// [START android_kmp_room_instantiate]
// shared/src/commonMain/kotlin/Database.kt

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
// [END android_kmp_room_instantiate]

// [START android_kmp_room_migration]
object Migration_1_2 : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // ...
    }
}
// [END android_kmp_room_migration]

// [START android_kmp_room_auto_migration_spec]
class AutoMigrationSpec_1_2 : AutoMigrationSpec {
    override suspend fun onPostMigrate(connection: SQLiteConnection) {
        // ...
    }
}
// [END android_kmp_room_auto_migration_spec]

// [START android_kmp_room_callback]
object MyRoomCallback : RoomDatabase.Callback() {
    override suspend fun onCreate(connection: SQLiteConnection) {
        // ...
    }

    override suspend fun onDestructiveMigration(connection: SQLiteConnection) {
        // ...
    }

    override suspend fun onOpen(connection: SQLiteConnection) {
        // ...
    }
}
// [END android_kmp_room_callback]

private suspend fun performWriteTransaction(builder: RoomDatabase.Builder<AppDatabase>) {
    // [START android_kmp_room_write_transaction]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val database: RoomDatabase = ...
    // [START_EXCLUDE silent]
     */
    val database: RoomDatabase = getRoomDatabase(builder)
    // [END_EXCLUDE]
    database.useWriterConnection { transactor ->
        transactor.immediateTransaction {
            // perform database operations in transaction.
        }
    }
    // [END android_kmp_room_write_transaction]
}

private suspend fun performReadTransaction(builder: RoomDatabase.Builder<AppDatabase>) {
    // [START android_kmp_room_read_transaction]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val database: RoomDatabase = ...
    // [START_EXCLUDE silent]
     */
    val database: RoomDatabase = getRoomDatabase(builder)
    // [END_EXCLUDE]
    database.useReaderConnection { transactor ->
        transactor.deferredTransaction {
            // perform database operations in transaction.
        }
    }
    // [END android_kmp_room_read_transaction]
}
