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

package com.example.compose.snippets.webview

import android.Manifest
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.snippets.R

// [START android_compose_webview_simple]
@Composable
fun SimpleWebView(
    initialUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(initialUrl)			
            }
        }
    )
}
// [END android_compose_webview_simple]

// [START android_compose_webview_persist]
@Composable
fun PersistentWebView(url: String) {
    val webViewStateBundle = rememberSaveable { Bundle() }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true

                // Restore the state and history
                if (webViewStateBundle.containsKey("WEBVIEW_STATE")) {
                    restoreState(webViewStateBundle.getBundle("WEBVIEW_STATE")!!)
                } else {
                    loadUrl(url)
                }
            }
        },
        onRelease = { releasedWebView ->
            // Save navigation history before the instance is destroyed
            val bundle = Bundle()
            releasedWebView.saveState(bundle)
            webViewStateBundle.putBundle("WEBVIEW_STATE", bundle)
        },
        modifier = Modifier.fillMaxSize()
    )
}
// [END android_compose_webview_persist]

// [START android_compose_webview_back_navigation]
// [START_EXCLUDE]
@OptIn(ExperimentalMaterial3Api::class)
// [END_EXCLUDE]
@Composable
fun BackNavigationDemoScreen(onBack: () -> Unit) {
    // 1. Hold a reference to the WebView to check its history state
    var webViewReference by remember { mutableStateOf<WebView?>(null) }
    
    // 2. Track if the WebView can go back to update BackHandler's enabled state
    var canGoBack by remember { mutableStateOf(false) }

    // 3. Intercept the system back press if the WebView has history
    BackHandler(enabled = canGoBack) {
        webViewReference?.goBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back Navigation Demo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Visual indicator of history state
                    Badge(containerColor = if (canGoBack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
                        Text(if (canGoBack) "Can Go Back" else "No History", modifier = Modifier.padding(4.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        
                        // Mock navigation for demonstration
                        webViewClient = object : WebViewClient() {
                            
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Update history state after every page load
                                canGoBack = view?.canGoBack() ?: false
                            }
                        }

                        // Start at page 1
                        loadUrl("https://developer.android.com")
                        webViewReference = this
                    }
                },
                onRelease = {
                    webViewReference = null
                }
            )
        }
    }
}
// [END android_compose_webview_back_navigation]

// [START android_compose_webview_window_insets]
@Composable
fun EdgeToEdgeDemo(url: String) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        factory = { context ->
            WebView(context).apply {
                loadUrl(url)
            }
        }
    )
}
// [END android_compose_webview_window_insets]

// [START android_compose_webview_theme_sync]
@Composable
fun ThemeSyncDemo(onBack: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()


    val themedContext = remember(isDark) {
        ContextThemeWrapper(
            context,
            if (isDark) R.style.Theme_Webviewdemo_Dark else R.style.Theme_Webviewdemo_Light
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Wrap in key(isDark) to force the AndroidView's factory to re-run.
        // This ensures native Context-bound UI matches the current theme, as the factory block only runs once.
        key(isDark) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { _ ->
                    WebView(themedContext).apply {
                        webViewClient = WebViewClient()

                        val html = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    // [START_EXCLUDE]
                                    <style>
                                        body {
                                            background-color: #f0f0f0;
                                            color: #333;
                                            font-family: sans-serif;
                                            padding: 30px;
                                            transition: all 0.5s ease;
                                        }
                                        .status-box {
                                            border: 2px dashed #999;
                                            padding: 20px;
                                            text-align: center;
                                        }
                                        // [END_EXCLUDE]
                                        
                                        @media (prefers-color-scheme: dark) {
                                            body {
                                                background-color: #1a1a1a;
                                                color: #ffffff;
                                            }
                                            .status-box {
                                                border-color: #BB86FC;
                                                background-color: #2d2d2d;
                                            }
                                        }
                                    </style>
                                </head>
                                // [START_EXCLUDE]
                                <body>
                                    <div class="status-box">
                                        <h3>XML Context Sync</h3>
                                        <p>If this background is dark, the <b>ContextThemeWrapper</b> successfully signaled 'isLightTheme=false' to the engine.</p>
                                        <p><b>JS Injection:</b> None</p>
                                        <p><b>Algorithmic Darkening:</b> Disabled</p>
                                    </div>
                                </body>
                                // [END_EXCLUDE]
                                </html>
                            """.trimIndent()
                        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
}
// [END android_compose_webview_theme_sync]

// [START android_compose_webview_permissions_handler]
class WebViewPermissionHandler(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    var pendingRequest by mutableStateOf<PermissionRequest?>(null)
        private set

    fun handleRequest(request: PermissionRequest) {
        val androidPermissions = mutableListOf<String>()
        request.resources.forEach { resource ->
            when (resource) {
                PermissionRequest.RESOURCE_VIDEO_CAPTURE -> androidPermissions.add(Manifest.permission.CAMERA)
                PermissionRequest.RESOURCE_AUDIO_CAPTURE -> androidPermissions.add(Manifest.permission.RECORD_AUDIO)
            }
        }
        
        // Save the request and launch the Android system dialog
        pendingRequest = request
        launcher.launch(androidPermissions.toTypedArray())
    }

    fun onResult(results: Map<String, Boolean>) {
        val allGranted = results.values.all { it }

        val isTrustedOrigin = pendingRequest?.origin?.host == "www.trusted-domain.com" // Always verify the origin before granting request

        if (allGranted && isTrustedOrigin) {
            pendingRequest?.grant(arrayOf("/* list of permissions */"))
        } else {
            pendingRequest?.deny()
        }
        pendingRequest = null
    }
}
// [END android_compose_webview_permissions_handler]

// [START android_compose_webview_permissions_remember]
@Composable
fun rememberWebViewPermissionHandler(): WebViewPermissionHandler {
    val handlerState = remember { mutableStateOf<WebViewPermissionHandler?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        handlerState.value?.onResult(results)
    }
    return remember {
        WebViewPermissionHandler(launcher).also { handlerState.value = it }
    }
}
// [END android_compose_webview_permissions_remember]

// [START android_compose_webview_permissions_screen]
@Composable
fun WebViewPermissionScreen() {
    val permissionHandler = rememberWebViewPermissionHandler()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                
                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        // Simply delegate to the handler
                        permissionHandler.handleRequest(request)
                    }
                }
                
		   // load a web page that needs permissions
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
// [END android_compose_webview_permissions_screen]
