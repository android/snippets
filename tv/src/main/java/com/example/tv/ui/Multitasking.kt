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

package com.example.tv.ui

import android.app.PictureInPictureParams
import android.content.pm.PackageManager.FEATURE_PICTURE_IN_PICTURE
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class MultitaskingFragment : Fragment() {

    private lateinit var pictureInPictureButton: Button

    // [START android_tv_multitasking_enter_pip]
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureInPictureButton.visibility =
            if (requireActivity().packageManager.hasSystemFeature(FEATURE_PICTURE_IN_PICTURE)) {
                pictureInPictureButton.setOnClickListener {
                    val aspectRatio = Rational(view.width, view.height)
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(aspectRatio)
                        .build()
                    val result = requireActivity().enterPictureInPictureMode(params)
                }
                View.VISIBLE
            } else {
                View.GONE
            }
    }
    // [END android_tv_multitasking_enter_pip]
}
