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

import android.content.Context
import android.text.format.DateFormat
import java.util.Date
import java.util.Locale

private fun formatTimeForSkeleton(context: Context) {
    // [START android_resources_internationalization_skeleton]
    val skeleton: String = if (DateFormat.is24HourFormat(context)) "Hm" else "hm"
    val formattedTime: String = android.icu.text.DateFormat.getInstanceForSkeleton(
        skeleton,
        Locale.getDefault()
    ).format(
        Date()
    )
    // [END android_resources_internationalization_skeleton]
}
