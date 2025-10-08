/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Correct the package name to match your project's namespace
package com.example.jetpackwebkit

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

/**
 * This sample demonstrates how to enable the FORCE_DARK feature for an Android WebView
 * using androidx.webkit APIs, with a check for feature support. It showcases the
 * modern and idiomatic way to configure WebView settings.
 */
// Correct the class name to match the AndroidManifest.xml entry
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You will need to create this layout file in `res/layout/`
        setContentView(R.layout.activity_webview_dark_mode)

        webView = findViewById(R.id.my_webview)

        configureWebViewDarkMode()

        webView.loadUrl("https://developer.android.com")
    }

    private fun configureWebViewDarkMode() {
        Log.d(TAG, "Attempting to configure WebView dark mode.")

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON)
            Log.d(TAG, "WebViewFeature.FORCE_DARK is supported and enabled.")
        } else {
            Log.d(TAG, "WebViewFeature.FORCE_DARK is NOT supported on this device.")
        }
    }
}