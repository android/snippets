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

package com.example.example.snippet.views.webapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.UiThread
import androidx.webkit.Navigation
import androidx.webkit.NavigationListener
import androidx.webkit.NavigationParameters
import androidx.webkit.Page
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

@SuppressLint("RequiresFeature")
@OptIn(WebViewCompat.ExperimentalNavigate::class)
private object NavigateSnippets {
    // [START android_views_webapps_navigate_lifecycle]
    class WebNavigationManager(private val webView: WebView) {
        // Track the navigation instance returned by the API
        private var currentNavigation: Navigation? = null

        init {
            // 1. Define listener to observe navigation lifecycle events
            val listener = object : NavigationListener {
                override fun onNavigationStarted(navigation: Navigation) {
                    if (navigation == currentNavigation) {
                        // Navigation started
                    }
                }

                override fun onNavigationRedirected(navigation: Navigation) {
                    if (navigation == currentNavigation) {
                        // Navigation encountered a redirect
                    }
                }

                override fun onNavigationCompleted(navigation: Navigation) {
                    if (navigation == currentNavigation) {
                        if (navigation.didCommit()) {
                            // Navigation committed successfully
                        } else if (navigation.didCommitErrorPage()) {
                            // Navigation committed an error page
                            val statusCode = navigation.statusCode
                            val error = navigation.webResourceError
                        }
                    }
                }

                override fun onFirstContentfulPaintMillis(page: Page, durationMillis: Long) {
                    // Match page with current navigation
                    if (page == currentNavigation?.page) {
                        // Page rendering started (First Contentful Paint achieved)
                    }
                }
            }

            // 2. Register listener on the main thread
            WebViewCompat.addNavigationListener(webView, listener)
        }

        @UiThread
        fun navigateToPage(url: String) {
            // Check feature availability
            if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEBVIEW_NAVIGATE_EXPERIMENTAL_V1)) {
                // Fall back to standard loadUrl if navigate API is unavailable
                webView.loadUrl(url)
                return
            }

            // 3. Configure navigation parameters
            val params = NavigationParameters.Builder()
                .setShouldReplaceCurrentEntry(true)
                .addAdditionalHeaders(
                    mapOf("X-Test-Navigate-Header" to "TestValue")
                )
                .build()

            // 4. Initiate navigation on the UI thread
            currentNavigation = WebViewCompat.navigate(webView, url, params)
        }
    }
    // [END android_views_webapps_navigate_lifecycle]
}

@SuppressLint("RequiresFeature")
@OptIn(WebViewCompat.ExperimentalNavigate::class)
private fun propagateAppStateSnippet(webView: WebView) {
    // [START android_views_webapps_navigate_headers]
    // Attach host app metadata so the server can verify compatibility and tailor content
    val params = NavigationParameters.Builder()
        .addAdditionalHeaders(
            mapOf(
                "X-App-Version" to BuildConfig.VERSION_NAME,
                "X-Client-Platform" to "Android"
            )
        )
        .build()

    // Use navigate instead of loadUrl to retain custom headers across state restoration
    WebViewCompat.navigate(webView, "https://www.example.com", params)
    // [END android_views_webapps_navigate_headers]
}

private fun saveStateSnippet(webView: WebView) {
    // [START android_views_webapps_navigate_save_state]
    // Save state with a maximum bundle size limit (for example, 64 KB)
    val maxSizeBytes = 64 * 1024
    val includeForwardState = false
    val outState = Bundle()

    WebViewCompat.saveState(webView, outState, maxSizeBytes, includeForwardState)
    // [END android_views_webapps_navigate_save_state]
}

private object BuildConfig {
    const val VERSION_NAME = "1.0.0"
}
