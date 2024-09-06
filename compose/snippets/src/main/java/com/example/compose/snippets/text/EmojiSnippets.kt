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

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.emoji2.widget.EmojiTextView
import com.example.compose.snippets.databinding.ExampleViewBinding
import com.example.compose.snippets.text.EmojiUtils.EMOJI_TEXT

private object ModernEmojiComposeDisableSnippet {
    @Composable
    fun ModernEmojiComposeDisableSnippet() {
        // [START android_compose_text_emoji]
        Text(
            text = "Hello $EMOJI_TEXT",
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    emojiSupportMatch = EmojiSupportMatch.None
                )/* ... */
            )
        )
        // [END android_compose_text_emoji]
    }
}

private object EmojiSnippets2 {
    // [START android_compose_text_emoji_compatibility_component]
    class MainActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val emojiTextView: EmojiTextView = findViewById(R.id.emoji_text_view)
            emojiTextView.text = getString(R.string.emoji_text_view, EMOJI_TEXT)

            val composeView: ComposeView = findViewById(R.id.compose_view)

            composeView.apply {
                setContent {
                    // compose code
                }
            }
        }
    }
    // [END android_compose_text_emoji_compatibility_component]

    object R {
        object id {
            const val emoji_text_view = 1
            const val compose_view = 2
        }

        object layout {
            const val activity_main = 0
        }

        object string {
            const val emoji_text_view = 0
        }
    }
}

private object EmojiSnippets3 {

    // [START android_compose_text_emoji_compatibility_textview]
    class MyActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val emojiTextView: TextView = findViewById(R.id.emoji_text_view)
            emojiTextView.text = getString(R.string.emoji_text_view, EMOJI_TEXT)

            val composeView: ComposeView = findViewById(R.id.compose_view)

            composeView.apply {
                setContent {
                    // compose code
                }
            }
        }
    }
    // [END android_compose_text_emoji_compatibility_textview]

    object R {
        object id {
            const val emoji_text_view = 1
            const val compose_view = 2
        }

        object layout {
            const val activity_main = 0
        }

        object string {
            const val emoji_text_view = 0
        }
    }
}

private object EmojiSnippets4 {
    // [START android_compose_text_emoji_compatibility_viewbinding]
    class MyActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(
                ComposeView(this).apply {
                    setContent {
                        Column {
                            Text(EMOJI_TEXT)

                            AndroidViewBinding(ExampleViewBinding::inflate) {
                                emojiTextView.text = EMOJI_TEXT
                            }
                        }
                    }
                }
            )
        }
    }
    // [END android_compose_text_emoji_compatibility_viewbinding]
}

private object EmojiSnippets5 {
    // [START android_compose_text_emoji_compatibility_compat]
    class MyActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(
                ComposeView(this).apply {
                    setContent {
                        Column {
                            Text(EMOJI_TEXT)

                            AndroidView(
                                factory = { context -> AppCompatTextView(context) },
                                update = { it.text = EMOJI_TEXT }
                            )
                        }
                    }
                }
            )
        }
    }
    // [END android_compose_text_emoji_compatibility_compat]
}
