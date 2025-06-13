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

@RequiresApi(api = Build.VERSION_CODES.S)
class CustomVibrationCompositions : Activity() {
    var vibrator: Vibrator = getApplicationContext().getSystemService(Vibrator::class.java)

    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_composed_vibration_effect]
    fun createComposedVibrationEffect() {
        vibrator.vibrate(
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                .compose()
        )
    }
    // [END android_ui_haptics_composed_vibration_effect]

    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_gap_between_primitives]
    fun gapBetweenPrimitives() {
        val delayMs: Int = 100
        vibrator.vibrate(
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.8f)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.6f)
                .addPrimitive(
                    VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, delayMs
                )
                .compose()
        )
    }
    // [END android_ui_haptics_gap_between_primitives]

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun checkPrimitivesSupport() {
        // [START android_ui_haptics_check_single_primitive_support]
        val primitive: Int = VibrationEffect.Composition.PRIMITIVE_LOW_TICK

        if (vibrator.areAllPrimitivesSupported(primitive)) {
            vibrator.vibrate(
                VibrationEffect.startComposition()
                    .addPrimitive(primitive).compose()
            )
        } else {
            // Play a predefined effect or custom pattern as a fallback.
        }
        // [END android_ui_haptics_check_single_primitive_support]

        // [START android_ui_haptics_check_multiple_primitives_support]
        val supported: BooleanArray = vibrator.arePrimitivesSupported(
            VibrationEffect.Composition.PRIMITIVE_LOW_TICK,
            VibrationEffect.Composition.PRIMITIVE_TICK,
            VibrationEffect.Composition.PRIMITIVE_CLICK
        )
        // [END android_ui_haptics_check_multiple_primitives_support]
    }
}
