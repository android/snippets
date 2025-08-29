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

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.CoroutineScope

class WebViewMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START android_identity_initialize_the_webview]
        val credentialManagerHandler = CredentialManagerHandler(this)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            AndroidView(
                factory = {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true

                        // Test URL:
                        val url = "https://passkeys-codelab.glitch.me/"
                        val listenerSupported = WebViewFeature.isFeatureSupported(
                            WebViewFeature.WEB_MESSAGE_LISTENER
                        )
                        if (listenerSupported) {
                            // Inject local JavaScript that calls Credential Manager.
                            hookWebAuthnWithListener(
                                this, this@WebViewMainActivity,
                                coroutineScope, credentialManagerHandler
                            )
                        } else {
                            // Fallback routine for unsupported API levels.
                        }
                        loadUrl(url)
                    }
                }
            )
        }
        // [END android_identity_initialize_the_webview]
    }

    /**
     * Connects the local app logic with the web page via injection of javascript through a
     * WebListener. Handles ensuring the [PasskeyWebListener] is hooked up to the webView page
     * if compatible.
     */
    fun hookWebAuthnWithListener(
        webView: WebView,
        activity: WebViewMainActivity,
        coroutineScope: CoroutineScope,
        credentialManagerHandler: CredentialManagerHandler
    ) {
        // [START android_identity_create_webview_object]
        val passkeyWebListener = PasskeyWebListener(activity, coroutineScope, credentialManagerHandler)

        val webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webView.evaluateJavascript(PasskeyWebListener.INJECTED_VAL, null)
            }
        }

        webView.webViewClient = webViewClient
        // [END android_identity_create_webview_object]

        // [START android_identity_set_web]
        val rules = setOf("*")
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            WebViewCompat.addWebMessageListener(
                webView, PasskeyWebListener.INTERFACE_NAME,
                rules, passkeyWebListener
            )
        }
        // [END android_identity_set_web]
    }
}
