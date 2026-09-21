/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.preview.wasm.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import kotlin.js.ExperimentalWasmJsInterop
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.fetch.Response

private var cachedGoogleSansFontFamily: FontFamily? = null

@OptIn(ExperimentalWasmJsInterop::class)
private suspend fun loadFontBytes(url: String): ByteArray {
    val response = window.fetch(url).await<Response>()
    if (!response.ok) {
        throw IllegalStateException("Failed to load font from $url: HTTP ${response.status}")
    }
    val buffer = response.arrayBuffer().await<ArrayBuffer>()
    val i8 = Int8Array(buffer)
    return ByteArray(i8.length) { i8[it] }
}

private suspend fun loadGoogleSansFontFamily(): FontFamily {
    cachedGoogleSansFontFamily?.let { return it }

    val href = window.location.href
    val baseUrl = href.substringBefore("wasm.html").substringBefore("#").substringBefore("?")
    val cleanBase = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

    val regularBytes = loadFontBytes("${cleanBase}fonts/GoogleSans-Regular.ttf")
    val mediumBytes = loadFontBytes("${cleanBase}fonts/GoogleSans-Medium.ttf")
    val boldBytes = loadFontBytes("${cleanBase}fonts/GoogleSans-Bold.ttf")

    val family = FontFamily(
        Font(
            identity = "GoogleSans-Regular",
            data = regularBytes,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            identity = "GoogleSans-Medium",
            data = mediumBytes,
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            identity = "GoogleSans-Bold",
            data = boldBytes,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )

    cachedGoogleSansFontFamily = family
    return family
}

fun createGoogleSansTypography(fontFamily: FontFamily): Typography {
    val default = Typography()
    return Typography(
        displayLarge = default.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = default.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = default.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = default.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = default.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = default.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = default.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = default.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = default.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = default.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = default.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = default.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = default.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = default.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = default.labelSmall.copy(fontFamily = fontFamily)
    )
}

@Composable
fun rememberGoogleSansTypography(): Typography {
    var fontFamily by remember { mutableStateOf(cachedGoogleSansFontFamily) }

    LaunchedEffect(Unit) {
        if (fontFamily == null) {
            try {
                fontFamily = loadGoogleSansFontFamily()
            } catch (e: Throwable) {
                // Fallback to SansSerif if network or font fetch is unavailable
                fontFamily = FontFamily.SansSerif
            }
        }
    }

    return remember(fontFamily) {
        createGoogleSansTypography(fontFamily ?: FontFamily.SansSerif)
    }
}
