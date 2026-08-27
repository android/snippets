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

package com.example.media

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class MediaProjectionActivity : ComponentActivity() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun startProjection() {
        // [START android_media_projection_start]
        val mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        var mediaProjection : MediaProjection
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val startMediaProjection = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                mediaProjection = mediaProjectionManager
                    .getMediaProjection(result.resultCode, result.data!!)
            }
        }
        // [START_EXCLUDE silent]
        */
        val startMediaProjection = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mediaProjection = mediaProjectionManager
                    .getMediaProjection(result.resultCode, result.data!!)!!
            }
        }
        // [END_EXCLUDE]

        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent())
        // [END android_media_projection_start]
    }

    fun createVirtualDisplayExample(
        width: Int,
        height: Int,
        screenDensity: Int,
        surface: Surface
    ) {
        val proj = mediaProjection ?: return
        // [START android_media_projection_virtual_display]
        virtualDisplay = proj.createVirtualDisplay(
                             "ScreenCapture",
                             width,
                             height,
                             screenDensity,
                             DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                             surface,
                             null, null)
        // [END android_media_projection_virtual_display]
    }

    fun getWindowMetricsExample(context: Context) {
        // [START android_media_projection_window_context_metrics]
        val windowContext = context.createWindowContext(context.display!!,
              WindowManager.LayoutParams.TYPE_APPLICATION, null)
        val projectionMetrics = windowContext.getSystemService(WindowManager::class.java)
              .maximumWindowMetrics
        // [END android_media_projection_window_context_metrics]
    }
}
