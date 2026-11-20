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

package com.example.snippets.connectivity

import android.content.Context
import androidx.annotation.WorkerThread
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import okhttp3.Cache
import okhttp3.OkHttpClient

// Holds the cache timestamp that the snippet reads and updates.
private class DownloadCache {
    private var lastUpdateTime = 0L

    // Runs on a worker thread because it opens a network connection, and disconnects when done.
    @WorkerThread
    fun checkForUpdate(url: URL) {
        // [START android_connectivity_avoid_downloads_last_modified]
        // url represents the website containing the content to place into the cache.
        val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        val currentTime: Long = System.currentTimeMillis()
        val lastModified: Long = conn.getHeaderFieldDate("Last-Modified", currentTime)

        // lastUpdateTime represents when the cache was last updated.
        if (lastModified < lastUpdateTime) {
            // Skip update.
        } else {
            // Parse update.
            lastUpdateTime = lastModified
        }
        // [END android_connectivity_avoid_downloads_last_modified]
        conn.disconnect()
    }
}

// Takes a Context so the snippet can read the app's cache directory.
private fun createCachingClient(context: Context): OkHttpClient {
    // [START android_connectivity_avoid_downloads_okhttp_cache]
    val cacheDir = context.getCacheDir()
    val cacheSize = 10L * 1024L * 1024L // 10 MiB
    val client: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(cacheDir, cacheSize))
        .build()
    // [END android_connectivity_avoid_downloads_okhttp_cache]
    return client
}
