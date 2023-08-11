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

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R

private object VariableFontSnippet1 {
    // [START android_compose_text_variable_fonts_define]
    // In Typography.kt
    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily =
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(950),
                    FontVariation.width(30f),
                    FontVariation.slant(-6f),
                )
            )
        )
    // [END android_compose_text_variable_fonts_define]
}
private object VariableFontDefaultSnippet2 {
    // [START android_compose_text_variable_fonts_fallback]
    // In Typography.kt
    val default = FontFamily(
        /*
        * This can be any font that makes sense
        */
        Font(
            R.font.robotoflex_static_regular
        )
    )
    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(950),
                    FontVariation.width(30f),
                    FontVariation.slant(-6f),
                )
            )
        )
    } else {
        default
    }
    // [END android_compose_text_variable_fonts_fallback]
}

private object VariableFontSnippetsExtractedSettings {
    // In Typography.kt
    val default = FontFamily(
        /*
        * This can be any font that makes sense
        */
        Font(
            R.font.robotoflex_static_regular
        )
    )
    // [START android_compose_text_variable_fonts_extracted_settings]
    // VariableFontDimension.kt
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 30f
        const val SLANT = -6f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(DisplayLargeVFConfig.SLANT),
                )
            )
        )
    } else {
        default
    }
    // [END android_compose_text_variable_fonts_extracted_settings]
}
private object VariableFontSnippetsUseTheme {
    // In Typography.kt
    val default = FontFamily(
        /*
        * This can be any font that makes sense
        */
        Font(
            R.font.robotoflex_static_regular
        )
    )
    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.SLANT),
                )
            )
        )
    } else {
        default
    }
    // [START android_compose_text_variable_fonts_typography]
    // Type.kt
    val Typography = Typography(
        displayLarge = TextStyle(
            fontFamily = displayLargeFontFamily,
            fontSize = 50.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp,
            /***/
        )
    )
    // [END android_compose_text_variable_fonts_typography]
}

private object VariableFontsTypographyTheme {
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 30f
        const val SLANT = -6f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    object HeadlineMediumVFConfig {
        const val WEIGHT = 800
        const val WIDTH = 90f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 750f
        const val COUNTER_WIDTH = 393
    }

    object BodyLargeVFConfig {
        const val WEIGHT = 400
        const val WIDTH = 50f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 750f
        const val COUNTER_WIDTH = 603
    }
    // In Typography.kt
    val default = FontFamily(
        /*
        * This can be any font that makes sense
        */
        Font(
            R.font.robotoflex_static_regular
        )
    )
    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(VariableFontSnippetsExtractedSettings.DisplayLargeVFConfig.SLANT),
                )
            )
        )
    } else {
        default
    }
    @OptIn(ExperimentalTextApi::class)
    val headlineMediumFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(HeadlineMediumVFConfig.WEIGHT),
                    FontVariation.width(HeadlineMediumVFConfig.WIDTH),
                    FontVariation.slant(HeadlineMediumVFConfig.SLANT)
                )
            )
        )
    } else {
        default
    }

    @OptIn(ExperimentalTextApi::class)
    val bodyLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(BodyLargeVFConfig.WEIGHT),
                    FontVariation.width(BodyLargeVFConfig.WIDTH),
                    FontVariation.slant(BodyLargeVFConfig.SLANT)
                )
            )
        )
    } else {
        FontFamily(
            Font(
                R.font.robotoflex_static_regular
            )
        )
    }
    // Type.kt
    val Typography = Typography(
        displayLarge = TextStyle(
            fontFamily = displayLargeFontFamily,
            fontSize = 50.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp,
            /***/
        ),
        headlineMedium = TextStyle(
            fontFamily = headlineMediumFontFamily,
            fontSize = 35.sp,
            lineHeight = 37.sp
            /***/
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyLargeFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            letterSpacing = 0.15.sp
            /***/
        ),
    )

    @Composable
    fun MyCustomTheme(
        content: @Composable () -> Unit,
    ) {
        // [START android_compose_variable_fonts_use_theme]
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = Typography,
            content = content
        )
        // [END android_compose_variable_fonts_use_theme]
    }

    // [START android_compose_variable_fonts_usage]
    @Composable
    @Preview
    fun CardDetails() {
        MyCustomTheme {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Compose",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 1
                    )
                    Text(
                        text = "Beautiful UIs on Android",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 2
                    )
                    Text(
                        text = "Jetpack Compose is Android’s recommended modern toolkit for building native UI. It simplifies and accelerates UI development on Android. Quickly bring your app to life with less code, powerful tools, and intuitive Kotlin APIs.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 3
                    )
                }
            }
        }
    }
    // [END android_compose_variable_fonts_usage]
}

