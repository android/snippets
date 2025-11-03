package com.example.identity.credentialmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import android.webkit.WebView
import com.example.identity.WebViewClientImpl


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
