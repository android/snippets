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

package com.example.example.snippet.views.webapps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.annotation.UiThread;
import androidx.webkit.Navigation;
import androidx.webkit.NavigationListener;
import androidx.webkit.NavigationParameters;
import androidx.webkit.Page;
import androidx.webkit.WebResourceErrorCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("RequiresFeature")
@OptIn(markerClass = WebViewCompat.ExperimentalNavigate.class)
class NavigateJava {

    // [START android_views_webapps_navigate_lifecycle_java]
    public class WebNavigationManager {

        private Navigation mCurrentNavigation;
        private final WebView mWebView;

        public WebNavigationManager(@NonNull WebView webView) {
            mWebView = webView;
            setupListener();
        }

        private void setupListener() {
            // 1. Define listener to observe navigation lifecycle events
            NavigationListener listener = new NavigationListener() {
                @Override
                public void onNavigationStarted(@NonNull Navigation navigation) {
                    if (navigation.equals(mCurrentNavigation)) {
                        // Navigation started
                    }
                }

                @Override
                public void onNavigationRedirected(@NonNull Navigation navigation) {
                    if (navigation.equals(mCurrentNavigation)) {
                        // Navigation encountered a redirect
                    }
                }

                @Override
                public void onNavigationCompleted(@NonNull Navigation navigation) {
                    if (navigation.equals(mCurrentNavigation)) {
                        if (navigation.didCommit()) {
                            // Navigation committed successfully
                        } else if (navigation.didCommitErrorPage()) {
                            // Navigation committed an error page
                            int statusCode = navigation.getStatusCode();
                            WebResourceErrorCompat error = navigation.getWebResourceError();
                        }
                    }
                }

                @Override
                public void onFirstContentfulPaintMillis(@NonNull Page page, long durationMillis) {
                    if (mCurrentNavigation != null && page.equals(mCurrentNavigation.getPage())) {
                        // Page rendering started (First Contentful Paint achieved)
                    }
                }
            };

            // 2. Register listener on the main thread
            WebViewCompat.addNavigationListener(mWebView, listener);
        }

        @UiThread
        public void navigateToPage(@NonNull String url) {
            // Check feature availability
            if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEBVIEW_NAVIGATE_EXPERIMENTAL_V1)) {
                // Fall back to standard loadUrl if navigate API is unavailable
                mWebView.loadUrl(url);
                return;
            }

            // 3. Configure navigation parameters
            NavigationParameters params = new NavigationParameters.Builder()
                .setShouldReplaceCurrentEntry(true)
                .addAdditionalHeaders(Collections.singletonMap(
                    "X-Test-Navigate-Header", "TestValue"
                ))
                .build();

            // 4. Initiate navigation on the UI thread
            mCurrentNavigation = WebViewCompat.navigate(mWebView, url, params);
        }
    }
    // [END android_views_webapps_navigate_lifecycle_java]

    private void propagateAppStateSnippet(WebView webView) {
        // [START android_views_webapps_navigate_headers_java]
        // Attach host app metadata so the server can verify compatibility and tailor content
        Map<String, String> headers = new HashMap<>();
        headers.put("X-App-Version", BuildConfig.VERSION_NAME);
        headers.put("X-Client-Platform", "Android");

        NavigationParameters params = new NavigationParameters.Builder()
            .addAdditionalHeaders(headers)
            .build();

        // Use navigate instead of loadUrl to retain custom headers across state restoration
        WebViewCompat.navigate(webView, "https://www.example.com", params);
        // [END android_views_webapps_navigate_headers_java]
    }

    private void saveStateSnippet(WebView webView) {
        // [START android_views_webapps_navigate_save_state_java]
        // Save state with a maximum bundle size limit (for example, 64 KB)
        int maxSizeBytes = 64 * 1024;
        boolean includeForwardState = false;
        Bundle outState = new Bundle();

        WebViewCompat.saveState(webView, outState, maxSizeBytes, includeForwardState);
        // [END android_views_webapps_navigate_save_state_java]
    }

    private static class BuildConfig {
        static final String VERSION_NAME = "1.0.0";
    }
}
