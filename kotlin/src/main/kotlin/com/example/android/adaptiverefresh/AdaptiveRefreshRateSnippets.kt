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

package com.example.android.adaptiverefresh

import android.view.Window
import androidx.annotation.RequiresApi

object AdaptiveRefreshRateSnippets {

    @RequiresApi(36)
    fun manageTouchBoost(window: Window) {
        // TODO: Replace with actual implementation when API is available
        fun setFrameRateBoostOnTouchEnabled(window: Window, enabled: Boolean) {
            // Placeholder implementation
        }
        fun isFrameRateBoostOnTouchEnabled(window: Window): Boolean {
            // Placeholder implementation
            return false
        }
        // [START android_kotlin_window_disable_touch_boost]
        // disable touch boost on a Window
        setFrameRateBoostOnTouchEnabled(window, false)
        // [END android_kotlin_window_disable_touch_boost]

        // [START android_kotlin_window_enable_touch_boost]
        // enable touch boost on a Window
        setFrameRateBoostOnTouchEnabled(window, true)
        // [END android_kotlin_window_enable_touch_boost]

        // [START android_kotlin_window_check_touch_boost]
        // check if touch boost is enabled on a Window
        val isTouchBoostEnabled = isFrameRateBoostOnTouchEnabled(window)
        // [END android_kotlin_window_check_touch_boost]
    }
}
