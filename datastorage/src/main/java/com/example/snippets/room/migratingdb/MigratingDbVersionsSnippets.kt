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

package com.example.snippets.room.migratingdb

import android.content.Context
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.RenameTable
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.AutoMigrationSpec
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL
import kotlin.reflect.KClass

// Fake annotations to avoid Room KSP running on these example databases since schemas are
// not exported and those are required to auto migrations.
internal annotation class Database(
    val version: Int,
    val entities: Array<KClass<*>>,
    val autoMigrations: Array<AutoMigration> = [],
)

internal annotation class AutoMigration(
    val from: Int,
    val to: Int,
    val spec: KClass<*> = Any::class
)

@Entity
data class User(
    @PrimaryKey val id: Int,
    val name: String
)

interface UserDao

// [START android_room3_migrating_db_automigration]
// Database class before the version update.
@Database(
  version = 1,
  entities = [User::class]
)
abstract class AppDatabaseV1 : RoomDatabase() {
  abstract fun userDao(): UserDao
}

// Database class after the version update.
@Database(
  version = 2,
  entities = [User::class],
  autoMigrations = [
    AutoMigration(from = 1, to = 2)
  ]
)
abstract class AppDatabaseV2 : RoomDatabase() {
  abstract fun userDao(): UserDao
}
// [END android_room3_migrating_db_automigration]


// [START android_room3_migrating_db_automigration_spec]
@Database(
  version = 2,
  entities = [User::class],
  autoMigrations = [
    AutoMigration (
      from = 1,
      to = 2,
      spec = MigrationSpec1To2::class
    )
  ]
)
abstract class AppDatabaseWithSpec : RoomDatabase() {
  abstract fun userDao(): UserDao
}

@RenameTable(fromTableName = "User", toTableName = "AppUser")
internal class MigrationSpec1To2 : AutoMigrationSpec
// [END android_room3_migrating_db_automigration_spec]

// Manual migrations example
abstract class ManualMigrationDatabase : RoomDatabase()

fun createManualMigrationDb(applicationContext: Context) {
    // [START android_room3_migrating_db_manual]
    val MIGRATION_1_2 = object : Migration(1, 2) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection.executeSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
          "PRIMARY KEY(`id`))")
      }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection.executeSQL("ALTER TABLE Book ADD COLUMN pub_year INTEGER")
      }
    }

    Room.databaseBuilder<ManualMigrationDatabase>(applicationContext, "database-name")
      .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
      .build()
    // [END android_room3_migrating_db_manual]
}

// Fallback example
abstract class FallbackMigrationDatabase : RoomDatabase()

fun createFallbackDb(applicationContext: Context) {
    // [START android_room3_migrating_db_fallback]
    Room.databaseBuilder<FallbackMigrationDatabase>(applicationContext, "database-name")
            .fallbackToDestructiveMigration()
            .build()
    // [END android_room3_migrating_db_fallback]
}
