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

package com.example.compose.snippets.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.RemoteInput

// [START android_notification_reply_receiver]
class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dataResults = RemoteInput.getDataResultsFromIntent(intent, KEY_REPLY)
        val imageUri: Uri? = dataResults?.get("image/*") as? Uri

        if (imageUri != null) {
            // Extract the image
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                // Display the image
                // ...
            }
        }
    }

    companion object {
        const val KEY_REPLY = "key_reply"
        const val KEY_TEXT_REPLY = "key_text_reply"
    }
}
// [END android_notification_reply_receiver]
