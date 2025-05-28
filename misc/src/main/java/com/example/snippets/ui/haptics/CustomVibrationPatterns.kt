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

package com.example.snippets.ui.haptics

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

class CustomVibrationPatterns : Activity() {
    @RequiresApi(Build.VERSION_CODES.M)
    val vibrator = applicationContext.getSystemService(Vibrator::class.java) as Vibrator

    // [START android_ui_haptics_ramp_up]
    @RequiresPermission(Manifest.permission.VIBRATE)
    @RequiresApi(Build.VERSION_CODES.O)
    fun rampUpPattern(vibrator: Vibrator) {
        val timings: LongArray = longArrayOf(
            50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200
        )
        val amplitudes: IntArray = intArrayOf(
            33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255
        )
        val repeatIndex = -1 // Don't repeat.

        vibrator.vibrate(
            VibrationEffect.createWaveform(
                timings, amplitudes, repeatIndex
            )
        )
    }
    // [END android_ui_haptics_ramp_up]

    @RequiresPermission(Manifest.permission.VIBRATE)
    @RequiresApi(Build.VERSION_CODES.O)
    // [START android_ui_haptics_repeat]
    fun startRepeatVibration() {
        val timings: LongArray = longArrayOf(50, 50, 100, 50, 50)
        val amplitudes: IntArray = intArrayOf(64, 128, 255, 128, 64)
        val repeat = 1 // Repeat from the second entry, index = 1.
        val repeatingEffect = VibrationEffect.createWaveform(
            timings, amplitudes, repeat
        )
        // repeatingEffect can be used in multiple places.

        vibrator.vibrate(repeatingEffect)
    }

    // [START_EXCLUDE]
    @RequiresPermission(Manifest.permission.VIBRATE)
    @RequiresApi(Build.VERSION_CODES.M)
    // [END_EXCLUDE]
    fun stopRepeatVibrator() {
        vibrator.cancel()
    }
    // [END android_ui_haptics_repeat]

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_fallback]
    private fun patternWithFallback() {
        val smoothTimings = longArrayOf(50, 50, 100, 50, 50)
        val onOffTimings = longArrayOf(50, 100)
        val amplitudes = intArrayOf(64, 128, 255, 128, 64)
        val repeatIndex = -1 // Don't repeat.

        if (vibrator.hasAmplitudeControl()) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    smoothTimings, amplitudes, repeatIndex
                )
            )
        } else {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    onOffTimings, repeatIndex
                )
            )
        }
    }
    // [END android_ui_haptics_fallback]
}
