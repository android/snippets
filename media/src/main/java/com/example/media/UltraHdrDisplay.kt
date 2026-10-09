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

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.media.databinding.FragmentUltraHdrDisplayBinding

private class UltraHdrDisplayFragment : Fragment() {

    private lateinit var binding: FragmentUltraHdrDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUltraHdrDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun displayUltraHdr(imageResId: Int) {
        // [START android_media_ultra_hdr_display_window_color_mode]
        val bitmap = BitmapFactory.decodeResource(resources, imageResId)
        binding.imageContainer.setImageBitmap(bitmap)

        // Set color mode of the activity to the correct color mode.
        requireActivity().window.colorMode =
            if (bitmap.hasGainmap()) ActivityInfo.COLOR_MODE_HDR else ActivityInfo.COLOR_MODE_DEFAULT
        // [END android_media_ultra_hdr_display_window_color_mode]
    }
}
