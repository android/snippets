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

package com.example.snippets.room3.sqliteroommigration

import android.content.Context
import androidx.room3.*
import androidx.room3.migration.Migration
import androidx.room3.support.getSupportWrapper
import androidx.sqlite.SQLiteConnection
import java.util.Date

class DateConverter {
    @ColumnTypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @ColumnTypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}

// [START android_room3_sqlite_migration_entity]
@Entity(tableName = "users")
data class User(
  @PrimaryKey @ColumnInfo(name = "userid") val id: String,
  @ColumnInfo(name = "username") val userName: String?,
  @ColumnInfo(name = "last_update") val date: Date?,
)
// [END android_room3_sqlite_migration_entity]

@Dao
interface UserDao

// [START android_room3_sqlite_migration_db]
@Database(entities = [User::class], version = 2)
@ColumnTypeConverters(DateConverter::class)
abstract class UsersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
// [END android_room3_sqlite_migration_db]


// [START android_room3_sqlite_migration_path]
val MIGRATION_1_2 = object : Migration(1, 2) {
  override suspend fun migrate(connection: SQLiteConnection) {
    // Empty implementation, because the schema isn't changing.
  }
}
// [END android_room3_sqlite_migration_path]

fun createDb(applicationContext: Context) {
    // [START android_room3_sqlite_migration_instantiation]
    val db =
        Room.databaseBuilder<UsersDatabase>(applicationContext, "database-name")
            .addMigrations(MIGRATION_1_2)
            .build()
    // [END android_room3_sqlite_migration_instantiation]
}

fun incrementalMigration(roomDatabase: UsersDatabase) {
    // [START android_room3_sqlite_migration_incremental]
    // Get SupportSQLiteDatabase wrapper
    val legacyDb = roomDatabase.getSupportWrapper()
    legacyDb.execSQL("INSERT INTO users ...")
    // [END android_room3_sqlite_migration_incremental]
}
