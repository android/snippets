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
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// [START android_security_webview_js_interface]
class SecureWebInterface(private val webView: WebView) {

    companion object {
        private const val ALLOWED_DOMAIN = "example.com"

        // Use WebViewCompat.addWebMessageListener when androidx.webkit is available
        // to enforce per-frame origin isolation instead of main-frame URL checks.
        fun registerSecureMessageListener(webView: WebView) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                WebViewCompat.addWebMessageListener(
                    webView,
                    "secureBridge",
                    setOf("https://example.com"),
                ) { _, message, sourceOrigin, isMainFrame, _ ->
                    val isTrustedFrame = isMainFrame &&
                        sourceOrigin.scheme == "https" &&
                        sourceOrigin.host == ALLOWED_DOMAIN
                    if (isTrustedFrame) {
                        message.data?.let { handleVerifiedMessage(it) }
                    }
                }
            }
        }

        private fun handleVerifiedMessage(payload: String) {
            // Safe implementation after per-frame origin verification
        }
    }

    // Fallback when hardening an existing @JavascriptInterface bridge.
    // @JavascriptInterface executes on the JavaBridge background thread, requiring
    // UI-thread marshaling to read webView.url. Note that webView.url reflects the
    // main-frame URL; use WebViewCompat.addWebMessageListener for per-frame isolation.
    @JavascriptInterface
    fun performSensitiveAction(payload: String) {
        val currentUrl = readWebViewUrlOnUiThread()
        val uri = currentUrl?.let { Uri.parse(it) }
        val scheme = uri?.scheme
        val host = uri?.host

        val isTrustedOrigin = scheme == "https" &&
            host != null &&
            (host == ALLOWED_DOMAIN || host.endsWith(".$ALLOWED_DOMAIN"))

        if (!isTrustedOrigin) {
            throw SecurityException("Unauthorized JavascriptInterface call from: $currentUrl")
        }

        processAction(payload)
    }

    private fun readWebViewUrlOnUiThread(): String? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return webView.url
        }
        var currentUrl: String? = null
        val latch = CountDownLatch(1)
        val posted = webView.post {
            currentUrl = webView.url
            latch.countDown()
        }
        if (!posted) {
            return null
        }
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                return null
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            return null
        }
        return currentUrl
    }

    private fun processAction(payload: String) {
        // Safe implementation after origin verification
    }
}
// [END android_security_webview_js_interface]
