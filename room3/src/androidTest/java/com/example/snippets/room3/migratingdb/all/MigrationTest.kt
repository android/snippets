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

package com.example.snippets.room3.migratingdb.all

import androidx.room3.*
import androidx.room3.migration.Migration
import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snippets.room3.migratingdb.User
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {}
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override suspend fun migrate(connection: SQLiteConnection) {}
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {}
}

@Database(entities = [User::class], version = 4)
abstract class MigrationDb : RoomDatabase()

@Database(entities = [User::class], version = 4)
abstract class AppDatabase : RoomDatabase()

// [START android_room3_migrating_db_test_all]
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    // Array of all migrations.
    private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        databaseClass = MigrationDb::class,
        driver = AndroidSQLiteDriver(),
        file = instrumentation.targetContext.getDatabasePath(TEST_DB),
    )

    @Test
    fun migrateAll() = runTest {
        // Create earliest version of the database.
        val connection = helper.createDatabase(1)
        connection.close()

        // Create latest version of the database.
        val db = Room.databaseBuilder<AppDatabase>(instrumentation.targetContext, TEST_DB)
          .setDriver(AndroidSQLiteDriver())
          .addMigrations(*ALL_MIGRATIONS)
          .build()
        // Open the database, Room validates the schema once all migrations
        // execute.
        db.useReaderConnection { connection ->
          // Perform additional validation
        }

        db.close()
    }
}
// [END android_room3_migrating_db_test_all]
