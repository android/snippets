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

package com.example.snippets.security.network

import java.io.File
import java.io.IOException
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

// [START android_security_network_secure_download]
fun downloadToAppStorage(client: OkHttpClient, url: String, token: String, dest: File) {
    val httpUrl = url.toHttpUrl()
    require(httpUrl.isHttps && httpUrl.host == "cdn.example.com")
    val request = Request.Builder()
        .url(httpUrl)
        .header("Authorization", "Bearer $token")
        .build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
        dest.outputStream().use { out -> checkNotNull(response.body).byteStream().copyTo(out) }
    }
}
// [END android_security_network_secure_download]
