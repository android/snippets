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

package com.example.studio.write

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.compose.runtime.Composable

// [START android_studio_write_lint_new_api]
@SuppressLint("NewApi")
fun logNotificationChannelInfo() {
    // Uses NotificationChannel (requires API 26+)
    // ...
}
// [END android_studio_write_lint_new_api]

// [START android_studio_write_lint_new_api_composable]
@SuppressLint("NewApi")
@Composable
fun MyComposable() {
    // ...
}
// [END android_studio_write_lint_new_api_composable]

// [START android_studio_write_lint_parser_error]
@SuppressLint("ParserError")
class FeedProvider : ContentProvider() {
// [END android_studio_write_lint_parser_error]
    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}

// [START android_studio_write_lint_suppress_all]
@SuppressLint("all")
// [START_EXCLUDE silent]
private fun suppressAllPlaceholder() {
}
// [END_EXCLUDE]
// [END android_studio_write_lint_suppress_all]
