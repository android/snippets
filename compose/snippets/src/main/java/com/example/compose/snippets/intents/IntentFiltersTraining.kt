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

package com.example.compose.snippets.intents

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.compose.snippets.R

private const val RESULT_COLOR_RED = 1

private class ShareActivity : Activity() {
    // [START android_intents_filters_training_handle_intent]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_example)

        val data: Uri? = intent?.data

        // Figure out what to do based on the intent type.
        if (intent?.type?.startsWith("image/") == true) {
            // Handle intents with image data.
        } else if (intent?.type == "text/plain") {
            // Handle intents with text.
        }
    }
    // [END android_intents_filters_training_handle_intent]

    fun returnResultWithData() {
        // [START android_intents_filters_training_return_result]
        // Create intent to deliver some kind of result data.
        Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri")).also { result ->
            setResult(Activity.RESULT_OK, result)
        }
        finish()
        // [END android_intents_filters_training_return_result]
    }

    fun returnResultCodeOnly() {
        // [START android_intents_filters_training_return_result_code]
        setResult(RESULT_COLOR_RED)
        finish()
        // [END android_intents_filters_training_return_result_code]
    }
}
