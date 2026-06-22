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

package com.example.snippets.security.intents

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

// Dummy DB Helper for compilation
class MyDbHelper(context: Context) : SQLiteOpenHelper(context, "mydb", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}

// [START android_security_contentprovider_secure]
class SecureDataProvider : ContentProvider() {
    private val tableName = "users"
    private lateinit var dbHelper: MyDbHelper

    override fun onCreate(): Boolean {
        dbHelper = MyDbHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = tableName
        // Strict projection map to prevent querying unauthorized columns
        queryBuilder.projectionMap = mapOf(
            "_id" to "_id",
            "display_name" to "display_name"
        )
        // Enable strict validation (always available since minSdk is 36)
        queryBuilder.setStrict(true)
        queryBuilder.setStrictColumns(true)
        queryBuilder.setStrictGrammar(true)

        // MUST parameterize selection criteria; NEVER append selection strings directly
        val db = dbHelper.readableDatabase
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun getType(uri: Uri): String? = null
}
// [END android_security_contentprovider_secure]
