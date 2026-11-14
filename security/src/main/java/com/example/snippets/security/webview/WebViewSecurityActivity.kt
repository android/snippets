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

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

// [START android_security_webview_hardening]
class WebViewSecurityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            // Enable JavaScript only if strictly required by your application
            javaScriptEnabled = true

            // Disable local file and ContentProvider access
            allowFileAccess = false
            allowContentAccess = false

            // Disable cross-origin file URL access schemes
            @Suppress("DEPRECATION")
            allowFileAccessFromFileURLs = false
            @Suppress("DEPRECATION")
            allowUniversalAccessFromFileURLs = false
        }

        // Attach a custom WebViewClient that enforces HTTPS and domain allowlisting
        webView.webViewClient = SecureWebViewClient()
    }
}
// [END android_security_webview_hardening]
