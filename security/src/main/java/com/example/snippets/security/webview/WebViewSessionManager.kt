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

import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView

object WebViewSessionManager {

    // [START android_security_webview_session_cleanup]
    fun performSecureLogout(webView: WebView) {
        // 1. Remove all cookies and flush cookie storage to disk once removal completes
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies {
            cookieManager.flush()
        }

        // 2. Clear HTML5 DOM storage (localStorage, sessionStorage, IndexedDB)
        WebStorage.getInstance().deleteAllData()

        // 3. Purge WebView disk cache, navigation history, and saved form data
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
    }
    // [END android_security_webview_session_cleanup]
}
