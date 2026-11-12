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

package com.example.tv.accessibility

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

private const val TAG = "Accessibility"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class AccessibilityActivity : ComponentActivity() {

    // [START android_tv_accessibility_audio_description]
    private lateinit var accessibilityManager: AccessibilityManager

    // [START_EXCLUDE silent]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        // In onCreate():
        accessibilityManager = getSystemService(AccessibilityManager::class.java)

        // Where your media player is initialized.
        if (accessibilityManager.isAudioDescriptionRequested) {
            // User has requested to enable audio descriptions.
        }
        // [END android_tv_accessibility_audio_description]
    }

    // [START android_tv_accessibility_audio_description_listener]
    private val listener =
        AccessibilityManager.AudioDescriptionRequestedChangeListener { enabled ->
            // Preference changed; reflect its state in your media player.
        }

    override fun onStart() {
        super.onStart()

        accessibilityManager.addAudioDescriptionRequestedChangeListener(mainExecutor, listener)
    }

    override fun onStop() {
        super.onStop()

        accessibilityManager.removeAudioDescriptionRequestedChangeListener(listener)
    }
    // [END android_tv_accessibility_audio_description_listener]

    private fun checkFontScale() {
        // [START android_tv_accessibility_font_scale]
        // Checking font scale with Context.
        val scale = resources.configuration.fontScale
        Log.d(TAG, "Text scale is: " + scale)
        // [END android_tv_accessibility_font_scale]
    }
}
