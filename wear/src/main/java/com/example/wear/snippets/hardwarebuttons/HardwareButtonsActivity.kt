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

package com.example.wear.snippets.hardwarebuttons

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.wear.input.WearableButtons

class HardwareButtonsActivity : ComponentActivity() {
    // [START android_wear_hardware_buttons_events]
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (event?.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    Log.d(TAG, "KEYCODE_STEM_1 pressed")
                    true
                }
                KeyEvent.KEYCODE_STEM_2 -> {
                    Log.d(TAG, "KEYCODE_STEM_2 pressed")
                    true
                }
                else -> {
                    super.onKeyDown(keyCode, event)
                }
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
    // [END android_wear_hardware_buttons_events]

    fun hardwareButtonsCount(context: Context, activity: Activity) {
        // [START android_wear_hardware_buttons_count]
        val count = WearableButtons.getButtonCount(context)

        if (count > 1) {
            Log.d(TAG, "More than one button available")
        }

        val buttonInfo = WearableButtons.getButtonInfo(
            activity,
            KeyEvent.KEYCODE_STEM_1
        )

        if (buttonInfo == null) {
            // KEYCODE_STEM_1 is unavailable
            Log.d(TAG, "KEYCODE_STEM_1 not available")
        } else {
            // KEYCODE_STEM_1 is present on the device
            Log.d(TAG, "KEYCODE_STEM_1 is present on the device")
        }
        // [END android_wear_hardware_buttons_count]
    }
}
private const val TAG = "HardwareButtons"
