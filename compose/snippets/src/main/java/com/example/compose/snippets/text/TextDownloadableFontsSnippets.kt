/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused", "PreviewMustBeTopLevelFunction")

package com.example.compose.snippets.text

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.isAvailableOnDevice
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.text.DownloadableFontsDebugSnippet.handler
import com.example.compose.snippets.text.TextDownloadableFontsSnippet1.provider
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * This file lets DevRel track changes to snippets present in
 * https://developer.android.com/jetpack/compose/text
 *
 * No action required if it's modified.
 */

private object TextDownloadableFontsSnippet1 {
    // [START android_compose_text_df_provider]
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    // [END android_compose_text_df_provider]
}

private object TextDownloadableFontsSnippet2 {
    // [START android_compose_text_df_fontFamily]

    // [START_EXCLUDE]
    /**
     // [END_EXCLUDE]
     import androidx.compose.ui.text.googlefonts.GoogleFont
     import androidx.compose.ui.text.font.FontFamily
     import androidx.compose.ui.text.googlefonts.Font
     // [START_EXCLUDE]
     **/
    // [END_EXCLUDE]

    val fontName = GoogleFont("Lobster Two")

    val fontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = provider)
    )
    // [END android_compose_text_df_fontFamily]
}

private object TextDownloadableFontsSnippet3 {
    // [START android_compose_text_df_fontFamily_style]

    // [START_EXCLUDE]
    /**
     // [END_EXCLUDE]
     import androidx.compose.ui.text.googlefonts.GoogleFont
     import androidx.compose.ui.text.font.FontFamily
     import androidx.compose.ui.text.googlefonts.Font
     // [START_EXCLUDE]
     **/
    // [END_EXCLUDE]

    val fontName = GoogleFont("Lobster Two")

    val fontFamily = FontFamily(
        Font(
            googleFont = fontName,
            fontProvider = provider,
            weight = FontWeight.Bold,
            style = FontStyle.Italic
        )
    )
    // [END android_compose_text_df_fontFamily_style]
}

@Composable
fun DownloadableFontsText() {
    // [START android_compose_text_df_fontFamily_usage]
    Text(
        fontFamily = fontFamily, text = "Hello World!"
    )
    // [END android_compose_text_df_fontFamily_usage]
}

private object TextDownloadableFontsSnippet4 {
    // [START android_compose_text_typography_definition]
    val MyTypography = Typography(
        bodyMedium = TextStyle(
            fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp/*...*/
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            /*...*/
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily, fontWeight = FontWeight.SemiBold/*...*/
        ),
        /*...*/
    )
    // [END android_compose_text_typography_definition]

    @Composable
    fun MyAppTheme(typography: Typography) {
    }

    @Composable
    fun MyApp() {
        // [START android_compose_text_app_typography]
        MyAppTheme(
            typography = MyTypography
        )/*...*/
        // [END android_compose_text_app_typography]
    }
}

private object FallbackFontsSnippet1 {
    // [START android_compose_text_df_fallbacks]

    // [START_EXCLUDE]
    /**
     // [END_EXCLUDE]
     import androidx.compose.ui.text.googlefonts.Font
     // [START_EXCLUDE]
     **/
    // [END_EXCLUDE]

    val fontName = GoogleFont("Lobster Two")

    val fontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = provider),
        Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Bold)
    )
    // [END android_compose_text_df_fallbacks]
}

private object FallbackFontsSnippet2 {
    // [START android_compose_text_df_fallbacks_style]

    // [START_EXCLUDE]
    /**
     // [END_EXCLUDE]
     import androidx.compose.ui.text.font.Font
     import androidx.compose.ui.text.googlefonts.Font
     // [START_EXCLUDE]
     **/
    // [END_EXCLUDE]

    val fontName = GoogleFont("Lobster Two")

    val fontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = provider),
        Font(resId = R.font.my_font_regular),
        Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Bold),
        Font(resId = R.font.my_font_regular_bold, weight = FontWeight.Bold)
    )
    // [END android_compose_text_df_fallbacks_style]
}

private object DownloadableFontsDebugSnippet {
    // [START android_compose_text_df_debug_apis_handler]
    val handler = CoroutineExceptionHandler { _, throwable ->
        // process the Throwable
        Log.e(TAG, "There has been an issue: ", throwable)
    }
    // [END android_compose_text_df_debug_apis_handler]
}

private object DownloadableFontsDebugSnippet2 {
    @Composable
    fun DownloadableFontsDebugSnippet() {
        // [START android_compose_text_df_debug_apis_handler_setup]
        CompositionLocalProvider(
            LocalFontFamilyResolver provides createFontFamilyResolver(LocalContext.current, handler)
        ) {
            Column {
                Text(
                    text = "Hello World!", style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        // [END android_compose_text_df_debug_apis_handler_setup]
    }
}

private object DownloadableFontsDebugSnippet3 {
    @Composable
    fun DownloadableFontsDebugSnippet() {
        // [START android_compose_text_df_debug_apis]
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            if (provider.isAvailableOnDevice(context)) {
                Log.d(TAG, "Success!")
            }
        }
        // [END android_compose_text_df_debug_apis]
    }
}

private val fontFamily = FontFamily()
private const val TAG = ""
