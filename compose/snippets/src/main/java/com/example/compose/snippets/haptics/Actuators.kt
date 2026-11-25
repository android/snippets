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

package com.example.compose.snippets.haptics

import android.os.VibrationEffect
import android.os.Vibrator

private lateinit var vibrator: Vibrator

private fun defaultWaveformSmoothing() {
    // [START android_haptics_actuators_waveform_default]
    val timings: LongArray = longArrayOf(50, 50, 50, 50, 50, 100, 350, 250)
    val amplitudes: IntArray = intArrayOf(77, 79, 84, 99, 143, 255, 0, 255)
    val repeatIndex = -1 // Don't repeat.

    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
    // [END android_haptics_actuators_waveform_default]
}

private fun improvedHapticPattern() {
    // [START android_haptics_actuators_waveform_improved]
    val timings: LongArray = longArrayOf(
        25, 25, 50, 25, 25, 25, 25, 25, 25, 25, 75, 25, 25,
        300, 25, 25, 150, 25, 25, 25
    )
    val amplitudes: IntArray = intArrayOf(
        38, 77, 79, 84, 92, 99, 121, 143, 180, 217, 255, 170, 85,
        0, 85, 170, 255, 170, 85, 0
    )
    val repeatIndex = -1 // Don't repeat.

    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
    // [END android_haptics_actuators_waveform_improved]
}
