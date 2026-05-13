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

package com.example.compose.snippets.sharesheet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.service.chooser.ChooserResult
import android.util.Log
import androidx.core.content.IntentCompat

class ShareBroadcastReceiver : BroadcastReceiver() {
    // [START android_receiver_info_on_sharing]
    override fun onReceive(context: Context?, intent: Intent) {
        val TAG = ShareBroadcastReceiver::class.simpleName
        val chooserResult: ChooserResult? = IntentCompat.getParcelableExtra(
            intent,
            Intent.EXTRA_CHOOSER_RESULT,
            ChooserResult::class.java,
        )
        chooserResult?.let {
            Log.i(TAG,
                "Share callback: isShortcut: ${it.isShortcut}, type: ${typeToString(it.type)}, componentName: ${it.selectedComponent}",
            )
        } ?: Log.i(TAG, "chooserResult is null")
    }
    // [END android_receiver_info_on_sharing]

    private fun typeToString(type: Int): String {
        return ""
    }
}