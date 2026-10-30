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

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewAssetLoader.Builder
import androidx.webkit.WebViewClientCompat

private fun Context.configureWebViewAssetLoader(webView: WebView) {
    // [START android_security_webview_asset_loader]
    val assetLoader: WebViewAssetLoader = Builder()
        .addPathHandler("/assets/", AssetsPathHandler(this))
        .build()

    webView.setWebViewClient(object : WebViewClientCompat() {
        @RequiresApi(21)
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        @Suppress("deprecation") // for API < 21
        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(Uri.parse(url))
        }
    })

    val webViewSettings: WebSettings = webView.getSettings()
    // Setting this off for security. Off by default for SDK versions >= 16.
    webViewSettings.allowFileAccessFromFileURLs = false
    // Off by default, deprecated for SDK versions >= 30.
    webViewSettings.allowUniversalAccessFromFileURLs = false
    // Keeping these off is less critical but still a good idea, especially if your app is not
    // using file:// or content:// URLs.
    webViewSettings.allowFileAccess = false
    webViewSettings.allowContentAccess = false

    // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
    // If the application's assets are in the "main/assets" folder this will read the file
    // from "main/assets/www/index.html" and load it as if it were hosted on:
    // https://appassets.androidplatform.net/assets/www/index.html
    webView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
    // [END android_security_webview_asset_loader]
}
