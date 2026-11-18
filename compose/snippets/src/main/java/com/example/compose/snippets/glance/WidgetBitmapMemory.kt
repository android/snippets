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

package com.example.compose.snippets.glance

import android.content.Context
import android.os.Build
import android.view.WindowManager

// [START android_compose_glance_widget_memory_cap]
private const val FALLBACK_MAX_BYTES = 15_000_000L

fun calculateWidgetMemoryCapBytes(context: Context): Long {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val wm = context.getSystemService(WindowManager::class.java)
            ?: return FALLBACK_MAX_BYTES
        val bounds = wm.currentWindowMetrics.bounds
        return 6L * bounds.width().toLong() * bounds.height().toLong()
    }
    val dm = context.resources.displayMetrics
    return 6L * dm.widthPixels.toLong() * dm.heightPixels.toLong()
}
// [END android_compose_glance_widget_memory_cap]
