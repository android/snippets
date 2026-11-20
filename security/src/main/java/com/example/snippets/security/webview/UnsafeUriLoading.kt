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

package com.example.snippets.security.webview

import android.net.Uri

private object UnsafeUriLoadingExample {
    // [START android_security_webview_is_uri_trusted]
    fun isUriTrusted(incomingUri: String, trustedHostName: String): Boolean {
        try {
            val uri = Uri.parse(incomingUri)
            return uri.scheme == "https" && uri.host == trustedHostName
        } catch (e: NullPointerException) {
            throw NullPointerException("incomingUri is null or not well-formed")
        }
    }
    // [END android_security_webview_is_uri_trusted]
}
