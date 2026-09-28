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

import android.view.View
import android.webkit.WebView
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private fun overrideBoundsCheckingSnippet(myWebView: WebView) {
    // [START android_views_webapps_window_insets_override_bounds]
    ViewCompat.setOnApplyWindowInsetsListener(myWebView) { _, windowInsets ->
        // By returning the original windowInsets object, we override the default
        // behavior that zeroes out system insets (like system bars or display
        // cutouts) when they don't directly overlap the WebView's screen bounds.
        windowInsets
    }
    // [END android_views_webapps_window_insets_override_bounds]
}

private fun zeroingInsetsSnippet(rootView: View) {
    // [START android_views_webapps_window_insets_zeroing]
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
        // 1. Identify the inset types you want to handle natively
        val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

        // 2. Extract the dimensions and apply them as padding to the native container
        val insets = windowInsets.getInsets(types)
        view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

        // 3. Return a new WindowInsets object with the handled types set to NONE (zeroed).
        // This informs the WebView that these areas are already padded, preventing
        // double-padding while still allowing the WebView to update its internal state.
        WindowInsetsCompat.Builder(windowInsets)
            .setInsets(types, Insets.NONE)
            .build()
    }
    // [END android_views_webapps_window_insets_zeroing]
}
