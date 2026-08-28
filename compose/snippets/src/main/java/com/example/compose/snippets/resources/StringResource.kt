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

package com.example.compose.snippets.resources

import android.text.Annotation
import android.text.SpannedString
import android.text.TextUtils
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R

// [START android_resources_string_resource_greeting]
@Composable
fun Greeting() {
    Text(text = stringResource(R.string.hello))
}
// [END android_resources_string_resource_greeting]

// [START android_resources_string_resource_planet_list]
@Composable
fun PlanetList() {
    val planets: Array<String> =
        stringArrayResource(R.array.planets_array)
    // Render the array, e.g. inside a LazyColumn.
}
// [END android_resources_string_resource_planet_list]

// [START android_resources_string_resource_song_count]
@Composable
fun SongCount(count: Int) {
    Text(
        text = pluralStringResource(
            R.plurals.numberOfSongsAvailable,
            count,
            count,
        )
    )
}
// [END android_resources_string_resource_song_count]

// [START android_resources_string_resource_welcome_message]
@Composable
fun WelcomeMessage(username: String, mailCount: Int) {
    Text(
        text = stringResource(
            R.string.welcome_messages,
            username,
            mailCount,
        )
    )
}
// [END android_resources_string_resource_welcome_message]

// [START android_resources_string_resource_welcome_html_message]
@Composable
fun WelcomeHtmlMessage(username: String, mailCount: Int) {
    // Escape the username in case it contains characters like "<" or "&"
    val escapedUsername = TextUtils.htmlEncode(username)

    val text = stringResource(
        R.string.welcome_messages,
        escapedUsername,
        mailCount,
    )

    Text(
        text = AnnotatedString.fromHtml(text)
    )
}
// [END android_resources_string_resource_welcome_html_message]

// [START android_resources_string_resource_styled_greeting]
@Composable
fun StyledGreeting() {
    val styled = buildAnnotatedString {
        append("Welcome to ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Android")
        }
        append("!")
    }
    Text(text = styled)
}
// [END android_resources_string_resource_styled_greeting]

// [START android_resources_string_resource_rich_text]
@Composable
fun RichText() {
    val text = buildAnnotatedString {
        withStyle(ParagraphStyle(lineHeight = 24.sp, textAlign = TextAlign.Center)) {
            withStyle(SpanStyle(color = Color.Gray)) {
                append("Hello, ")
            }
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                )
            ) {
                append("world")
            }
            append("!")
        }
    }
    Text(text = text)
}
// [END android_resources_string_resource_rich_text]

// [START android_resources_string_resource_annotated_title]
@Composable
fun AnnotatedTitle() {
    val context = LocalContext.current
    val source = context.resources.getText(R.string.title) as SpannedString
    val text = buildAnnotatedString {
        append(source.toString())
        source.getSpans(0, source.length, Annotation::class.java)
            .forEach { annotation ->
                if (annotation.key == "font" &&
                    annotation.value == "title_emphasis") {
                    addStyle(
                        SpanStyle(
                            fontFamily = FontFamily(
                                Font(R.font.permanent_marker)
                            )
                        ),
                        source.getSpanStart(annotation),
                        source.getSpanEnd(annotation),
                    )
                }
            }
    }
    Text(text = text)
}
// [END android_resources_string_resource_annotated_title]
