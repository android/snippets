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

package com.example.kmp.snippets.sqlite

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL

// [START android_kmp_sqlite_driver]
fun main() {
    val databaseConnection = BundledSQLiteDriver().open("todos.db")
    databaseConnection.execSQL(
        "CREATE TABLE IF NOT EXISTS Todo (id INTEGER PRIMARY KEY, content TEXT)"
    )
    databaseConnection.prepare(
        "INSERT OR IGNORE INTO Todo (id, content) VALUES (? ,?)"
    ).use { stmt ->
        stmt.bindInt(index = 1, value = 1)
        stmt.bindText(index = 2, value = "Try Room in the KMP project.")
        stmt.step()
    }
    databaseConnection.prepare("SELECT content FROM Todo").use { stmt ->
        while (stmt.step()) {
            println("Action item: ${stmt.getText(0)}")
        }
    }
    databaseConnection.close()
}
// [END android_kmp_sqlite_driver]

private fun performTransaction() {
    // [START android_kmp_sqlite_transaction]
    val connection: SQLiteConnection = BundledSQLiteDriver().open("todos.db")
    connection.execSQL("BEGIN IMMEDIATE TRANSACTION")
    try {
        // perform database operations in transaction.
        connection.execSQL("END TRANSACTION")
    } catch (t: Throwable) {
        connection.execSQL("ROLLBACK TRANSACTION")
    }
    // [END android_kmp_sqlite_transaction]
}

private fun queryWithNoResult() {
    // [START android_kmp_sqlite_query_no_result]
    val connection: SQLiteConnection = BundledSQLiteDriver().open("todos.db")
    connection.execSQL("ALTER TABLE ...")
    // [END android_kmp_sqlite_query_no_result]
}

private fun queryResultWithNoArgs() {
    // [START android_kmp_sqlite_query_result_no_args]
    val connection: SQLiteConnection = BundledSQLiteDriver().open("todos.db")
    connection.prepare("SELECT * FROM Pet").use { statement ->
        while (statement.step()) {
            // read columns.
            statement.getInt(0)
            statement.getText(1)
        }
    }
    // [END android_kmp_sqlite_query_result_no_args]
}

private fun queryResultWithArgs(connection: SQLiteConnection, id: Int) {
    // [START android_kmp_sqlite_query_result_with_args]
    connection.prepare("SELECT * FROM Pet WHERE id = ?").use { statement ->
        statement.bindInt(1, id)
        if (statement.step()) {
            // row found, read columns.
        } else {
            // row not found.
        }
    }
    // [END android_kmp_sqlite_query_result_with_args]
}
