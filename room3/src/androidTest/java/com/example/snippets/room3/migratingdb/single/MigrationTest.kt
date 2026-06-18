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

package com.example.snippets.room3.migratingdb.single

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snippets.room3.migratingdb.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {}
}

@Database(entities = [User::class], version = 2)
abstract class MigrationDb : RoomDatabase()

// [START android_room3_migrating_db_test_single]
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = instrumentation,
        databaseClass = MigrationDb::class,
        driver = AndroidSQLiteDriver(),
        file = instrumentation.targetContext.getDatabasePath(TEST_DB),
    )

    @Test
    fun migrate1To2() = runTest {
        val connection = helper.createDatabase(1)
        // Database has schema version 1. Insert some data using SQL queries.
        // You can't use DAO classes because they expect the latest schema.
        connection.execSQL("INSERT INTO User (id, name) VALUES (1, 'John Doe')")
        connection.close()

        // Re-open the database with version 2 and provide MIGRATION_1_2
        val migratedConnection = helper.runMigrationsAndValidate(2, listOf(MIGRATION_1_2))
        
        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val hasData = migratedConnection.prepare("SELECT COUNT(*) FROM User").use {
          it.step()
          it.getLong(0) > 0
        }
        assertTrue("Expected data was not migrated", hasData)
        migratedConnection.close()
    }
}
// [END android_room3_migrating_db_test_single]
