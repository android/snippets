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

package com.example.compose.snippets.intents

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
import android.net.Uri

private const val URL_TO_LOAD = "https://www.example.com"
private val CONTENT_URI_TO_SHARE_WITH_OTHER_APP: Uri =
    Uri.parse("content://com.example.provider/data")

private class PackageVisibilityUseCasesActivity : Activity() {
    fun openUrlInNonBrowserApp(url: String) {
        // [START android_package_visibility_require_non_browser]
        try {
            val intent = Intent(ACTION_VIEW, Uri.parse(url)).apply {
                // The URL should either launch directly in a non-browser app (if it's
                // the default) or in the disambiguation dialog.
                addCategory(CATEGORY_BROWSABLE)
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Only browser apps are available, or a browser is the default.
            // So you can open the URL directly in your app, for example in a
            // Custom Tab.
            openInCustomTabs(url)
        }
        // [END android_package_visibility_require_non_browser]
    }

    fun avoidDisambiguationDialog() {
        // [START android_package_visibility_require_default]
        val url = URL_TO_LOAD
        try {
            // For this intent to be invoked, the system must directly launch a
            // non-browser app.
            val intent = Intent(ACTION_VIEW, Uri.parse(url)).apply {
                addCategory(CATEGORY_BROWSABLE)
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER or
                    FLAG_ACTIVITY_REQUIRE_DEFAULT
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // This code executes in one of the following cases:
            // 1. Only browser apps can handle the intent.
            // 2. The user has set a browser app as the default app.
            // 3. The user hasn't set any app as the default for handling this URL.
            openInCustomTabs(url)
        }
        // [END android_package_visibility_require_default]
    }

    fun grantUriAccessPermission() {
        // [START android_package_visibility_grant_uri_permission]
        val shareIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            data = CONTENT_URI_TO_SHARE_WITH_OTHER_APP
        }
        // [END android_package_visibility_grant_uri_permission]
    }

    private fun openInCustomTabs(url: String) {}
}
