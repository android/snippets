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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class SecureWebViewClient : WebViewClient() {

    // [START android_security_webview_url_allowlist]
    companion object {
        private const val ALLOWED_DOMAIN = "example.com"
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        val scheme = uri.scheme
        val host = uri.host

        // 1. Enforce HTTPS; forward only main-frame HTTP links and block custom/file schemes
        if (scheme != "https") {
            if (scheme == "http" && request.isForMainFrame) {
                openInSystemBrowser(view, uri)
            }
            return true // Block loading in WebView
        }

        // 2. Validate host against explicit domain allowlist
        if (host != null && (host == ALLOWED_DOMAIN || host.endsWith(".$ALLOWED_DOMAIN"))) {
            return false // Allow loading in WebView
        }

        // 3. Route untrusted main-frame HTTPS domains to the system browser
        if (request.isForMainFrame) {
            openInSystemBrowser(view, uri)
        }
        return true // Block loading in WebView
    }

    private fun openInSystemBrowser(view: WebView, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        try {
            view.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w("WEBVIEW_NAVIGATION", "No browser handler available for $uri", e)
        }
    }
    // [END android_security_webview_url_allowlist]

    // [START android_security_webview_ssl_error]
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        Log.w("SSL_VALIDATION_ERROR", "TLS certificate error encountered: ${error.primaryError}")

        // Always abort the connection on TLS validation errors.
        // Never call handler.proceed() in production builds.
        handler.cancel()
    }
    // [END android_security_webview_ssl_error]
}
