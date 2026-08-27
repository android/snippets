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
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.Fragment

class UltraHdrDisplayFragment : Fragment() {

    private val binding = FragmentBinding()

    fun displayUltraHdr(bitmap: Bitmap) {
        // [START android_media_ultra_hdr_display_window_color_mode]
        val bitmap = /* Get Bitmap from Image Resource */
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        binding.imageContainer.setImageBitmap(bitmap)

        // Set color mode of the activity to the correct color mode.
        requireActivity().window.colorMode =
           if (bitmap.hasGainmap()) ActivityInfo.COLOR_MODE_HDR else ActivityInfo.COLOR_MODE_DEFAULT
        // [START_EXCLUDE silent]
        */
        bitmap
        binding.imageContainer.setImageBitmap(bitmap)
        requireActivity().window.colorMode =
           if (bitmap.hasGainmap()) ActivityInfo.COLOR_MODE_HDR else ActivityInfo.COLOR_MODE_DEFAULT
        // [END_EXCLUDE]
        // [END android_media_ultra_hdr_display_window_color_mode]
    }

    private class FragmentBinding {
        val imageContainer = ImageView(null)
    }
}
