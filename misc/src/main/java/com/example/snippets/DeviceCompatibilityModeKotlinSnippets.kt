/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.snippets

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.window.layout.WindowMetricsCalculator

class DeviceCompatibilityModeKotlinSnippets {

    @RequiresApi(Build.VERSION_CODES.N)
    // [START android_device_compatibility_mode_isLetterboxed_kotlin]
    fun isLetterboxed(activity: AppCompatActivity): Boolean {
        if (isInMultiWindowMode) return false

        val wmc = WindowMetricsCalculator.getOrCreate()
        val currentBounds = wmc.computeCurrentWindowMetrics(this).bounds
        val maxBounds = wmc.computeMaximumWindowMetrics(this).bounds

        val isScreenPortrait = maxBounds.height() > maxBounds.width()

        return if (isScreenPortrait) {
            currentBounds.height() < maxBounds.height()
        } else {
            currentBounds.width() < maxBounds.width()
        }
    }
    // [END android_device_compatibility_mode_isLetterboxed_kotlin]
}
