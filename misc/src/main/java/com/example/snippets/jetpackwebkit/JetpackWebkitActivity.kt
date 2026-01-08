package com.example.snippets.jetpackwebkit

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the user interface from a layout resource.
        setContentView(R.layout.activity_main)

        // You still get your WebView instance the standard way.
        // [START android_views_notifications_build_basic]

        // import android.webkit.WebView
        // import androidx.webkit.WebSettingsCompat
        // import androidx.webkit.WebViewFeature

        val webView: WebView = findViewById(R.id.my_webview)

        // This is necessary to keep navigation inside your WebView
        webView.webViewClient = WebViewClient()

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // To enable a modern feature, you pass that instance to a Jetpack Webkit helper.
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON)
        }
        // [END android_views_notifications_build_basic]

        // Load a URL
        webView.loadUrl("https://developer.android.com")
    }
}