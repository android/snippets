/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused", "PreviewMustBeTopLevelFunction")

package com.example.compose.snippets.text

import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.text.TextDownloadableFontsSnippet1.provider

/**
 * This file lets DevRel track changes to snippets present in
 * https://developer.android.com/jetpack/compose/text
 *
 * No action required if it's modified.
 */

private object TextDownloadableFontsSnippet1 {
    @OptIn(ExperimentalTextApi::class)
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
}

@OptIn(ExperimentalTextApi::class)
private object TextDownloadableFontsSnippet2 {
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

}


@OptIn(ExperimentalTextApi::class)
private object TextDownloadableFontsSnippet3 {
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
            googleFont = fontName, fontProvider = provider,
            weight = FontWeight.Bold, style = FontStyle.Italic
        )
    )

}

@Composable
fun DownloadableFontsText() {
    Text(
        fontFamily = fontFamily,
        text = "Hello World!"
    )
}

private object TextDownloadableFontsSnippet4 {
    val MyTypography = Typography(
        body1 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
            /*...*/
        ),
        body2 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            /*...*/
        ),
        h4 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold
            /*...*/
        ),
        /*...*/
    )

    @Composable
    fun MyAppTheme(typography: Typography) {
    }

    @Composable
    fun MyApp() {
        MyAppTheme(
            typography = MyTypography
        )
        /*...*/
    }

}

@OptIn(ExperimentalTextApi::class)
private object FallbackFontsSnippet1 {


}

@OptIn(ExperimentalTextApi::class)
private object FallbackFontsSnippet2 {


}


@OptIn(ExperimentalTextApi::class)
private object DownloadableFontsDebug1 {


}

@OptIn(ExperimentalTextApi::class)
private object DownloadableFontsDebug2 {


}

@OptIn(ExperimentalTextApi::class)
private object DownloadableFontsDebug3 {


}

val fontFamily = FontFamily()
