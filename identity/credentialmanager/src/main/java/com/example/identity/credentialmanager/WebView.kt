/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.identity.credentialmanager

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.example.identity.credentialmanager.WebViewClientImpl

// [START android_identity_webview_main_activity]
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val url = "https://passkeys-codelab.glitch.me/"
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true

                        webViewClient = WebViewClientImpl()
                    }
                },
                update = { webView ->
                    run {
                        webView.loadUrl(url)
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_AUTHENTICATION)) {
                            WebSettingsCompat.setWebAuthenticationSupport(
                                webView.settings,
                                WebSettingsCompat.WEB_AUTHENTICATION_SUPPORT_FOR_APP,
                            )
                            // Check if getWebauthenticationSupport may have been disabled by the WebView.
                            Log.e(
                                "WebViewPasskeyDemo",
                                "getWebAuthenticationSupport result: " + WebSettingsCompat.getWebAuthenticationSupport(
                                    webView.settings
                                ),
                            )
                        } else {
                            Log.e("WebViewPasskeyDemo", "WebView does not support passkeys.")
                        }
                    }
                },
            )
        }
    }
}
// [END android_identity_webview_main_activity]