private object VariableFontsTypographyCustomAxes {
    // [START android_compose_variable_font_custom_axes]
    fun ascenderHeight(ascenderHeight: Float): FontVariation.Setting {
        require(ascenderHeight in 649f..854f) { "'Ascender Height' must be in 649f..854f" }
        return FontVariation.Setting("YTAS", ascenderHeight)
    }

    fun counterWidth(counterWidth: Int): FontVariation.Setting {
        require(counterWidth in 323..603) { "'Counter width' must be in 323..603" }
        return FontVariation.Setting("XTRA", counterWidth.toFloat())
    }
    // [END android_compose_variable_font_custom_axes]
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 30f
        const val SLANT = -6f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    object HeadlineMediumVFConfig {
        const val WEIGHT = 800
        const val WIDTH = 90f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 750f
        const val COUNTER_WIDTH = 393
    }

    object BodyLargeVFConfig {
        const val WEIGHT = 400
        const val WIDTH = 50f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 750f
        const val COUNTER_WIDTH = 603
    }
    // In Typography.kt
    val default = FontFamily(
        /*
        * This can be any font that makes sense
        */
        Font(
            R.font.robotoflex_static_regular
        )
    )
    // [START android_compose_variable_font_custom_axis_usage]
    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(DisplayLargeVFConfig.SLANT),
                    ascenderHeight(DisplayLargeVFConfig.ASCENDER_HEIGHT),
                    counterWidth(DisplayLargeVFConfig.COUNTER_WIDTH)
                )
            )
        )
    } else {
        default
    }
    // [END android_compose_variable_font_custom_axis_usage]

    @OptIn(ExperimentalTextApi::class)
    val headlineMediumFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(HeadlineMediumVFConfig.WEIGHT),
                    FontVariation.width(HeadlineMediumVFConfig.WIDTH),
                    FontVariation.slant(HeadlineMediumVFConfig.SLANT),
                    ascenderHeight(HeadlineMediumVFConfig.ASCENDER_HEIGHT),
                    counterWidth(HeadlineMediumVFConfig.COUNTER_WIDTH)
                )
            )
        )
    } else {
        default
    }

    @OptIn(ExperimentalTextApi::class)
    val bodyLargeFontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            Font(
                R.font.robotoflex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(BodyLargeVFConfig.WEIGHT),
                    FontVariation.width(BodyLargeVFConfig.WIDTH),
                    FontVariation.slant(BodyLargeVFConfig.SLANT),
                    ascenderHeight(BodyLargeVFConfig.ASCENDER_HEIGHT),
                    counterWidth(BodyLargeVFConfig.COUNTER_WIDTH)
                )
            )
        )
    } else {
        FontFamily(
            Font(
                R.font.robotoflex_static_regular
            )
        )
    }
    // Type.kt
    val Typography = Typography(
        displayLarge = TextStyle(
            fontFamily = displayLargeFontFamily,
            fontSize = 50.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp,
            /***/
        ),
        headlineMedium = TextStyle(
            fontFamily = headlineMediumFontFamily,
            fontSize = 35.sp,
            lineHeight = 37.sp
            /***/
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyLargeFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            letterSpacing = 0.15.sp
            /***/
        ),
    )

    @Composable
    fun MyCustomTheme(
        content: @Composable () -> Unit,
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = Typography,
            content = content
        )
    }

    @Composable
    @Preview
    fun CardDetails() {
        MyCustomTheme {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Compose",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 1
                    )
                    Text(
                        text = "Beautiful UIs on Android.",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 2
                    )
                    Text(
                        text = "Jetpack Compose is Android’s recommended modern toolkit for building native UI. It simplifies and accelerates UI development on Android. Quickly bring your app to life with less code, powerful tools, and intuitive Kotlin APIs.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 3
                    )
                }
            }
        }
    }
}
@Composable
@Preview
fun VariableFontConfigured() {
    VariableFontsTypographyTheme.CardDetails()
}
@Composable
@Preview
fun VariableFontConfiguredCustomAxes() {
    VariableFontsTypographyCustomAxes.CardDetails()
}
