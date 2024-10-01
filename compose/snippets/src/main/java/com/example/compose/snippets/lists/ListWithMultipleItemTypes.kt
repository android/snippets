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

package com.example.compose.snippets.lists

import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.w3c.dom.Text

// [START android_compose_lists_multiple_item_types]
@Composable
fun ListWithMultipleItems(messages: List<Any>) {
    LazyColumn {
        items(
            messages.size,
            contentType = { it }
        ) {
            for (message in messages)
                when (message) {
                    is MediaStore.Audio -> AudioMessage(message)
                    is Text -> TextMessage(message)
                }
        }
    }
}

@Composable
fun AudioMessage(message: MediaStore.Audio) {
    TODO("Not yet implemented.")
}

@Composable
fun TextMessage(message: Text) {
    TODO("Not yet implemented.")
}

data class SampleMessage(val text: String, val content: Any)
// [END android_compose_lists_multiple_item_types]
