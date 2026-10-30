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

import android.webkit.WebView
import java.net.URI

private fun removeJavascriptInterfaceExample(webView: WebView) {
    // [START android_security_webview_remove_javascript_interface]
    webView.removeJavascriptInterface("myObject")
    // [END android_security_webview_remove_javascript_interface]
}

private object LoadSecureUrlExample {
    // [START android_security_webview_load_secure_url]
    fun loadSecureUrl(webView: WebView?, url: String?) {
        webView?.let { wv -> // Ensure valid WebView and URL.
            url?.let {
                try {
                    val uri = URI(url)
                    if (uri.scheme.equals("https", ignoreCase = true)) { // Enforce HTTPS scheme for security.
                        wv.loadUrl(url)
                    } else {
                        // Log an error or handle the case where the URL is not secure.
                        System.err.println("Attempted to load a non-HTTPS URL: $url")
                    }
                } catch (e: Exception) {
                    // Handle exception for improper URL format.
                    System.err.println("Invalid URL syntax: $url")
                }
            }
        }
    }
    // [END android_security_webview_load_secure_url]
}
