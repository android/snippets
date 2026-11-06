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

package com.example.snippets.security.privacy

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

private fun flagSensitiveClipboardData(clipData: ClipData) {
    // [START android_security_privacy_clipboard_flag_sensitive]
    // If your app is compiled with the API level 33 SDK or higher.
    clipData.apply {
        description.extras = PersistableBundle().apply {
            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        }
    }

    // If your app is compiled with API level 32 SDK or lower.
    clipData.apply {
        description.extras = PersistableBundle().apply {
            putBoolean("android.content.extra.IS_SENSITIVE", true)
        }
    }
    // [END android_security_privacy_clipboard_flag_sensitive]
}

private fun Context.scheduleClipboardClear(backgroundExecutor: ScheduledExecutorService) {
    // [START android_security_privacy_clipboard_clear_scheduled]
    // The Executor makes this task Asynchronous so that the UI continues being responsive.
    backgroundExecutor.schedule({
        // Creates a clip object with the content of the Clipboard.
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        // If SDK version is higher or equal to 28, it deletes Clipboard data with clearPrimaryClip().
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboard.clearPrimaryClip()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // If SDK version is lower than 28, it will replace Clipboard content with an empty value.
            val newEmptyClip = ClipData.newPlainText("EmptyClipContent", "")
            clipboard.setPrimaryClip(newEmptyClip)
        }
        // The delay after which the Clipboard is cleared, measured in seconds.
    }, 5, TimeUnit.SECONDS)
    // [END android_security_privacy_clipboard_clear_scheduled]
}
