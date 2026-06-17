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

package com.example.compose.snippets.adaptivelayouts

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity

class SupportConnectedDisplays : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun getDisplayInfoSnippet() {
        // [START android_compose_layouts_connected_displays_get_info]
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = displayManager.getDisplays()
        // The default display is 0. External displays have other IDs.
        val externalDisplays = displays.filter { it.displayId != Display.DEFAULT_DISPLAY }
        // [END android_compose_layouts_connected_displays_get_info]
    }

    private fun launchActivitySnippet() {
        // [START android_compose_layouts_connected_displays_launch_activity]
        // Get DisplayManager and find the first external display.
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val externalDisplayId = displayManager.displays
            .firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }
            ?.displayId

        // If an external display is found, launch the activity on it.
        if (externalDisplayId != null) {
            val intent = Intent(this, MySecondaryActivity::class.java)
            val options = ActivityOptions.makeBasic()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                options.launchDisplayId = externalDisplayId
            }
            startActivity(intent, options.toBundle())
        } else {
            // Optionally, handle the case where no external display is connected.
        }
        // [END android_compose_layouts_connected_displays_launch_activity]
    }
}

class MySecondaryActivity : ComponentActivity()
