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

package com.example.compose.snippets.text

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Snippets for https://developer.android.com/develop/ui/compose/text/style-text.
 *
 * This file is also compiled into the interactive previews (preview/wasm), so it must only use
 * multiplatform Compose APIs.
 */

// [START android_compose_text_shadow]
@Composable
fun TextShadow() {
    val offset = Offset(5.0f, 10.0f)
    Text(
        text = "Hello world!",
        style = TextStyle(
            fontSize = 24.sp,
            shadow = Shadow(
                color = Color.Blue, offset = offset, blurRadius = 3f
            )
        )
    )
}
// [END android_compose_text_shadow]

val LightBlue = Color(0xFF0066FF)
val Purple = Color(0xFF800080)

@Composable
fun TextStyledBrushSnippet(text: String) {
    // [START android_compose_text_brush]
    val gradientColors = listOf(Cyan, LightBlue, Purple /*...*/)

    Text(
        text = text,
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = gradientColors
            )
        )
    )
    // [END android_compose_text_brush]
}

// Saturated colors, so the gradient and the alpha difference stay visible on a light surface.
private val RainbowColors = listOf(
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
)

@Composable
fun AnnotatedStringBrushSnippet() {
    val rainbowColors = RainbowColors
    // [START android_compose_text_annotatedstring_brush]
    Text(
        text = buildAnnotatedString {
            append("Do not allow people to dim your shine\n")
            withStyle(
                SpanStyle(
                    brush = Brush.linearGradient(
                        colors = rainbowColors
                    )
                )
            ) {
                append("because they are blinded.")
            }
            append("\nTell them to put some sunglasses on.")
        }
    )
    // [END android_compose_text_annotatedstring_brush]
}

@Composable
fun AlphaBrushSnippet() {
    val rainbowColors = RainbowColors
    val text = run {
        // [START android_compose_text_alpha_brush]
        val brush = Brush.linearGradient(colors = rainbowColors)

        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = brush, alpha = .5f
                )
            ) {
                append("Text in ")
            }
            withStyle(
                SpanStyle(
                    brush = brush, alpha = 1f
                )
            ) {
                append("Compose ❤️")
            }
        }
        // [END android_compose_text_alpha_brush]
    }
    Text(text, fontSize = 24.sp)
}

@Preview(showBackground = true)
// [START android_compose_text_marquee]
@Composable
fun BasicMarqueeSample() {
    // Marquee only animates when the content doesn't fit in the max width.
    Column(Modifier.width(400.dp)) {
        Text(
            "Learn about why it's great to use Jetpack Compose",
            modifier = Modifier.basicMarquee(),
            fontSize = 50.sp
        )
    }
}
// [END android_compose_text_marquee]
