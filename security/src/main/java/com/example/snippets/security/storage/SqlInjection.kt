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

package com.example.snippets.security.storage

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.sql.Connection
import java.sql.PreparedStatement

private fun buildSelectionClause(userInput: String) {
    // [START android_security_storage_sql_selection_args]
    // Constructs a selection clause with a replaceable parameter.
    val selectionClause = "var = ?"

    // Sets up an array of arguments.
    val selectionArgs: Array<String> = arrayOf("")

    // Adds values to the selection arguments array.
    selectionArgs[0] = userInput
    // [END android_security_storage_sql_selection_args]
}

private class OrderValidator(private val db: SQLiteDatabase) {
    // [START android_security_storage_sql_raw_query]
    fun validateOrderDetails(email: String, orderNumber: String): Boolean {
        val cursor = db.rawQuery(
            "select * from purchases where EMAIL = ? and ORDER_NUMBER = ?",
            arrayOf(email, orderNumber)
        )

        val bool = cursor?.moveToFirst() ?: false
        cursor?.close()

        return bool
    }
    // [END android_security_storage_sql_raw_query]
}

private fun updateEmployeeRole(con: Connection) {
    // [START android_security_storage_sql_prepared_statement]
    val pstmt: PreparedStatement = con.prepareStatement(
        "UPDATE EMPLOYEES SET ROLE = ? WHERE ID = ?"
    ).apply {
        setString(1, "Barista")
        setInt(2, 295094)
    }
    // [END android_security_storage_sql_prepared_statement]
}

private fun queryFeedEntries(dbHelper: SQLiteOpenHelper) {
    // [START android_security_storage_sql_query_method]
    val db: SQLiteDatabase = dbHelper.getReadableDatabase()
    // Defines a projection that specifies which columns from the database
    // should be selected.
    val projection = arrayOf(
        BaseColumns._ID,
        FeedEntry.COLUMN_NAME_TITLE,
        FeedEntry.COLUMN_NAME_SUBTITLE
    )

    // Filters results WHERE "title" = 'My Title'.
    val selection: String = FeedEntry.COLUMN_NAME_TITLE.toString() + " = ?"
    val selectionArgs = arrayOf("My Title")

    // Specifies how to sort the results in the returned Cursor object.
    val sortOrder: String = FeedEntry.COLUMN_NAME_SUBTITLE.toString() + " DESC"

    val cursor = db.query(
        FeedEntry.TABLE_NAME, // The table to query.
        projection, // The array of columns to return
        //   (pass null to get all).
        selection, // The columns for the WHERE clause.
        selectionArgs, // The values for the WHERE clause.
        null, // Don't group the rows.
        null, // Don't filter by row groups.
        sortOrder // The sort order.
    ).use {
        // Perform operations on the query result here.
        it.moveToFirst()
    }
    // [END android_security_storage_sql_query_method]
}

private object FeedEntry : BaseColumns {
    const val TABLE_NAME = "entry"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_SUBTITLE = "subtitle"
}
